/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iwebnext.vchatt.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.iwebnext.vchatt.activity.ChatRoomActivity;
import com.iwebnext.vchatt.activity.GroupChatRoomActivity;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.app.Config;
import com.iwebnext.vchatt.model.Message;
import com.iwebnext.vchatt.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class VChatGcmPushReceiver extends GcmListenerService {

    private static final String TAG = VChatGcmPushReceiver.class.getSimpleName();


    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String title = bundle.getString("title");
        Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
        String flag = bundle.getString("flag");
        String data = bundle.getString("data");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "isBackground: " + isBackground);
        Log.d(TAG, "flag: " + flag);
        Log.d(TAG, "data: " + data);

        if (flag == null)
            return;

        if (BaseApplication.getInstance().getPrefManager().getUser() == null) {
            // user is not logged in, skipping push notification
            Log.e(TAG, "user is not logged in, skipping push notification");
            return;
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        switch (Integer.parseInt(flag)) {
            case Config.PUSH_TYPE_CHATROOM:
                // push notification belongs to a chat room
                processChatRoomPush(title, isBackground, data);
                break;
            case Config.PUSH_TYPE_GROUP_CHATROOM:
                processGroupMessage(title, isBackground, data);
                break;
            case Config.PUSH_TYPE_USER:
                // push notification is specific to user
//                processUserMessage(title, isBackground, data);
                break;
            case Config.PUSH_TYPE_USER_STATUS:
                processUserStatusPush(isBackground, data);
                break;
        }
    }

