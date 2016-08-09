package com.iwebnext.vchatt.app;


import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.iwebnext.vchatt.LruBitmapCache;
import com.iwebnext.vchatt.activity.LoginActivity;
import com.iwebnext.vchatt.helper.AppPreferenceManager;
import com.iwebnext.vchatt.request.DeleteChatHistoryRequest;
import com.iwebnext.vchatt.utils.Constants;


public class BaseApplication extends Application {

    public static final String TAG = BaseApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static BaseApplication mInstance;

    private AppPreferenceManager pref;

    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public AppPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new AppPreferenceManager(this);
        }

        return pref;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void logout() {
        deleteChatHistoryAtLogout();
        
        if (pref.getUserType().equals(Constants.USER_TYPE_FACEBOOK)) {
            LoginManager.getInstance().logOut();
        }

        pref.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void deleteChatHistoryAtLogout() {
        String userId = pref.getUser().getId();
        addToRequestQueue(new DeleteChatHistoryRequest(userId, null));
    }
}
