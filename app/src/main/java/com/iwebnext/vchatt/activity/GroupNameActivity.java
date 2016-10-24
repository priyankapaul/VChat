package com.iwebnext.vchatt.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.utils.EndPoints;
import com.iwebnext.vchatt.utils.FileGroupUtils;
import com.iwebnext.vchatt.utils.FilePathUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by PRIYANKA on 10/15/2016.
 */
public class GroupNameActivity extends AppCompatActivity {
    private final static String TAG = GroupNameActivity.class.getSimpleName();
    private int PICK_IMAGE_REQUEST = 1;

    TextView instruction;
    EditText groupName;
    private String mPeerId, title, mPeerImage;
    ImageView groupImage;
    FloatingActionButton fabDone;
    ProgressDialog progressDialog;
    ArrayList<String> groupList = new ArrayList<String>();
    private Bitmap bitmap;
    String uploadFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_name);

        groupName = (EditText) findViewById(R.id.group_name);
        instruction = (TextView) findViewById(R.id.instruction);
        Button btnDone = (Button) findViewById(R.id.tick);

        groupList = (ArrayList<String>) getIntent().getSerializableExtra("groupList");
        System.out.println("list iss " + groupList);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.user_circle);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        groupImage = (ImageView) findViewById(R.id.group_image);
        if (groupImage != null) {
            groupImage.setImageBitmap(circularBitmap);
        }

        final ImageButton selectPicture = (ImageButton) findViewById(R.id.btn_select_group_picture);

        if (selectPicture != null) {
            selectPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == selectPicture) {
                        showFileChooser();
                    }
                }
            });
        }


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = groupName.getText().toString();

                final String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();
                invokeImageUploadTask(selfUserId, name, groupList);
            }
        });


        if (ContextCompat.checkSelfPermission(GroupNameActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GroupNameActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(GroupNameActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        FileGroupUtils.PERMISSIONS_REQUEST_READ_MEDIA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

//        final Intent intent = getIntent();
//        mPeerId = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_ID);
//
//        final String title = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_NAME);
//      //  final String mPeerImage = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_IMAGE);

    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading in progress!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(GroupNameActivity.this.getApplicationContext().getContentResolver(), filePath);
                    //Setting the Bitmap to ImageView
                    groupImage.setImageBitmap(bitmap);

                    uploadFile = FilePathUtils.getPath(GroupNameActivity.this, filePath);
                    // invokeImageUploadTask();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void invokeImageUploadTask(String userId, String groupName, ArrayList<String> groupList) {
        String[] params = new String[]{uploadFile, userId, groupName, String.valueOf(groupList), EndPoints.UPDATE_GROUP_AVATAR};

        new UploadImageAsyncTask().execute(params);
    }

    /**
     * Inner class -- AsyncTask to upload image
     */
    class UploadImageAsyncTask extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(GroupNameActivity.this, "Uploading...", "Please wait...", false, false);
        }

        @Override
        protected Void doInBackground(String... params) {
            FileGroupUtils.uploadAvatar(params[0], params[1], params[2], params[3], params[4]);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Group creation successfull", Toast.LENGTH_SHORT).show();
//            Intent i=new Intent(GroupNameActivity.this,GroupChatRoomActivity.class);
//
//            startActivity(i);

        }
    }


}
