/*
 * Copyright 2014 Roberto Fabrizi.
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to:
 * Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *
 */
package com.rhad.filescanner.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * This class maps the PACCHETTI table on the database. If the table doesn't exist it is automatically generated.
 * @author Roberto Fabrizi
 */
@Entity
@Table(name="PACCHETTI" ,schema="ORAP8")
public class Pacchetti implements java.io.Serializable {

    @Id
    @GenericGenerator(name="kaugen" , strategy="increment")
    @GeneratedValue(generator="kaugen")
    @Column(name="ID", precision=22, scale=0)
    private BigDecimal id;
    @Column(name="PACKAGE_NAME")
    private String packageName;
    @Column(name="FILE_NAME")
    private String fileName;
    @Column(name="FILE_SIZE", precision=16, scale=0)
    private Long fileSize;
    @Temporal(TemporalType.DATE)
    @Column(name="LAST_MODIFIED", length=7)
    private Date lastModified;

    /**
     * Create an empty <code>Pacchetti</code>.
     */
    public Pacchetti() {
    }

    /**
     * Create a <code>Pacchetti</code> with the passed id.
     * @param id the id of this Pacchetti
     */
    public Pacchetti(BigDecimal id) {
        this.id = id;
    }

    /**
     * Create a <code>Pacchetti</code> with the passed parameters.
     * @param id 
     * @param packageName 
     * @param fileName
     * @param fileSize
     * @param lastModified
     */
    public Pacchetti(BigDecimal id, String packageName, String fileName, Long fileSize, Date lastModified) {
       this(id);
       this.packageName = packageName;
       this.fileName = fileName;
       this.fileSize = fileSize;
       this.lastModified = lastModified;
    }
   
    @Override
    public String toString() {
        return "Pacchetti{" + "id=" + this.id + ", packageName=" + this.packageName + ", fileName=" + this.fileName + ", fileSize=" + this.fileSize + ", lastModified=" + this.lastModified + '}';
    }

    /**
     * Returns the id of this object.
     * @return the id of this object
     */
    public BigDecimal getId() {
        return this.id;
    }
    
    /**
     * Sets the id of this object.
     * @param id the id of this object
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }
    
    /**
     * Returns the package name of this object.
     * @return the package name of this object
     */
    public String getPackageName() {
        return this.packageName;
    }
    
    /**
     * Set the package name of this object.
     * @param packageName the package name of this object
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    /**
     * Returns the file name of this object.
     * @return the file name of this object
     */
    public String getFileName() {
        return this.fileName;
    }
    
    /**
     * Sets the file name of this object.
     * @param fileName the file name of this object
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Returns the file size of this object.
     * @return the file size of this object
     */
    public Long getFileSize() {
        return this.fileSize;
    }
    
    /**
     * Set the file size of this object.
     * @param fileSize the file size of this object
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    /**
     * Returns the date of the last change to the file contained in this Pacchetti.
     * @return the date of the last change to the file contained in this Pacchetti
     */
    public Date getLastModified() {
        return this.lastModified;
    }
    
    /**
     * Sets the date of the last change to the file contained in this Pacchetti.
     * @param lastModified the date of the last change to the file contained in this Pacchetti
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}