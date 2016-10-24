package com.iwebnext.vchatt.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.activity.ChatRoomActivity;
import com.iwebnext.vchatt.activity.MainActivity;
import com.iwebnext.vchatt.activity.SearchUsersActivity;
import com.iwebnext.vchatt.adapter.FriendListAdapter;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.app.Config;
import com.iwebnext.vchatt.gcm.GcmIntentService;
import com.iwebnext.vchatt.gcm.NotificationUtils;
import com.iwebnext.vchatt.helper.SimpleDividerItemDecoration;
import com.iwebnext.vchatt.model.Friend;
import com.iwebnext.vchatt.model.Message;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Priyanka on 6/3/2016.
 */
public class HomeFragment extends Fragment implements MainActivity.SearchQueryListener {
    private String TAG = HomeFragment.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private ArrayList<Friend> friendsArrayList;
    private ArrayList<Friend> backupFriendsArrayList;

    private FriendListAdapter friendListAdapter;
    private RecyclerView rvFriends;

    public HomeFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).setFragmentListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);
        rvFriends = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchUsersActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Broadcast receiver calls in two scenarios
         * 1. gcm registration is completed
         * 2. when new push notification is received
         * */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    subscribeToGlobalTopic();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                    Log.e(TAG, "GCM registration id is sent to our server");

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };

        backupFriendsArrayList = new ArrayList<>();
        friendsArrayList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(getActivity(), friendsArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvFriends.setLayoutManager(layoutManager);
        rvFriends.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity()
        ));
        rvFriends.setItemAnimator(new DefaultItemAnimator());
        rvFriends.setAdapter(friendListAdapter);

        rvFriends.addOnItemTouchListener(new FriendListAdapter.RecyclerTouchListener(getActivity(), rvFriends, new FriendListAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                Friend friend = friendsArrayList.get(position);
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_FRIEND_ID, friend.getId());
                intent.putExtra(Constants.EXTRA_KEY_FRIEND_NAME, friend.getName());
                intent.putExtra(Constants.EXTRA_KEY_FRIEND_IMAGE, friend.getImage());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /**
         * Always check for google play services availability before
         * proceeding further with GCM
         * */
        if (checkPlayServices()) {
            registerGCM();
            fetchFriendList();
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing getActivity(), the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(getActivity(), GcmIntentService.class);
        intent.putExtra("key", "register");
        getActivity().startService(intent);
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "getActivity() device is not supported. Google Play Services not installed!");
                Toast.makeText(getActivity(), "getActivity() device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Handles new push notification
     */
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);
        Message message;
        String friendId;
        switch (type) {
            case Config.PUSH_TYPE_CHATROOM:
                message = (Message) intent.getSerializableExtra(Constants.EXTRA_KEY_MESSAGE);
                friendId = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_ID);

                if (message != null && friendId != null) {
                    updateRow(friendId, message);
                }
                break;
            case Config.PUSH_TYPE_USER:
                // push belongs to user alone
                // just showing the message in a toast
                message = (Message) intent.getSerializableExtra(Constants.EXTRA_KEY_MESSAGE);
                Toast.makeText(getActivity(), "New push: " + message.getContent(), Toast.LENGTH_LONG).show();
                break;
            case Config.PUSH_TYPE_USER_STATUS:
                // update specific user in friend list
                friendId = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_ID);
                boolean userStatus = intent.getBooleanExtra(Constants.EXTRA_KEY_USER_STATUS, false);
                for (Friend friend : friendsArrayList) {
                    if (friend.getId().equals(friendId)) {
                        friend.setStatus(userStatus);
                        friendListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                break;
        }


    }

    /**
     * Updates the chat list unread count and the last message
     */

    private void updateRow(String friendId, Message message) {
        for (Friend friend : friendsArrayList) {
            if (friend.getId().equals(friendId)) {
                int index = friendsArrayList.indexOf(friend);
                friend.setLastMessage(message.getContent());
                friend.setUnreadCount(friend.getUnreadCount() + 1);
                friendsArrayList.remove(index);
                friendsArrayList.add(index, friend);
                break;
            }
        }
        friendListAdapter.notifyDataSetChanged();
    }


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
                                backupFriendsArrayList.add(friend);
                            }
                        }

                    } else {
                        // error in fetching friend list
                        Toast.makeText(getActivity(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                friendListAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq);
    }

    // subscribing to global topic
    private void subscribeToGlobalTopic() {
        Intent intent = new Intent(getActivity(), GcmIntentService.class);
        intent.putExtra(GcmIntentService.KEY, GcmIntentService.SUBSCRIBE);
        intent.putExtra(GcmIntentService.TOPIC, Config.TOPIC_GLOBAL);
        getActivity().startService(intent);
    }

    @Override
    public void updateFragmentList(String query) {
        friendsArrayList.clear();

        for (Friend friend : backupFriendsArrayList) {
            String s1 = query.toLowerCase();
            String s2 = friend.getName().toLowerCase();
            if (s2.contains(s1)) {
                friendsArrayList.add(friend);
            }

            friendListAdapter.notifyDataSetChanged();
        }

    }


}
