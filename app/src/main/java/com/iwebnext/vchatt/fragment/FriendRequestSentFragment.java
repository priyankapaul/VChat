package com.iwebnext.vchatt.fragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.activity.ImageConverter;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.User;
import com.iwebnext.vchatt.request.AcceptFriendRequest;
import com.iwebnext.vchatt.request.DeclineFriendRequest;
import com.iwebnext.vchatt.request.FriendRequestCancelled;
import com.iwebnext.vchatt.request.FriendRequestSent;
import com.iwebnext.vchatt.request.RemoveFriend;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by PRIYANKA on 7/4/2016.
 */
public class FriendRequestSentFragment extends Fragment {
    private TextView tvName, tvId;
    private ImageView ivImage;
    private Button btnSend, btnCancel, btnRemove, btnAccept, btnDecline;
    private String TAG = FriendRequestSentFragment.class.getSimpleName();

    private User userToSendFriendRequest;
    //    String  mPeerId = userToSendFriendRequest.getId();
    public FriendRequestSentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_friend, container, false);

        userToSendFriendRequest = (User) getArguments().getSerializable(Constants.EXTRA_KEY_USER);
        String  mPeerId =   userToSendFriendRequest.getId();

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        tvName = (TextView) rootView.findViewById(R.id.tv_name);

        btnSend = (Button) rootView.findViewById(R.id.btn_send);

        btnRemove = (Button) rootView.findViewById(R.id.btn_remove);

        btnAccept = (Button) rootView.findViewById(R.id.btn_accept);

        btnCancel = (Button) rootView.findViewById(R.id.btn_request_cancel);

        btnDecline = (Button) rootView.findViewById(R.id.btn_decline);

        tvId = (TextView) rootView.findViewById(R.id.tv_id);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.user_profile);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        ivImage = (ImageView) rootView.findViewById(R.id.iv_img);
        ivImage.setImageBitmap(circularBitmap);

        showUserProfile();

        // set visibility depending upon friend request status
//        int status = userToSendFriendRequest.getFriendRequestStatus();
//        if (status == User.FRIEND_REQUEST_NOT_INITIATED || status == User.FRIEND_REQUEST_CANCELLED) {
//            btnCancel.setVisibility(View.GONE);
//            btnSend.setVisibility(View.VISIBLE);
//        } else {
//            btnCancel.setVisibility(View.VISIBLE);
//            btnSend.setVisibility(View.GONE);
//        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = new String(userToSendFriendRequest.getId());

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                userToSendFriendRequest.setFriendRequestStatus(User.FRIEND_REQUEST_SENT);
                                btnSend.setEnabled(true);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Friend request send successfully")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();

                            } else {
                                btnSend.setEnabled(false);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Already sent")
                                        // .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                FriendRequestSent addRequest = new FriendRequestSent(id, responseListener);
                BaseApplication.getInstance().addToRequestQueue(addRequest);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String id = new String(userToSendFriendRequest.getId());

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                userToSendFriendRequest.setFriendRequestStatus(User.FRIEND_REQUEST_CANCELLED);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("friend request cancelled")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                FriendRequestCancelled cancelRequest = new FriendRequestCancelled(id, responseListener);
                BaseApplication.getInstance().addToRequestQueue(cancelRequest);

            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String id = new String(userToSendFriendRequest.getId());
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                userToSendFriendRequest.setFriendRequestStatus(User.FRIEND_REMOVE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("remove friend successfully")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                RemoveFriend remove = new RemoveFriend(id, responseListener);
                BaseApplication.getInstance().addToRequestQueue(remove);

            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String id = new String(userToSendFriendRequest.getId());
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                userToSendFriendRequest.setFriendRequestStatus(User.FRIEND_REQUEST_ACCEPTED);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Friend added successfully")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                AcceptFriendRequest accept = new AcceptFriendRequest(id, responseListener);
                BaseApplication.getInstance().addToRequestQueue(accept);

            }
        });
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String id = new String(userToSendFriendRequest.getId());
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                userToSendFriendRequest.setFriendRequestStatus(User.FRIEND_REQUEST_DECLINE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Friend Request decline")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                DeclineFriendRequest decline = new DeclineFriendRequest(id, responseListener);
                BaseApplication.getInstance().addToRequestQueue(decline);

            }
        });
        return rootView;
    }


    private void showUserProfile() {
        tvId.setText(userToSendFriendRequest.getId());
        //   Log.i(TAG, "Friend id is" + userToSendFriendRequest.getId());
        tvName.setText(userToSendFriendRequest.getName());
        //   Log.i(TAG, "Friend name is" + userToSendFriendRequest.getName());
        new DownloadImage().execute(userToSendFriendRequest.getImage());
        allRequest();
    }

    private void allRequest() {
        String  mPeerId = userToSendFriendRequest.getId();
        final String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();
        String endPoint = EndPoints.ALL_REQUEST.replace("_ID_", mPeerId);
        endPoint = endPoint.replace("_MY_", selfUserId);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean sent_friend_request = jsonResponse.getBoolean("sent_friend_request");
                    boolean cancel_request = jsonResponse.getBoolean("cancel_request");
                    boolean remove_friend = jsonResponse.getBoolean("remove_friend");
                    boolean accept_friend = jsonResponse.getBoolean("accept_friend");
                    boolean decline_friend_request = jsonResponse.getBoolean("decline_friend_request");


                    if(sent_friend_request){
                        btnSend.setVisibility(View.VISIBLE);
                    }
                    if(cancel_request){
                        btnCancel.setVisibility(View.VISIBLE);
                    }
                    if(remove_friend){
                        btnRemove.setVisibility(View.VISIBLE);
                    }
                    if(accept_friend){
                        btnAccept.setVisibility(View.VISIBLE);
                        btnDecline.setVisibility(View.VISIBLE);
                    }
                    if(decline_friend_request){
                        btnAccept.setVisibility(View.VISIBLE);
                        btnDecline.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq);
    }

    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        /**
         * Called after the nivUser has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image) {
            setImage(image);
        }


        /**
         * Actually download the Image from the _url
         *
         * @param _url
         * @return
         */
        private Drawable downloadImage(String _url) {
            //Prepare to download nivUser
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();

                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return new BitmapDrawable(bMap);
            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }
    }

    protected void setImage(Drawable drawable) {
        ivImage.setImageDrawable(drawable);
    }

}
