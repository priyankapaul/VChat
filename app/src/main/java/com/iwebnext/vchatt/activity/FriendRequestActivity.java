package com.iwebnext.vchatt.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.adapter.FriendRequestAdapter;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.FriendRequest;
import com.iwebnext.vchatt.request.FriendRequestAccept;
import com.iwebnext.vchatt.request.FriendRequestDecline;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendRequestActivity extends AppCompatActivity {
    private final static String TAG = FriendRequestActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FriendRequestAdapter adapter;
    private ArrayList<FriendRequest> friendRequestList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_request_layout);

        recyclerView = (RecyclerView) findViewById(R.id.friend_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        friendRequestList = new ArrayList<>();
        adapter = new FriendRequestAdapter(friendRequestList);
        recyclerView.setAdapter(adapter);

        fetchFriendRequestList();

        recyclerView.addOnItemTouchListener(new FriendRequestAdapter.RecyclerTouchListener(FriendRequestActivity.this, recyclerView, new FriendRequestAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                final FriendRequest friendRequest = adapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Friend Request");
                builder.setMessage("Please decide on request!");
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String id = friendRequest.getId();
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendRequestActivity.this);
                                        builder.setMessage("friend request accepted")
                                                .setNegativeButton("Ok", null)
                                                .create()
                                                .show();
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendRequestActivity.this);
                                        builder.setMessage("Failed")
                                                .setNegativeButton("Retry", null)
                                                .create()
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        FriendRequestAccept friendRequest = new FriendRequestAccept(id, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(FriendRequestActivity.this);
                        queue.add(friendRequest);
                    }
                });

                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String id = friendRequest.getId();
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendRequestActivity.this);
                                        builder.setMessage("friend request declined")
                                                .setNegativeButton("Ok", null)
                                                .create()
                                                .show();
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendRequestActivity.this);
                                        builder.setMessage("Failed")
                                                .setNegativeButton("Retry", null)
                                                .create()
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        FriendRequestDecline friendRequest = new FriendRequestDecline(id, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(FriendRequestActivity.this);
                        queue.add(friendRequest);


                    }
                });
                builder.show();


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    /**
     * fetching the friend request list
     */
    private void fetchFriendRequestList() {
        final String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();
        String endPoint = EndPoints.FRIEND_REQUEST.replace("_ID_", selfUserId);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    JSONArray friendRequests = obj.getJSONArray(Constants.TAG_JSON_LIST);
                    for (int i = 0; i < friendRequests.length(); i++) {
                        JSONObject friendRequest = (JSONObject) friendRequests.get(i);
                        String userId = friendRequest.getString("user_id");
                        String name = friendRequest.getString("name");
                        String image = friendRequest.getString("image");

                        FriendRequest model = new FriendRequest(name, userId, image);
                        friendRequestList.add(model);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(FriendRequestActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(FriendRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq);
    }
}