//    "message":{
//        "created_at":"2016-08-19 05:19:06",
//                "message_id":887,
//                "type":0,
//                "content":"ffdfdfdfd"
//    },
//            "user":{
//        "user_id":144,
//                "user_name":"priyanka"
//    }

    private void processUserStatusPush(boolean isBackground, String data) {
        if (!isBackground) {
            try {
                JSONObject dataObj = new JSONObject(data);
                JSONObject uObj = dataObj.getJSONObject("user");

                String userId = uObj.getString("user_id");
                boolean userStatus = uObj.getBoolean("user_status");

                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra(Constants.EXTRA_KEY_PUSH_TYPE, Config.PUSH_TYPE_USER_STATUS);
                pushNotification.putExtra(Constants.EXTRA_KEY_USER_STATUS, userStatus);
                pushNotification.putExtra(Constants.EXTRA_KEY_FRIEND_ID, userId);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
            }
        }
    }


    /**
     * Processing chat room push message
     * this message will be broadcasts to all the activities registered
     */
    private void processChatRoomPush(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject dataObj = new JSONObject(data);

                JSONObject uObj = dataObj.getJSONObject("user");

                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (uObj.getString("user_id").equals(BaseApplication.getInstance().getPrefManager().getUser().getId())) {
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }

                String userId = uObj.getString("user_id");
                String userName = uObj.getString("user_name");

                Message message = null;
                JSONObject msgObj = dataObj.getJSONObject("message");

                String msgId = msgObj.getString("message_id");
                String content = msgObj.getString("content");
                String createdAt = msgObj.getString("created_at");
                int type = msgObj.getInt("type");

                message = new Message(msgId, content, createdAt, userId, userName, type);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra(Constants.EXTRA_KEY_PUSH_TYPE, Config.PUSH_TYPE_CHATROOM);
                    pushNotification.putExtra(Constants.EXTRA_KEY_MESSAGE, message);
                    pushNotification.putExtra(Constants.EXTRA_KEY_FRIEND_ID, userId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    resultIntent.putExtra(Constants.EXTRA_KEY_FRIEND_ID, userId);
                    showNotificationMessage(getApplicationContext(), title, userName + " : " + message.getContent(), message.getCreatedAt(), resultIntent);
                }

            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
//                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }


    /**
     * Processing group chat room push message
     * this message will be broadcasts to all the activities registered
     */
//    private void processGroupMessage(String groupTitle, boolean isBackground, String data) {
//        if (!isBackground) {
//
//            try {
//                JSONObject dataObj = new JSONObject(data);
//                String groupId = dataObj.getString("group_id");
//                String groupName = dataObj.getString("group_name");
//
//                Message message = null;
//                JSONObject mObj = dataObj.getJSONObject("message");
////                Message message = new Message();
////                message.setContent(mObj.getString("content"));
////                message.setId(mObj.getString("message_id"));
////                message.setCreatedAt(mObj.getString("created_at"));
////                message.setType(mObj.getInt("type"));
//                String msgId = mObj.getString("message_id");
//                String content = mObj.getString("content");
//                String createdAt = mObj.getString("created_at");
//                int type = mObj.getInt("type");
//                message = new Message(msgId, content, createdAt, groupId, groupName, type);
//
//
//                JSONObject uObj = dataObj.getJSONObject("user");
//
//                if (uObj.getString("user_id").equals(BaseApplication.getInstance().getPrefManager().getUser().getId())) {
//                    Log.e(TAG, "Skipping the push message as it belongs to same user");
//                    return;
//                }
//                User user = new User();
//                user.setId(uObj.getString("user_id"));
//                user.setEmail(uObj.getString("email"));
//                user.setName(uObj.getString("name"));
////                message.setUser(user);
//
////
////                JSONObject uObj = dataObj.getJSONObject("user");
////                String userId = uObj.getString("user_id");
////                String userName = uObj.getString("user_name");
//
//
//                // skip the message if the message belongs to same user as
//                // the user would be having the same message when he was sending
//                // but it might differs in your scenario
//                if (uObj.getString("user_id").equals(BaseApplication.getInstance().getPrefManager().getUser().getId())) {
//                    Log.e(TAG, "Skipping the push message as it belongs to same user");
//                    return;
//                }
////                User user = new User();
////                user.setId(uObj.getString("user_id"));
////                user.setEmail(uObj.getString("email"));
////                user.setName(uObj.getString("user_name"));
////                String userName = uObj.getString("user_name");
//                // message.setUser(user);
//
//                // verifying whether the app is in background or foreground
//                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                    // app is in foreground, broadcast the push message
//                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                    pushNotification.putExtra(Constants.EXTRA_KEY_PUSH_TYPE, Config.PUSH_TYPE_GROUP_CHATROOM);
//                    pushNotification.putExtra(Constants.EXTRA_KEY_MESSAGE, message);
//                    //    pushNotification.putExtra(Constants.EXTRA_KEY_GROUP_ID, groupId);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//                    // play notification sound
//                    NotificationUtils notificationUtils = new NotificationUtils();
//                    notificationUtils.playNotificationSound();
//                } else {
//
//                    // app is in background. show the message in notification try
//                    Intent resultIntent = new Intent(getApplicationContext(), GroupChatRoomActivity.class);
//                    resultIntent.putExtra(Constants.EXTRA_KEY_GROUP_ID, groupId);
//                    showNotificationMessage(getApplicationContext(), groupTitle, groupName + " : " + message.getContent(), message.getCreatedAt(), resultIntent);
//                }
//
//            } catch (JSONException e) {
//                Log.e(TAG, "json parsing error: " + e.getMessage());
//            }
//
//        } else {
//            // the push notification is silent, may be other operations needed
//            // like inserting it in to SQLite
//        }
//    }
    private void processGroupMessage(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject dataObj = new JSONObject(data);
                String groupId = dataObj.getString("group_id");
                String groupName = dataObj.getString("group_name");

                JSONObject uObj = dataObj.getJSONObject("user");

                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (uObj.getString("user_id").equals(BaseApplication.getInstance().getPrefManager().getUser().getId())) {
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }

                String userId = uObj.getString("user_id");
                String userName = uObj.getString("user_name");

                Message message = null;
                JSONObject msgObj = dataObj.getJSONObject("message");

                String msgId = msgObj.getString("group_message_id");
                String content = msgObj.getString("content");
                String createdAt = msgObj.getString("created_at");
                int type = msgObj.getInt("type");

                message = new Message(msgId, content, createdAt, userId, userName, type);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra(Constants.EXTRA_KEY_PUSH_TYPE, Config.PUSH_TYPE_GROUP_CHATROOM);
                    pushNotification.putExtra(Constants.EXTRA_KEY_MESSAGE, message);
                    pushNotification.putExtra(Constants.EXTRA_KEY_GROUP_ID, groupId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), GroupChatRoomActivity.class);
                    resultIntent.putExtra(Constants.EXTRA_KEY_GROUP_ID, groupId);
                    showNotificationMessage(getApplicationContext(), title, groupName + " : " + message.getContent(), message.getCreatedAt(), resultIntent);
                }

            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
//                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {

            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }


    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and nivUser
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
