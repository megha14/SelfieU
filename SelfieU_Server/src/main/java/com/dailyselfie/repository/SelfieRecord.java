package com.dailyselfie.repository;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class SelfieRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String user;
    private String name;
    private String contentType;
    private String dataUrl;
    private String path;
    private String date_created;
    private String date_modified;


    public SelfieRecord() {

    }

    public SelfieRecord(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    public String getDate_created() {

        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDataUrl() {
        return dataUrl;
    }


    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {

        return "ID : " + getId() +
                " Name : " + getName() +
                " Path : " + getPath() +
                " content type : " + getContentType() +
                " User : " + getUser() +
                " url : " + getDataUrl() +
                " date created : " + getDate_created() +
                " date modified : " + getDate_modified();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SelfieRecord)
                && Objects.equals(getName(), ((SelfieRecord) obj).getName());

    }


}