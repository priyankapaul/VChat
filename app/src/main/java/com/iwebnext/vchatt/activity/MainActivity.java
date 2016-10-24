package com.iwebnext.vchatt.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.fragment.HomeFragment;
import com.iwebnext.vchatt.fragment.ProfileFragment;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    DrawerLayout drawer;
    private TextView navName;
    private String mPeerId,groupId;
    private ImageView navImage;
    FragmentManager fragmentManager;
    SearchQueryListener fragmentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        mPeerId = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_ID);
        groupId = intent.getStringExtra(Constants.EXTRA_KEY_GROUP_ID);


        HomeFragment homeFragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // Setup drawer view
        setupDrawerContent(navigationView);

        View hView =  navigationView.getHeaderView(0);


        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.user_profile);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
        navImage = (ImageView) hView.findViewById(R.id.nav_image);
        navImage.setImageBitmap(circularBitmap);
//        ImageView navImage = (ImageView)hView.findViewById(R.id.nav_image);
//        navImage.setImageBitmap(circularBitmap);

        navName = (TextView) hView.findViewById(R.id.nav_name);

        if (BaseApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
//            Intent i = new Intent(MainActivity.this,HomeFragment.class);
//            startActivity(i);
        }
    }


    private void launchLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        fetchNavDetails();
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {

                sendDataToFragment(searchQuery);
                return true;
            }
        });

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.action_group:
                Intent intent = new Intent(MainActivity.this,GroupActivity.class);
                startActivity(intent);
                break;

            case R.id.action_show_group:
                Intent i = new Intent(MainActivity.this, FriendGroupListActivity.class);
                startActivity(i);
                break;
            case R.id.action_logout:
                BaseApplication.getInstance().logout();
                break;
            case R.id.action_search:
                return true;

//            case R.id.action_attach_video:
//                Intent intent = new Intent(MainActivity.this,AttachVideoActivity.class);
//                startActivity(intent);
//
//
//                return true;
//            case R.id.action_attach_picture:
//             Intent i = new Intent(MainActivity.this,AttachPictureActivity.class);
//               startActivity(i);
//               //ap.showFileChooser();
//                return true;
        }

        int id = menuItem.getItemId();

        return super.onOptionsItemSelected(menuItem);

    }

    @SuppressWarnings("StatementWithEmptyBody")

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {

                        // Handle navigation view item clicks here.
                        int id = item.getItemId();
                        Fragment fragment = null;
                        Class fragmentClass = null;
                        if (id == R.id.nav_home) {
                            fragmentClass = HomeFragment.class;
                        } else if (id == R.id.nav_address) {
                            fragmentClass = ProfileFragment.class;
                        } else if (id == R.id.nav_phone) {
                            fragmentClass = ProfileFragment.class;
                        } else if (id == R.id.nav_website) {

                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://inextwebs.com/vchatt/")));
                            drawer.closeDrawers();
                            return true;
                        } else if (id == R.id.nav_friend_request) {

                            startActivity(new Intent(MainActivity.this, FriendRequestActivity.class));
                            drawer.closeDrawers();
                            return true;
                        }
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

                        item.setChecked(true);
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });


    }

    public void setFragmentListener(SearchQueryListener listener) {
        this.fragmentListener = listener;
    }

    public interface SearchQueryListener {
        void updateFragmentList(String query);
    }

    public void sendDataToFragment(String query) {
        this.fragmentListener.updateFragmentList(query);
    }

    private void fetchNavDetails() {

        final String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();
        String endPoint = EndPoints.NAV_DRAWER.replace("_ID_", selfUserId);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error

                    JSONArray profileArray = obj.getJSONArray("information");
                    for (int i = 0; i < profileArray.length(); i++) {
                        JSONObject profileObj = (JSONObject) profileArray.get(i);

                        navName.setText(profileObj.getString("name"));
                        new DownloadImage().execute(profileObj.getString("image"));

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                //  mAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                // Toast.makeText(getActivity(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
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
        navImage.setImageDrawable(drawable);
    }

}



