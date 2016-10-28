package com.iwebnext.vchatt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iwebnext.vchatt.fragment.Demo1;
import com.iwebnext.vchatt.fragment.Demo2;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    public TabsPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Demo1 friendListFragment = new Demo1();
                return friendListFragment;
            case 1:
                Demo2 groupShowFragment = new Demo2();
                return groupShowFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
