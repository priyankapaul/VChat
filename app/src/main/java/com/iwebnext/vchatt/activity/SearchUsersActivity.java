package com.iwebnext.vchatt.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.fragment.SearchUsersFragment;

public class SearchUsersActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    SearchQueryListener fragmentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        SearchUsersFragment searchUsersFragment = new SearchUsersFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_container, searchUsersFragment).commit();
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
}
