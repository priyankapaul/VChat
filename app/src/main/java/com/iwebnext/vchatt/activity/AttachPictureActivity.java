package com.iwebnext.vchatt.activity;

/**
 * Created by PRIYANKA on 6/21/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class AttachPictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button buttonChoosePicture;

    private String friendId;
    private EditText editTextName;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";

    private Bitmap bitmap;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attach_picture);

        Intent intent = getIntent();
        friendId = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_ID);

        buttonChoosePicture = (Button) findViewById(R.id.buttonChoosePicture);
        editTextName = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.iv_attach_image);
        buttonChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())

                {
                    case R.id.buttonChoosePicture:
                        showFileChooser();
                        break;

                }
            }
        });
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void uploadImage(Bitmap bitmap) {
        //Converting Bitmap to String
        final String image = getStringImage(bitmap);

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(AttachPictureActivity.this, "Sending...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {


                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(AttachPictureActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(AttachPictureActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final String selfUserId = BaseApplication.getInstance().getPrefManager().getUser().getId();

                //Getting Image Name
                String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                params.put("user_id", selfUserId);
                params.put("id", friendId);

                //returning parameters
                return params;

            }

        };
        BaseApplication.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {

                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(AttachPictureActivity.this.getApplicationContext().getContentResolver(), filePath);
                    //Setting the Bitmap to ImageView
                    imageView.setImageBitmap(bitmap);

                    uploadImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


}
