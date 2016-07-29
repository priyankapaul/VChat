package com.iwebnext.vchatt.request;

/**
 * Created by PRIYANKA on 6/7/2016.
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfileRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://inextwebs.com/gcm_chat/include/update_profile.php";
    private Map<String, String> params;

    public ProfileRequest(String user_id, String name, String email, String password,String address, String telephone, String medical_license_no, String prof_name, String website, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("address",address);
        params.put("telephone",telephone);
        params.put("medical_license_no", medical_license_no);
        params.put("prof_name", prof_name);
        params.put("website", website);
    }



    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
