package com.iwebnext.vchatt.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwebnext.vchatt.R;

/**
 * Created by PRIYANKA on 8/11/2016.
 */
public class AppExpireActivity extends AppCompatActivity {

    private ImageView logoImage;
    private TextView text1;
    private TextView text2;
    private Button btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_expire_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        logoImage = (ImageView) findViewById(R.id.logo);
        text1 = (TextView) findViewById(R.id.tv_text1);
        text2 = (TextView) findViewById(R.id.tv_text2);
        btnBuy = (Button) findViewById(R.id.btn_buy);


        btnBuy.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/in/webapps/mpp/home")));

            }
        });
    }
}
