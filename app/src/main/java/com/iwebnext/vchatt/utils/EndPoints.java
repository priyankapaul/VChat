package com.iwebnext.vchatt.utils;

public class EndPoints {

    // localhost url
    // public static final String BASE_URL = "http://192.168.0.101/gcm_chat/v1";

    public static final String BASE_URL = "http://inextwebs.com/gcm_chat/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String REGISTER_USER =  BASE_URL + "/register_user";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String MESSAGES = BASE_URL + "/messages/_ID_/_MY_";
    public static final String ADD_MESSAGE = BASE_URL + "/add_message";
    public static final String FRIEND_LIST = BASE_URL + "/friend_list/_ID_";
    public static final String FRIEND_REQUEST =  BASE_URL + "/friend_requests/_ID_";
    public static final String PROFILE =  BASE_URL + "/user_profile/_ID_";
    public static final String UPDATE_AVATAR =  BASE_URL + "/update_avatar";
    public static final String UPLOAD_VIDEO =  BASE_URL + "/upload_video";

    // TODO - To use standard /v1/ version of APIs
    public static final String NAV_DRAWER = "http://inextwebs.com/gcm_chat/include/nav_drawer.php?id=_ID_";
    public static final String SEARCH_ALL_USER = "http://inextwebs.com/gcm_chat/include/search_all_user.php";
    public static final String UPLOAD_IMAGE_URL = "http://inextwebs.com/gcm_chat/include/attach_picture.php";
}


