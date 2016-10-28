package com.iwebnext.vchatt.model;

import java.io.Serializable;


public class Message implements Serializable {
    // Message Types
    public static int TEXT = 0;
    public static int IMAGE = 1;
    public static int VIDEO = 2;

    String id, content, createdAt;
    String userId;
    String userName;
    int type;

    /**
     * Message constructor
     *
     * @param id
     * @param content
     * @param createdAt
     * @param userId
     * @param type
     */
    public Message(String id, String content, String createdAt, String userId, String userName, int type) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userName = userName;
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
    public int setType(int type) {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String setContent(String content) {
        return  content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String setCreatedAt(String createdAt) {
        return createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String setUserId(String userId) {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String setUserName(String userName) {
        return  userName;
    }
}
