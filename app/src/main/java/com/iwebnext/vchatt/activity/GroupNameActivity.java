package com.iwebnext.vchatt.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwebnext.vchatt.R;

/**
 * Created by PRIYANKA on 10/15/2016.
 */
public class GroupNameActivity extends AppCompatActivity {

    TextView instruction;
    EditText groupName;
    ImageView groupImage;
    FloatingActionButton fabDone;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_name);

        groupName = (EditText) findViewById(R.id.group_name);
        groupImage = (ImageView) findViewById(R.id.group_image);
        instruction = (TextView) findViewById(R.id.instruction);
      FloatingActionButton fabDone = (FloatingActionButton) findViewById(R.id.tick);


    }
}
