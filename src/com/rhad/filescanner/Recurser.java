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

import com.rhad.filescanner.model.Pacchetti;
import com.rhad.filescanner.persistence.EntityManagerFactoryUtil;
import java.io.File;
import java.util.Date;
import java.util.regex.*;
import javax.persistence.*;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

/**
 * This class recursively traverses all the files contained in the passed root folder, creating new 
 * {@link com.rhad.filescanner.model.Pacchetti}s every time a file is found, and prints / merges them to the database.
 * @author Roberto Fabrizi
 */
public final class Recurser implements Runnable {
    
    private final String root;
    private final File dir;
    private final boolean debug;
    private final Pattern pattern;
    private static final Logger LOGGER = Logger.getLogger(Recurser.class);
        
    /**
     * Creates a <code>Recurser</code>.
     * @param root the name of the starting root folder
     * @param dir the dir for the traversal
     * @param pattern the Pattern to use
     * @param debug if the <code>Recurser</code> will merge the {@link com.rhad.filescanner.model.Pacchetti}s or just print them out for debug purposed
     */
    public Recurser(String root, File dir, Pattern pattern, boolean debug){
        this.root=root;
        this.dir=dir;
        this.pattern=pattern;
        this.debug=debug;
    }

    private void listRecursively(String root, File fdir) {
        // if it's a file it's a leaf, create a Pacchetti with the root name and current file
        if(fdir.isFile()){
            if(this.pattern==null || (this.pattern!=null && this.pattern.matcher(fdir.getName()).matches())){
                Pacchetti pacchetto = new Pacchetti();
                pacchetto.setPackageName(root);
                pacchetto.setFileName(fdir.getName());
                pacchetto.setFileSize(fdir.length());
                pacchetto.setLastModified(new Date(fdir.lastModified()));
                //System.out.println("About to merge the Pacchetti: "+pacchetto);
                if(this.debug==false){
                    LOGGER.trace("About to merge the Pacchetti: "+pacchetto);
                    this.merge(pacchetto);
                }
            }
        }
        // if it's a folder it's not a leaf, keep traversing the file system
        if (fdir.isDirectory()) {
            File[] fileArray = fdir.listFiles();
            if (fileArray!=null && fileArray.length>0){
                // Go over each file/subdirectory.
                for (File f : fileArray) {
                    if (f!=null)
                        listRecursively(root, f);
                }
            }
        }
    }

    private void merge(Pacchetti pacchetto) {
        LOGGER.trace("EntityManager.merge() called");
        EntityManager entityManager = null;
        // a transaction object
        EntityTransaction tx = null;
        try {
            // get an EntityManager from the EntityManagerFactory
            entityManager = EntityManagerFactoryUtil.GetEntityManagerFactory().createEntityManager();
            // get a transaction from the EntityManager
            tx = entityManager.getTransaction();
            LOGGER.trace("EntityManager.getTransaction() ended");
            // open a transaction
            tx.begin();
            LOGGER.trace("EntityTransaction opened");
            entityManager.merge(pacchetto);
            // commit
            tx.commit();
            LOGGER.trace("EntityTransaction.commit() ended successfully");
        } catch(HibernateException he) {
            // grab this superclass runtime exception, print the object that caused it
            LOGGER.error("The Pacchetti that caused the exception is:");
            LOGGER.error(pacchetto);
            // and rethrow it
            throw he;
        } finally {
            if (tx != null && tx.isActive()) {
                LOGGER.trace("Tx is not null and it is active");
                try {
                    // Second try catch as the rollback could fail as well
                    tx.rollback();
                    LOGGER.trace("Tx rolled back");
                } catch (Exception e1) {
                    LOGGER.error("Could not rollback the current transaction",e1);
                }
            }
            // close the entity manager to not go out of memory
            if(entityManager!=null && entityManager.isOpen()){
                entityManager.close();
                LOGGER.trace("EntityManager.close() called");
            }
        }
        LOGGER.trace("EntityManager.merge() ended");
    }

    @Override
    public void run() {
        LOGGER.trace("Started....");
        this.listRecursively(this.root, this.dir);
        LOGGER.trace("Ended.");        
    }
}