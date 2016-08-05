package com.iwebnext.vchatt.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.utils.EndPoints;

import java.util.HashMap;
import java.util.Map;

public class SignUpRequest extends StringRequest {
    private Map<String, String> params;

    public SignUpRequest(String name, String email, String password, String medicalLicenseNo, String profession, String state, Response.Listener<String> listener, String address, String telephone, String website, String type, String social_media_id) {
        super(Method.POST, EndPoints.REGISTER_USER, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("medical_license_no", medicalLicenseNo);
        params.put("profession", profession);
        params.put("state", state);
        params.put("address", address);
        params.put("telephone", telephone);
        params.put("website", website);
        params.put("type", type);
        params.put("social_media_id", social_media_id);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
