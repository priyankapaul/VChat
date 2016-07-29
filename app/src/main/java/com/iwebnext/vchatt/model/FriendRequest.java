package com.iwebnext.vchatt.model;

/**
 * Created by PRIYANKA on 6/30/2016.
 */


import android.os.Parcel;
import android.os.Parcelable;

public class FriendRequest implements Parcelable {

    private String name;
    private String id;
    private String image;
    private Boolean friendRequestSent;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public Boolean getFriendRequestSent(){
        return friendRequestSent;
    }

    /**
     * A constructor that initializes the FriendRequest object
     **/
    public FriendRequest(String name, String id, String image) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.friendRequestSent = friendRequestSent;
    }

    /**
     * Retrieving FriendRequest data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    protected FriendRequest(Parcel in) {
        name = in.readString();
        id = in.readString();
        image = in.readString();
        friendRequestSent = (in.readInt() == 0) ? false : true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeInt(friendRequestSent ? 1 : 0);
        dest.writeString(image);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FriendRequest> CREATOR = new Parcelable.Creator<FriendRequest>() {
        @Override
        public FriendRequest createFromParcel(Parcel in) {
            return new FriendRequest(in);
        }

        @Override
        public FriendRequest[] newArray(int size) {
            return new FriendRequest[size];
        }
    };
}