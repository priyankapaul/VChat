package com.iwebnext.vchatt.model;

import java.io.Serializable;


public class Friend implements Serializable {
    String id, name, lastMessage, timestamp;
    String image, telephone, profession, address;
    int unreadCount;
    boolean status;
    public Boolean box;
    public boolean isSelected;
    public Friend() {
    }

    public Friend(String id,Boolean box,boolean isSelected, String image, String name, String lastMessage, String profession, String address,String telephone, String timestamp, int unreadCount, boolean status) {
        this.id = id;
        this.name = name;
        this.telephone = telephone;
        this.address = address;
        this.profession = profession;
        this.image = image;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.status = status;
        this.box = box;
        this.isSelected = isSelected;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public Boolean getBox() {
        return box;
    }
    public void setBox(Boolean box){
        this.box = box;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
