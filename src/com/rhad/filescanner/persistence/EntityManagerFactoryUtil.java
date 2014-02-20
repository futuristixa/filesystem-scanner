/*
 * Copyright 2014 Roberto Fabrizi.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to:
 * Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *
 */
package com.rhad.filescanner.persistence;

import javax.persistence.*;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;

/**
 * A factory that manages and returns an {@link javax.persistence.EntityManagerFactory} object (an <code>EntityManagerFactory</code> is an expensive-to-create,
 * threadsafe object intended to be shared by all application threads). It is created once, usually on application startup,
 * and failing to create it usually means that the application should terminate.
 * @author Roberto Fabrizi
 */
public final class EntityManagerFactoryUtil {
    
    // the EntityManagerFactory used to retrieve EntityManager objects
    private static EntityManagerFactory FACTORY;
    private static EntityManagerFactoryUtil EntityManagerFactoryUtil = null;
    private static final Logger LOGGER = Logger.getLogger(EntityManagerFactoryUtil.class);
    private static final String STRONG_ENCRYPTOR_PASSWORD="3Kp24zesgV7uHVZkWvxB";
    
    /**
     * Creates this factory. This method is thread safe to avoid initializing two factories.
     * @return the <code>EntityManagerFactoryUtil</code> singleton instance
     */
    public synchronized static EntityManagerFactoryUtil CreateEntityManagerFactoryUtil() {
        LOGGER.trace("CreateEntityManagerFactoryUtil called");
        if (EntityManagerFactoryUtil == null) {
            EntityManagerFactoryUtil = new EntityManagerFactoryUtil("fileScanner");
        }
        LOGGER.trace("CreateEntityManagerFactoryUtil ended");
        return EntityManagerFactoryUtil;
    }
    
    /**
     * The private ctor, which means that it can't be instantiated directly, as requested by the Singleton design pattern.
     * @param entityManagerFactory the name of the <code>persistence-unit</code> to use
     */
    private EntityManagerFactoryUtil(String entityManagerFactory) {
        // the default behaviour is to lookup a file persistence.xml into the meta-inf of the current jar. If
        // it isn't there, the meta-inf folder of the other jars in the classpath are looked up
        // an encryptor, to put encrypted passwords in the persistence.xml
        StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor(); 
        // password used to encrypt/decrypt the passwords contained in the persistence.xml
        strongEncryptor.setPassword(STRONG_ENCRYPTOR_PASSWORD); 
        // an hibernate encryption registry
        HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance(); 
        // set the registry
        registry.registerPBEStringEncryptor("configurationHibernateEncryptor", strongEncryptor);
        // create the EntityManagerFactory
        //this.factory=Persistence.createEntityManagerFactory(entityManagerFactory);
        FACTORY=Persistence.createEntityManagerFactory(entityManagerFactory);
    }
    
    /**
     * Returns the {@link javax.persistence.EntityManagerFactory} managed by this class.
     * @return a <code>EntityManagerFactory</code> to acquire {@link javax.persistence.EntityManager} instances from
     */
    public static EntityManagerFactory GetEntityManagerFactory() {
        return FACTORY;
    }
    
    /**
     * Closes the {@link javax.persistence.EntityManagerFactory} managed by this class.
     */
    public static synchronized void Close() {
        if(FACTORY!=null && FACTORY.isOpen()){
            FACTORY.close();
        }
    }
}