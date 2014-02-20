/*
 * Copyright 2014 Roberto Fabrizi.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to:
 * Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *
 */
package com.rhad.filescanner.config;

import java.io.*;
import java.util.*;

/**
 * This class is a mapping of the <code>config.properties</code> file.
 * @author Roberto Fabrizi
 */
public final class ConfigProperties {

    private final Boolean debug;
    private int threads = Runtime.getRuntime().availableProcessors();
    private String[] pathArray;
    private String[] fileExtentionArray;

    /**
     * Creates a <code>ConfigProperties</code>, which is in charge of parsing the config.properties file and extract the
     * required informations from it.
     * @param configFile a File that points at the config.properties
     * @throws IOException if a {@link java.io.FileInputStream} from the config.properties file cannot be created or loaded
     * @throws IllegalArgumentException if the <code>ConfigProperties</code> couldn't be created because some mandatory fields are null or invalid
     */
    public ConfigProperties(File configFile) throws IOException {
        // create the Properties object, load the data into it, and close the stream
        FileInputStream propertiesFile = null;
        try {
            Properties defaultProps = new Properties();
            propertiesFile = new FileInputStream(configFile);
            defaultProps.load(propertiesFile);
            try {
                this.threads=Integer.parseInt(defaultProps.getProperty("threads"));
            } catch(NumberFormatException e) {
            }
            this.debug = Boolean.valueOf(defaultProps.getProperty("debug"));
            String paths=defaultProps.getProperty("paths");
            if(paths==null || paths.equalsIgnoreCase("")){
                throw new IllegalArgumentException("The paths property cannot be null.");
            }
            // if it's not null and it contains at least a , split it over the ,
            if(paths.contains(",")){
                this.pathArray = paths.split(",");
            } else if(!paths.contains(",")){
                // if it isnt null but doesnt contain any , use it as is
                this.pathArray = new String[1];
                this.pathArray[0] = paths;
            }
            String fileExtentions=defaultProps.getProperty("file_extentions");
            // if it's not null and it contains at least a , split it over the ,
            if(fileExtentions==null){
                this.fileExtentionArray = null;
            } else if(fileExtentions.contains(",")){
                this.fileExtentionArray = fileExtentions.split(",");
            } else if(!fileExtentions.contains(",")){
                // if it isnt null but doesnt contain any , use it as is
                this.fileExtentionArray = new String[1];
                this.fileExtentionArray[0] = paths;
            }
        } finally {
            if(propertiesFile!=null){
                propertiesFile.close();
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if(this.pathArray != null && this.pathArray.length > 0){
            for(int i = 0; i<this.pathArray.length; i++){
                sb.append(this.pathArray[i]).append(",");
            }
        }
        StringBuilder sb2 = new StringBuilder("");
        if(this.fileExtentionArray != null && this.fileExtentionArray.length > 0){
            for(int i = 0; i<this.fileExtentionArray.length; i++){
                sb2.append(this.fileExtentionArray[i]).append(",");
            }
        }
        return "ConfigProperties{" + "debug=" + this.debug + ", threads=" + this.threads + ", pathArray=" + sb.toString() + ", fileExtentionArray=" + sb2.toString() + '}';
    }
    
    /**
     * Returns whether the application will run in debug mode or not. If this parameter isn't specified in the config.properties file, it returns false.
     * @return whether the application will run in debug mode or not. If this parameter isn't specified in the config.properties file, it returns false
     */
    public Boolean isDebug() {
        return this.debug;
    }
    
    /**
     * Returns the number of parallel threads to use for the scan process. If none where specified in the config.properties file, the number of CPUs is returned.
     * @return the number of parallel threads to use for the scan process. If none where specified in the config.properties file, the number of CPUs is returned
     */
    public Integer getThreads() {
        return this.threads;
    }

    /**
     * Returns the array of absolute paths to scan.
     * @return the array of absolute paths to scan
     */
    public String[] getPathArray() {
        return this.pathArray;
    }
    
    /**
     * Returns a List of wanted file extentions.
     * @return a List of wanted file extentions
     */
    public List<String> getWantedFileExtentions() {
        List<String> result;
        if(this.fileExtentionArray==null){
            result = new ArrayList<String>();
        } else {
            result = Arrays.asList(this.fileExtentionArray);
            result = Collections.unmodifiableList(result);
        }
        return result;
    }
}