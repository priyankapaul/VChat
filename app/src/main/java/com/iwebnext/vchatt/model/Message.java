package com.iwebnext.vchatt.model;

import java.io.Serializable;


public class Message implements Serializable {
    String id, message, createdAt, messageType, image,videoUrl;
    User user;

    public Message() {
    }

    public Message(String id, String message, String createdAt, User user, String messageType, String image) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.messageType = messageType;
        this.image = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
