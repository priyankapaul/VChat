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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.activity.ImageConverter;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.User;
import com.iwebnext.vchatt.request.FriendRequestCancelled;
import com.iwebnext.vchatt.request.FriendRequestSent;
import com.iwebnext.vchatt.utils.Constants;

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
    private Button btnAdd, btnCancel;
    private String TAG = FriendRequestSentFragment.class.getSimpleName();

    private User userToSendFriendRequest;

    public FriendRequestSentFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_friend, container, false);

        userToSendFriendRequest = (User) getArguments().getSerializable(Constants.EXTRA_KEY_USER);


        tvName = (TextView) rootView.findViewById(R.id.tv_name);

        btnAdd = (Button) rootView.findViewById(R.id.btn_add);
        btnAdd.setEnabled(true);
        tvId = (TextView) rootView.findViewById(R.id.tv_id);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.user_profile);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        ivImage = (ImageView) rootView.findViewById(R.id.iv_img);
        ivImage.setImageBitmap(circularBitmap);


        btnCancel = (Button) rootView.findViewById(R.id.btn_request_cancel);

        // set visibility depending upon friend request status
        int status = userToSendFriendRequest.getFriendRequestStatus();
        if (status == User.FRIEND_REQUEST_NOT_INITIATED || status == User.FRIEND_REQUEST_CANCELLED) {
            btnCancel.setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                btnCancel.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.GONE);

                final String id = new String(userToSendFriendRequest.getId());

                //final String id = tvId.getId().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                userToSendFriendRequest.setFriendRequestStatus(User.FRIEND_REQUEST_SENT);
                                btnAdd.setEnabled(true);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Friend request send successfully")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();

                            } else {
                                btnAdd.setEnabled(false);
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
        showUserProfile();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.GONE);

                final String id = new String(userToSendFriendRequest.getId());

                //final String id = tvId.getId().toString();

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
                                // Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                //SignUpActivity.this.startActivity(intent);
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
        return rootView;
    }

    private void showUserProfile() {
        tvId.setText(userToSendFriendRequest.getId());

        Log.i(TAG, "Friend id is" + userToSendFriendRequest.getId());
        tvName.setText(userToSendFriendRequest.getName());
        Log.i(TAG, "Friend name is" + userToSendFriendRequest.getName());
        new DownloadImage().execute(userToSendFriendRequest.getImage());
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
