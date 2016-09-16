package com.iwebnext.vchatt.model;

import java.io.Serializable;


public class Friend implements Serializable {
    String id, name, lastMessage, timestamp;
    String image, telephone, profession, address;
    int unreadCount;

    public Friend() {
    }

    public Friend(String id, String image, String name, String lastMessage, String profession, String address,String telephone, String timestamp, int unreadCount) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.address = address;
        this.profession = profession;
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

    public String getTelephone(){
        return telephone;
    }
    public void setTelephone(String telephone){
        this.telephone = telephone;
    }
    public String getProfession(){
        return profession;
    }
    public void setProfession(String profession){
        this.profession = profession;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
