package com.iwebnext.vchatt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.adapter.GroupAdapter;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.Friend;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PRIYANKA on 10/14/2016.
 */
public class GroupActivity extends AppCompatActivity {
    private String TAG = GroupActivity.class.getSimpleName();

    public ArrayList<Friend> friendsArrayList;
    private GroupAdapter friendListAdapter;
    private RecyclerView rvFriends;
    private GroupAdapter gAdapter;
    FrameLayout Frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        rvFriends = (RecyclerView) findViewById(R.id.recycler_view);
        Frame = (FrameLayout) findViewById(R.id.fab_frame);
        final FloatingActionButton groupFab = (FloatingActionButton) findViewById(R.id.group_fab);
        groupFab.setVisibility(View.VISIBLE);


        groupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, GroupNameActivity.class);
                ArrayList<String> myList = new ArrayList<String>();
                intent.putExtra("mylist", myList);

                GroupActivity fragment = new GroupActivity();

//                Bundle bundle = new Bundle();
//                bundle.putSerializable(Constants.EXTRA_KEY_USER, gAdapter.getItem(position));
//                // bundle.putSerializable(Constants.EXTRA_KEY_USER, mAdapter.getItemId(position));

                startActivity(intent);
            }
        });

        friendsArrayList = new ArrayList<>();
        friendListAdapter = new GroupAdapter(GroupActivity.this, friendsArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GroupActivity.this);
        rvFriends.setLayoutManager(layoutManager);
        rvFriends.setAdapter(friendListAdapter);

    fetchFriendList();

    }

//    private List<Friend> currentSelectedItems = new ArrayList<>();
//
//    ...
//
//    friendListAdapter = new GroupAdapter(items, new GroupAdapter.OnItemCheckListener() {
//        @Override
//        public void onItemCheck(Friend item) {
//            currentSelectedItems.add(item);
//        }
//
//        @Override
//        public void onItemUncheck(Friend item) {
//            currentSelectedItems.remove(item);
//        }
//    });



    /**
     * fetching the friend list
     */
    private void fetchFriendList() {
        final String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();
        String endPoint = EndPoints.FRIEND_LIST.replace("_ID_", selfUserId);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        friendsArrayList.clear();
                        JSONArray friendObjArr = obj.getJSONArray("friend_list");
                        for (int i = 0; i < friendObjArr.length(); i++) {
                            JSONObject friendObj = (JSONObject) friendObjArr.get(i);
                            String idd = friendObj.getString("user_id");
                            if (idd != selfUserId) {
                                Friend friend = new Friend();

                                friend.setId(friendObj.getString("user_id"));
                                friend.setName(friendObj.getString("name"));
                                friend.setImage(friendObj.getString("image"));
                                friend.setLastMessage("");
                                friend.setUnreadCount(0);
                                friend.setTimestamp(friendObj.getString("created_at"));
                                friend.setStatus(friendObj.getString("user_status").equals("1"));
                                friendsArrayList.add(friend);


                            }
                        }

                    } else {
                        // error in fetching friend list
                        //  Toast.makeText(getActivity(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(GroupActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                friendListAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GroupActivity.this,"Network Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq);
    }


}
