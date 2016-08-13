package com.iwebnext.vchatt.activity;

/**
 * Created by PRIYANKA on 7/18/2016.
 */


import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;
import com.iwebnext.vchatt.utils.FilePathUtils;
import com.iwebnext.vchatt.utils.FileUploadUtils;

public class AttachVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AttachVideoActivity.class.getSimpleName();

    private static final int REQUEST_CODE_SELECT_VIDEO = 3;

    private String peerId;
    private Button buttonChoose;
    private Button buttonUpload;
    private TextView textView;
    private TextView textViewResponse;
    private VideoView attachVideo;

    private String selectedPath;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_video);

        Intent intent = getIntent();
        peerId = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_ID);

        buttonChoose = (Button) findViewById(R.id.buttonChooseVideo);
        buttonUpload = (Button) findViewById(R.id.buttonUploadVideo);
        attachVideo = (VideoView) findViewById(R.id.videoView);

        textView = (TextView) findViewById(R.id.textView);
        textViewResponse = (TextView) findViewById(R.id.textViewResponse);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        //   if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{"android.permission.READ_PHONE_STATE"}, 0);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        FileUploadUtils.PERMISSIONS_REQUEST_READ_MEDIA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FileUploadUtils.PERMISSIONS_REQUEST_READ_MEDIA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), REQUEST_CODE_SELECT_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_VIDEO) {
                Uri selectedVideoUri = intent.getData();
                selectedPath = FilePathUtils.getPath(AttachVideoActivity.this, selectedVideoUri);
                textView.setText(selectedPath);
                attachVideo.setVideoURI(selectedVideoUri);

                attachVideo.start();
            }
        }
    }

    private void invokeUploadTask() {
        UploadVideoAsyncTask uv = new UploadVideoAsyncTask();
        uv.execute();
    }


    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            chooseVideo();
        }
        if (v == buttonUpload) {
            invokeUploadTask();
        }
    }

    /**
     * Inner class -- AsyncTask to upload video
     */
    class UploadVideoAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AttachVideoActivity.this, "Sending File", "Please wait...", false, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String userId = BaseApplication.getInstance().getPrefManager().getUser().getId();
            FileUploadUtils.uploadVideo(selectedPath, userId, peerId, EndPoints.UPLOAD_VIDEO);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            progressDialog.dismiss();
        }

    }
}