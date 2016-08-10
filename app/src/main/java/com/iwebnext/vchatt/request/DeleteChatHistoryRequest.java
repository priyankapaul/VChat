package com.iwebnext.vchatt.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.utils.EndPoints;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by priyanka on 8/10/16.
 */
public class DeleteChatHistoryRequest extends StringRequest {
    private Map<String, String> params;

    public DeleteChatHistoryRequest(String userId, Response.Listener<String> listener) {
        super(Method.POST, EndPoints.DELETE_CHAT_HISTORY, listener, null);

        params = new HashMap<>();
        params.put("user_id", userId);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
