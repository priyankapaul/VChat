package com.iwebnext.vchatt.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.app.BaseApplication;

import java.util.HashMap;
import java.util.Map;

public class DeclineFriendRequest extends StringRequest {
    final static String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();


    private static final String REGISTER_REQUEST_URL = "http://inextwebs.com/gcm_chat/include/friend_request_decline.php";
    private Map<String, String> params;

    public DeclineFriendRequest(String id, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("friend_id", id);
        params.put("my_id",selfUserId);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
