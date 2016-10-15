package com.iwebnext.vchatt.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.adapter.FriendProfileAdapter;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.Friend;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendProfileActivity extends AppCompatActivity {
    private String TAG = FriendProfileActivity.class.getSimpleName();
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private String peerId,peerName,peerImage;
    private RecyclerView recyclerView;
    private FriendProfileAdapter friendProfileAdapter;
    private ArrayList<Friend> friendProfileList;
    private GoogleApiClient client;
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_profile_activity);

        final Intent intent = getIntent();
        peerName = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_NAME);

        peerImage = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_IMAGE);
        System.out.println("peer image" + peerImage);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        NetworkImageView imgColapse = (NetworkImageView) findViewById(R.id.img_collapse);
        imgColapse.setImageUrl(peerImage, BaseApplication.getInstance().getImageLoader());

        collapsingToolbarLayout.setTitle(peerName);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        //  collapsingToolbarLayout.setBackground(Drawable.createFromPath(peerImage));
        Context context = this;
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.indigo_600));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.indigo_900));


        dynamicToolbarColor();
        toolbarTextAppernce();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setStatusBarTranslucent(true);

        Intent i = getIntent();
        peerId = i.getStringExtra(Constants.EXTRA_KEY_FRIEND_ID);


        recyclerView = (RecyclerView)findViewById(R.id.rv_friend_profile);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        friendProfileList = new ArrayList<>();
        friendProfileAdapter = new FriendProfileAdapter(friendProfileList);
        recyclerView.setAdapter(friendProfileAdapter);
        fetchFriendProfile();


        imgColapse.setOnClickListener(new View.OnClickListener() {
            //Start new list activity
            public void onClick(View v) {
                Intent mainIntent = new Intent(FriendProfileActivity.this, FriendImageDownload.class);
                mainIntent.putExtra(Constants.EXTRA_KEY_FRIEND_IMAGE, peerImage);
                startActivity(mainIntent);
            }
        });




    }


    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void dynamicToolbarColor() {


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.user_circle);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {

            @Override
            public void onGenerated(Palette palette) {


                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                if (vibrantSwatch != null) {
                    collapsingToolbarLayout.setBackgroundColor(vibrantSwatch.getRgb());
                    collapsingToolbarLayout.setBackgroundColor(vibrantSwatch.getRgb());

                }

            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void fetchFriendProfile() {

        String endPoint = EndPoints.FRIEND_PROFILE.replace("_ID_", peerId);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray friendObjArr = obj.getJSONArray("friend_profile");

                        JSONObject friendObj = (JSONObject) friendObjArr.get(0);
                        Friend friend = new Friend();
                        friend.setId(friendObj.getString("user_id"));
                        friend.setName(friendObj.getString("name"));

                        friend.setTelephone(friendObj.getString("telephone"));
                        friend.setAddress(friendObj.getString("address"));
                        friend.setProfession(friendObj.getString("profession"));

                        friendProfileList.add(friend);
                    }
                    else {
                        // error in fetching friend list
                        //  Toast.makeText(getActivity(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(FriendProfileActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                friendProfileAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FriendProfileActivity.this,"Network Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq);
    }




}