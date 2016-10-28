package com.iwebnext.vchatt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iwebnext.vchatt.R;

/**
 * Created by PRIYANKA on 10/25/2016.
 */
public class Demo2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View android = inflater.inflate(R.layout.group_list_fragment_layout, container, false);
        ((TextView)android.findViewById(R.id.tv_group)).setText("Groups");
        return android;
    }}
