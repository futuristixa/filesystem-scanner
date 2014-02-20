/*
 * Copyright 2014 Roberto Fabrizi.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to:
 * Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *
 */
package com.rhad.filescanner;

import com.rhad.filescanner.persistence.EntityManagerFactoryUtil;
import com.rhad.filescanner.config.ConfigProperties;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * The main class / starting point of the application.
 * @author Roberto Fabrizi
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    
    /**
     * Starts the scan of the paths of interest.
     * @param args the command line arguments
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        ExecutorService threadPool = null;
        try {
            ConfigProperties configProperties = LoadConfigurationParameters();
            LOGGER.info("ConfigProperties loaded");
            LOGGER.info(configProperties);
            EntityManagerFactoryUtil.CreateEntityManagerFactoryUtil();
            LOGGER.info("EntityManagerFactoryUtil loaded");
            threadPool = Executors.newFixedThreadPool(configProperties.getThreads());
            LOGGER.info("ExecutorService created using "+configProperties.getThreads()+" concurrent threads");
            Collection<Future<?>> tasks = new LinkedList<Future<?>>();
            String[] pathsToParse = configProperties.getPathArray();
            if(pathsToParse!=null && pathsToParse.length>0){
                List<String> fileExtentionToMatchList = new LinkedList<String>();
                for(String fileExtentionToMatch : configProperties.getWantedFileExtentions()){
                    fileExtentionToMatchList.add(".+\\."+fileExtentionToMatch);
                }
                StringBuilder patternToUse = new StringBuilder("");
                for(int i=0; i<fileExtentionToMatchList.size();i++){
                    if(i==fileExtentionToMatchList.size()-1){
                        patternToUse.append(fileExtentionToMatchList.get(i));
                    } else {
                        patternToUse.append(fileExtentionToMatchList.get(i)).append("|");
                    }
                }
                Pattern pattern = null;
                if(!patternToUse.toString().equalsIgnoreCase("")){
                    pattern = Pattern.compile(patternToUse.toString());
                }
                for(int i=0; i<pathsToParse.length; i++){
                    File root = new File(pathsToParse[i]);
                    if (root.isDirectory()) {
                        File[] fileArray = root.listFiles();
                        for(File f : fileArray){
                            if(f.isDirectory()){
                                Runnable recurser = new Recurser(f.getName(), f, pattern, configProperties.isDebug());
                                Future<?> future = threadPool.submit(recurser);
                                tasks.add(future);
                            }
                        }
                        for (Future<?> f : tasks) {
                            //this method blocks until the async computation is finished
                            f.get(); 
                        }
                    } else {
                        LOGGER.warn("Not a directory: " + root);
                    }
                }
            } else {
                LOGGER.error("No path to scan");
            }
        } catch (Exception e){
            LOGGER.fatal("A fatal exception has occurred", e);
        } finally {
            // to avoid issues with c3p0 connection pooling wait a bit before shutting down
            Thread.sleep(10000L);
            if(threadPool!=null){
                threadPool.shutdown();
                LOGGER.info("No more threads are accepted, waiting 10 seconds before forcibly shutting the thread pool down...");
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    // Cancel currently executing tasks
                    threadPool.shutdownNow();
                }
                LOGGER.info("ExecutorService shutdown finished");
            }   
            EntityManagerFactoryUtil.Close();
            try {
            // it is fine to hold the lock for 5 seconds here as this is the shutdown method
            // reduced even more, shutdown hook should not take long
            Thread.sleep(2000L);
            } catch(InterruptedException ie) {}
            LOGGER.info("EntityManagerFactory shut down");        
        }       
    }
    
    /**
     * Loads the content of the classpath-exported <code>config.properties</code> file in a {@link com.rhad.filescanner.ConfigProperties} object and returns it. 
     * If the process fails, the application terminates.
     * @return a configProperties object that maps the <code>config.properties</code> classpath-exported file
     */
    private static ConfigProperties LoadConfigurationParameters() {
        LOGGER.trace("LoadConfigurationParameters called");
        File propertiesConfigFile;
        ConfigProperties configurationProperties = null;
        try {
            propertiesConfigFile = new File(Main.class.getClassLoader().getResource("config.properties").toURI());
            if(propertiesConfigFile.exists()){
                // if config.properties exists, parse it and load its content in a ConfigurationProperties object
                configurationProperties = new ConfigProperties(propertiesConfigFile);
            } else {
                throw new FileNotFoundException();
            }            
        } catch (URISyntaxException ex) {
            LOGGER.fatal("Fatal URISyntaxException: the URI of the file 'config.properties' is incorrect", ex);
            System.exit(0);
        } catch (IOException ex) {
            LOGGER.fatal("Fatal URISyntaxException: the URI of the file 'config.properties' is incorrect", ex);
            System.exit(0);
        }
        LOGGER.trace("LoadConfigurationParameters ended");
        return configurationProperties;
    }
}