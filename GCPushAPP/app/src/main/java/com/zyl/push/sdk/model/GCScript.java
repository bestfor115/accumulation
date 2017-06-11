package com.zyl.push.sdk.model;

import java.io.Serializable;

public class GCScript implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 8473519280574600067L;
    private String id;
    private String url;
    private String loadClass;
    private int count;
    private String[] paramter;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public String getLoadClass() {
        return loadClass;
    }
    public void setLoadClass(String loadClass) {
        this.loadClass = loadClass;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String[] getParamter() {
        return paramter;
    }
    public void setParamter(String[] paramter) {
        this.paramter = paramter;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

}
