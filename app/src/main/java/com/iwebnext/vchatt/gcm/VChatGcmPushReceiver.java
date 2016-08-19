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
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.iwebnext.vchatt.activity.ChatRoomActivity;
import com.iwebnext.vchatt.activity.MainActivity;
import com.iwebnext.vchatt.app.Config;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.Message;
import com.iwebnext.vchatt.model.User;
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
            case Config.PUSH_TYPE_USER:
                // push notification is specific to user
//                processUserMessage(title, isBackground, data);
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

    /**
     * Processing chat room push message
     * this message will be broadcasts to all the activities registered
     */
    private void processChatRoomPush(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject datObj = new JSONObject(data);

                JSONObject uObj = datObj.getJSONObject("user");

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
                JSONObject msgObj = datObj.getJSONObject("message");

                String msgId = msgObj.getString("message_id");
                String content = msgObj.getString("content");
                String createdAt = msgObj.getString("created_at");
                int type = msgObj.getInt("type");

                message = new Message(msgId, content, createdAt, userId, userName, type);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", Config.PUSH_TYPE_CHATROOM);
                    pushNotification.putExtra("message", message);
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
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     * Processing user specific push message
     * It will be displayed with / without nivUser in push notification tray
     */
//    private void processUserMessage(String title, boolean isBackground, String data) {
//        if (!isBackground) {
//
//            try {
//                JSONObject datObj = new JSONObject(data);
//
//                String imageUrl = datObj.getString("image");
//
//                JSONObject uObj = datObj.getJSONObject("user");
//
//                String userId = uObj.getString("user_id");
//                String name = uObj.getString("name");
//                String email = uObj.getString("email");
//                User user = new User(userId, name, email);
//
//                Message message = null;
//                JSONObject msgObj = datObj.getJSONObject("message");
//
//                boolean error = msgObj.getBoolean("error");
//                if (!error) {
//                    String msgId = msgObj.getString("message_id");
//                    // TODO - not sure to be created with image or not
//                    String content = msgObj.getString("content");
//                    String createdAt = msgObj.getString("created_at");
//
//                    // TODO - Make one single API to upload video/image or text
//                    message = new Message(msgId, imageUrl, createdAt, user, Message.IMAGE);
//                } else {
//                    // TODO - what if message is returns in error=true
//                    message = new Message(null, null, null, user, Message.IMAGE);
//                }
//
//                // verifying whether the app is in background or foreground
//                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//
//                    // app is in foreground, broadcast the push message
//                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                    pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
//                    pushNotification.putExtra("message", message);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                    // play notification sound
//                    NotificationUtils notificationUtils = new NotificationUtils();
//                    notificationUtils.playNotificationSound();
//                } else {
//
//                    // app is in background. show the message in notification try
//                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//
//                    // check for push notification nivUser attachment
//                    if (TextUtils.isEmpty(imageUrl)) {
//                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getContent(), message.getCreatedAt(), resultIntent);
//                    } else {
//                        // push notification contains nivUser
//                        // show it with the nivUser
//                        showNotificationMessageWithBigImage(getApplicationContext(), title, message.getContent(), message.getCreatedAt(), resultIntent, imageUrl);
//                    }
//                }
//            } catch (JSONException e) {
//                Log.e(TAG, "json parsing error: " + e.getMessage());
//                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//
//        } else {
//            // the push notification is silent, may be other operations needed
//            // like inserting it in to SQLite
//        }
//    }

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
