package com.ly.avid.fbproject;

/**
 * Created by Holaverse on 2017/1/20.
 */

public class ItemUser {

    private String id;
    private String name;
    private String url;

    public ItemUser(String id, String name, String url){
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
