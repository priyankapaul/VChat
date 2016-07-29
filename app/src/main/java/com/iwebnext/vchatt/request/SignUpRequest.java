package com.iwebnext.vchatt.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignUpRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://inextwebs.com/gcm_chat/include/SignUp.php";
    private Map<String, String> params;

    public SignUpRequest(String name, String email, String password, String medicalLicenseNo, String profession, String state, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);        
        params.put("email", email);
        params.put("password", password);

        params.put("medical_license_no", medicalLicenseNo);
        params.put("profession",profession);
        params.put("state", state);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
