package com.iwebnext.vchatt.model;

/**
 * Created by PRIYANKA on 6/18/2016.
 */


import java.io.Serializable;


public class Post implements Serializable {
    String image,id, name, lastMessage, timestamp;
    int unreadCount;

    public Post() {
    }

    public Post(String id,String image, String name, String lastMessage, String timestamp, int unreadCount) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


   }