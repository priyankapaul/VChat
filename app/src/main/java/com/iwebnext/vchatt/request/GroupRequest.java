package com.iwebnext.vchatt.request;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.utils.EndPoints;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PRIYANKA on 10/18/2016.
 */

public class GroupRequest extends StringRequest {
    final static String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();

    private Map<String, String> params;

    public GroupRequest(String name, Response.Listener<String> listener) {
        super(Method.POST, EndPoints.GROUP_DETAILS, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("user_id", selfUserId);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}


