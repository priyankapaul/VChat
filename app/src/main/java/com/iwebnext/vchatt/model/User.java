package com.iwebnext.vchatt.model;

import java.io.Serializable;


public class User implements Serializable {
    /**
     * friend request statuses
     */
    public static int FRIEND_REQUEST_NOT_INITIATED = 0;
    public static int FRIEND_REQUEST_SENT = 1;
    public static int FRIEND_REQUEST_ACCEPTED = 2;
    public static int FRIEND_REQUEST_CANCELLED = 3;
    public static int FRIEND_REMOVE = 4;
    public static int FRIEND_REQUEST_DECLINE = 5;
    public static int PICTURE_SENT = 1;


    String id;
    String name;
    String email;
    String timestamp;
    String image;

    int friendRequestStatus = FRIEND_REQUEST_NOT_INITIATED;

    public User() {
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getFriendRequestStatus() {
        return friendRequestStatus;
    }

    public void setFriendRequestStatus(int friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }
}
