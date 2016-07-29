package com.iwebnext.vchatt.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.request.ForgetPasswordRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PRIYANKA on 6/14/2016.
 */
public class ForgetPasswordActivity extends AppCompatActivity {

    private String UPLOAD_URL = "http://inextwebs.com/gcm_chat/include/forget_password.php";
    private String KEY_FORGET_PASSWORD = "forgetPassword";
    private EditText resetEmail;
    private TextView resetTitle;
    private Button btnReset, btnLogin;
    private TextView resetText;
    private DotProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forget_password);

        resetEmail = (EditText) findViewById(R.id.et_email_id);
        resetTitle = (TextView) findViewById(R.id.tv_reset_title);
        resetTitle = (TextView) findViewById(R.id.tv_sent_email);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnLogin = (Button) findViewById(R.id.btn_back_to_login);
        progressBar = (DotProgressBar) findViewById(R.id.dot_progress_bar);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                ForgetPasswordActivity.this.startActivity(registerIntent);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                final String forgetEmail = resetEmail.getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
                                builder.setMessage("Sending Email")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
                                builder.setMessage("Sending Email Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest(forgetEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ForgetPasswordActivity.this);
                queue.add(forgetPasswordRequest);
            }
        });



    }

}

