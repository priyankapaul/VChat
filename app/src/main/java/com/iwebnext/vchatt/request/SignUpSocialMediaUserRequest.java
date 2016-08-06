package com.iwebnext.vchatt.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.utils.EndPoints;

import java.util.HashMap;
import java.util.Map;

public class SignUpSocialMediaUserRequest extends StringRequest {
    private Map<String, String> params;

    public SignUpSocialMediaUserRequest(String name, String email, String type, String social_media_id, Response.Listener<String> listener) {
        super(Method.POST, EndPoints.REGISTER_SOCIAL_MEDIA_USER, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("type", type);
        params.put("social_media_id", social_media_id);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
