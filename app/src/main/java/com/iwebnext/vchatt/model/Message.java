package com.iwebnext.vchatt.model;

import java.io.Serializable;


public class Message implements Serializable {
    // Message Types
    public static int TEXT = 0;
    public static int IMAGE = 1;
    public static int VIDEO = 2;

    String id, content, createdAt;
    int type;

    User user;

    public Message(String id, String content, String createdAt, User user, int type) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.user = user;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
