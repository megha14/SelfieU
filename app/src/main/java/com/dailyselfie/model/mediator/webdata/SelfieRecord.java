package com.dailyselfie.model.mediator.webdata;

public class SelfieRecord {
    private long id;
    private String name;
    private String contentType;
    private String dataUrl;
    private String user;
    private String path;
    private String date_created;
    private String date_modified;


    public SelfieRecord(String name, String contentType) {
        this.contentType = contentType;
        this.name = name;

    }

    public SelfieRecord(String name) {
        this.name = name;

    }

    public SelfieRecord(long id, String name, String contentType) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
    }

    public SelfieRecord() {

    }

    public String getDate_created() {
        return date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

}
