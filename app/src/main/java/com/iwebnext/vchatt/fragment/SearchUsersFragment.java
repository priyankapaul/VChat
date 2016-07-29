package com.iwebnext.vchatt.fragment;

/**
 * Created by PRIYANKA on 6/27/2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gcm.GCMRegistrar;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.adapter.SearchAllUserAdapter;
import com.iwebnext.vchatt.app.MyApplication;
import com.iwebnext.vchatt.helper.SimpleDividerItemDecoration;
import com.iwebnext.vchatt.model.User;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class SearchUsersFragment extends Fragment {
    private String TAG = SearchUsersFragment.class.getSimpleName();

    private Map<String, String> params;

    private ArrayList<User> userList;
    private ArrayList<User> backUpUserList;

    private SearchAllUserAdapter mAdapter;
    private RecyclerView recyclerView;
    private EditText etUserName;
    private Button btnSearch;

    public SearchUsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.search_all_user_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.user_recycler_view);
        etUserName = (EditText) rootView.findViewById(R.id.user_name);
        btnSearch = (Button) rootView.findViewById(R.id.button_search);
        GCMRegistrar.checkDevice(getContext());
        GCMRegistrar.checkManifest(getContext());
        final String regId = GCMRegistrar.getRegistrationId(getActivity());
        Log.v("GCM ID", "GCM ID is " + regId);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity()
        ));


        backUpUserList = new ArrayList<>();
        userList = new ArrayList<>();
        mAdapter = new SearchAllUserAdapter(getActivity(), userList);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new SearchAllUserAdapter.RecyclerTouchListener(getActivity(), recyclerView, new SearchAllUserAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FriendRequestSentFragment fragment = new FriendRequestSentFragment();

                // send data from Activity to Fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.EXTRA_KEY_USER, mAdapter.getItem(position));
                // bundle.putSerializable(Constants.EXTRA_KEY_USER, mAdapter.getItemId(position));

                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        //fetchAllUserList();

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (etUserName.getText().toString().length() == 0)
                    etUserName.setError("Please enter name!");
                else {
                    userList.clear();
                    recyclerView.setVisibility(View.VISIBLE);
                    String name = etUserName.getText().toString();
                    fetchAllUserList(name);
                }

            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * fetching the friend list
     */
    private void fetchAllUserList(String name) {

        String url = EndPoints.SEARCH_ALL_USER + "?search=" + name;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.e(TAG, "response: " + response);

                try {
                    userList.clear();
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray chatRoomsArray = obj.getJSONArray("list");
                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);

                            User user = new User();
                            user.setId(chatRoomsObj.getString("user_id"));
                            user.setName(chatRoomsObj.getString("name"));
                            user.setImage(chatRoomsObj.getString("image"));

                            userList.add(user);
                            backUpUserList.add(user);

                        }


                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getActivity(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                mAdapter.notifyDataSetChanged();
                // backUpUserList.clear();
                // subscribing to all chat room topics
                //subscribeToAllTopics();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                VolleyLog.e("Network Error: ", error.getMessage());
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
/*
    @Override
    public void updateFragmentList(String query) {

        recyclerView.setVisibility(View.VISIBLE);
        userList.clear();

        for (User user : backUpUserList) {
            String s1 = query.toLowerCase();
            String s2 = user.getName().toLowerCase();
            if (s2.contains(s1)) {
                userList.add(user);
            }
        }
    }
    */
}
