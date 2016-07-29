package com.iwebnext.vchatt.model;

import java.io.Serializable;


public class SearchAllUser implements Serializable {
    String image,id, name;
    int unreadCount;

    public SearchAllUser() {
    }

    public SearchAllUser(String id, String image, String name) {
        this.id = id;
        this.name = name;
        this.image = image;

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
