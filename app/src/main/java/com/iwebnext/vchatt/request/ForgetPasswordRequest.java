package com.iwebnext.vchatt.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://inextwebs.com/gcm_chat/include/forget_password.php";
    private Map<String, String> params;

    public ForgetPasswordRequest(String forgetEmail, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email", forgetEmail);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
