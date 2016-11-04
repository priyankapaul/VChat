package com.iwebnext.vchatt.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.adapter.GroupChatRoomThreadAdapter;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.app.Config;
import com.iwebnext.vchatt.gcm.NotificationUtils;
import com.iwebnext.vchatt.model.Message;
import com.iwebnext.vchatt.model.User;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;
import com.iwebnext.vchatt.utils.FileGroupUtils;
import com.iwebnext.vchatt.utils.FilePathUtils;
import com.iwebnext.vchatt.utils.FileUploadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupChatRoomActivity extends AppCompatActivity {

    private String TAG = GroupChatRoomActivity.class.getSimpleName();

    private static final int RC_PICK_IMAGE = 1;
    private static final int RC_SELECT_VIDEO = 2;

    private String mUserId;
    private String mUserName, mPeerImage;
    private String groupId;
    private RecyclerView mRvChatThread;
    private GroupChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> mMessageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText mInputMessage;
    private Button mBtnSend;

    String mUploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        final Intent intent = getIntent();
        groupId = intent.getStringExtra(Constants.EXTRA_KEY_GROUP_ID);
        final String title = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_NAME);
        final String mPeerImage = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_IMAGE);

        if (groupId == null)
        {
            Toast.makeText(getApplicationContext(), "Message not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitle(groupId);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupChatRoomActivity.this, FriendProfileActivity.class);
                i.putExtra(Constants.EXTRA_KEY_GROUP_ID, groupId);
                i.putExtra(Constants.EXTRA_KEY_FRIEND_NAME, title);
                i.putExtra(Constants.EXTRA_KEY_FRIEND_IMAGE, mPeerImage);
                startActivity(i);
            }
        });

        mInputMessage = (EditText) findViewById(R.id.message);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mRvChatThread = (RecyclerView) findViewById(R.id.recycler_view);
        mMessageArrayList = new ArrayList<>();
        // host user
        User user = BaseApplication.getInstance().getPrefManager().getUser();
        mUserId = user.getId();
        mUserName = user.getName();
        mAdapter = new GroupChatRoomThreadAdapter(this, mMessageArrayList, mUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvChatThread.setLayoutManager(layoutManager);
        mRvChatThread.setItemAnimator(new DefaultItemAnimator());
        Log.i(TAG, "madapter" + mAdapter);
        mRvChatThread.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchChatThread();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            int type = -1;
            if (requestCode == RC_PICK_IMAGE) {
                type = Message.IMAGE;
            } else if (requestCode == RC_SELECT_VIDEO) {
                type = Message.VIDEO;
            }
            Uri filePath = data.getData();
            mUploadFile = FilePathUtils.getPath(GroupChatRoomActivity.this, filePath);

            invokeImageUploadTask(type);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        FileUploadUtils.PERMISSIONS_REQUEST_READ_MEDIA);
            }
        }
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_logout:
                BaseApplication.getInstance().logout();
                break;
            case R.id.action_attach_image:
                chooseImage();
                return true;
            case R.id.action_attach_video:
                chooseVideo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     */
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra(Constants.EXTRA_KEY_PUSH_TYPE, -1);
        switch (type) {
            case Config.PUSH_TYPE_GROUP_CHATROOM:
                Message message = (Message) intent.getSerializableExtra(Constants.EXTRA_KEY_MESSAGE);
                String groupId = intent.getStringExtra(Constants.EXTRA_KEY_GROUP_ID);
                updateMessageList(message, groupId);
                break;
        }
    }

    private void updateMessageList(Message message, String groupId) {
        if (message != null && groupId != null)
        {

            mMessageArrayList.add(message);
            mAdapter.notifyDataSetChanged();

            if (mAdapter.getItemCount() > 1)
            {
                mRvChatThread.getLayoutManager().smoothScrollToPosition(mRvChatThread, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     */
    private void sendMessage() {
        final String message = this.mInputMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        String endPoint = EndPoints.ADD_GROUP_MESSAGE;

        Log.e(TAG, "endpoint: " + endPoint);

        this.mInputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (!obj.getBoolean("error")) {

                        JSONObject msgObj = obj.getJSONObject("message");

                        String msgId = msgObj.getString("group_message_id");
                        String content = msgObj.getString("content");
                        String createdAt = msgObj.getString("created_at");
                        int type = msgObj.getInt("type");

                        // since it is a sent message, userId and userName should be that of Host's
                        Message message = new Message(msgId, content, createdAt, mUserId, mUserName, type);

                        mMessageArrayList.add(message);

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            mRvChatThread.getLayoutManager().smoothScrollToPosition(mRvChatThread, null, mAdapter.getItemCount() - 1);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Network error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                mInputMessage.setText(message);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", BaseApplication.getInstance().getPrefManager().getUser().getId());
                params.put("group_id", groupId);
                params.put("message", message);

                Log.e(TAG, "Params: " + params.toString());

                return params;
            }

            ;
        };
        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq);
    }

    /**
     * Fetching all the messages of a single user
     */
    private void fetchChatThread() {

        String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();
        String endPoint = EndPoints.FETCH_GROUP_MESSAGES.replace("_ID_", groupId);


        endPoint = endPoint.replace("_MY_", selfUserId);
        Log.e(TAG, "endPoint: " + endPoint);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                parseMessageList(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Network error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq);
    }

    // Attach image
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE);
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), RC_SELECT_VIDEO);
    }

    private void invokeImageUploadTask(int type) {
        String userId = BaseApplication.getInstance().getPrefManager().getUser().getId();
        String[] params = new String[]{mUploadFile, EndPoints.UPLOAD_GROUP_FILE, userId, groupId, String.valueOf(type)};
        new UploadImageAsyncTask().execute(params);
    }

    /**
     * Inner class -- AsyncTask to upload image
     */
    class UploadImageAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(GroupChatRoomActivity.this, "Sending...", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(String... params)
        {
            return FileGroupUtils.uploadFile(params[0], params[1], params[2], params[3], params[4]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            parserMediaResponse(result);
        }
    }

    private void parserMediaResponse(String response) {
        if (response == null) {
            Toast.makeText(this, "Failed to upload!", Toast.LENGTH_SHORT);
            return;
        }
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getBoolean("error") == false) {
                JSONObject msgObj = obj.getJSONObject("message");

                String content = msgObj.getString("content");
                String commentId = msgObj.getString("message_id");
                String createdAt = msgObj.getString("created_at");
                int type = msgObj.getInt("type");

                Message message = new Message(commentId, content, createdAt, mUserId, mUserName, type);
                mMessageArrayList.add(message);

                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1) {
                    mRvChatThread.getLayoutManager().smoothScrollToPosition(mRvChatThread, null, mAdapter.getItemCount() - 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseMessageList(String response)
    {
        try {
            JSONObject obj = new JSONObject(response);
            // check for error
            if (obj.getBoolean("error") == false) {
                JSONArray msgJsonArr = obj.getJSONArray("messages");

                for (int i = 0; i < msgJsonArr.length(); i++) {
                    JSONObject msgObj = (JSONObject) msgJsonArr.get(i);

                    String content = msgObj.getString("content");
                    String commentId = msgObj.getString("message_id");
                    String createdAt = msgObj.getString("created_at");
                    int type = msgObj.getInt("type");

                    // parse User object
                    JSONObject userObj = msgObj.getJSONObject("user");
                    String userId = userObj.getString("user_id");
                    String userName = userObj.getString("user_name");

                    Message message = new Message(commentId, content, createdAt, userId, userName, type);
                    mMessageArrayList.add(message);
                }

                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1) {
                    mRvChatThread.getLayoutManager().smoothScrollToPosition(mRvChatThread, null, mAdapter.getItemCount() - 1);
                }
            } else {
                //Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.e(TAG, "json parsing error: " + e.getMessage());
            //Toast.makeText(getApplicationContext(), "json parse error: " + e.getContent(), Toast.LENGTH_SHORT).show();
        }
    }
}
