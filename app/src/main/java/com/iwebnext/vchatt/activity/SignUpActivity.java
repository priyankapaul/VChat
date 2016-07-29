package com.iwebnext.vchatt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.MyApplication;
import com.iwebnext.vchatt.request.SignUpRequest;
import com.iwebnext.vchatt.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL = "http://inextwebs.com/gcm_chat/include/profile_photo.php";

    private String KEY_IMAGE = "image";
    Spinner spProfession;
    EditText etMedicalLicenseNo;
    ProgressDialog progressDialog;
    private Bitmap bitmap;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spProfession = (Spinner) findViewById(R.id.spinner);
        final EditText etName = (EditText) findViewById(R.id.input_name);
        final EditText etEmail = (EditText) findViewById(R.id.input_email);
        final EditText etPassword = (EditText) findViewById(R.id.input_password);
        etMedicalLicenseNo = (EditText) findViewById(R.id.input_medical_license_number);
        final EditText etState = (EditText) findViewById(R.id.input_state);
        final Button bRegister = (Button) findViewById(R.id.btn_create_account);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.user_profile);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        profileImage = (ImageView) findViewById(R.id.input_image);
        profileImage.setImageBitmap(circularBitmap);

        final ImageButton selectPicture = (ImageButton) findViewById(R.id.btn_select_picture);

        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == selectPicture) {
                    showFileChooser();
                }
            }
        });

        spProfession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        showMedicalLicenseNumber(true);
                        break;
                    case 0:
                    default:
                        showMedicalLicenseNumber(false);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();
                //  final String degree = etDegree.getText().toString();
                final String medicalLicenseNo = etMedicalLicenseNo.getText().toString();
                final String state = etState.getText().toString();
                final String profession = spProfession.getSelectedItem().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            dismissProgress();
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage("Registered Successfully")
                                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                intent.putExtra(Constants.EXTRA_KEY_USER_EMAIL, email);
                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }
                                        })
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setTitle("Register Failed")
                                        .setMessage("Please try later")
                                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                setResult(RESULT_CANCELED);
                                                finish();
                                            }
                                        })
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            dismissProgress();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setTitle("Register Failed")
                                    .setMessage("Please try later")
                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setResult(RESULT_CANCELED);
                                            finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                };

                SignUpRequest registerRequest = new SignUpRequest(name, email, password, medicalLicenseNo, profession, state, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
                queue.add(registerRequest);
                showProgress();
            }
        });
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Registration in progress!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    private void showMedicalLicenseNumber(boolean b) {
        etMedicalLicenseNo.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    // Convert Bitmap to Base 64String

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("nivUser/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void uploadImage(Bitmap bitmap) {
        //Converting Bitmap to String
        final String image = getStringImage(bitmap);

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(SignUpActivity.this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(SignUpActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();


                //Getting Image Name
                //  String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put("user_id", selfUserId);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(SignUpActivity.this.getApplicationContext().getContentResolver(), filePath);
                    //Setting the Bitmap to ImageView
                    profileImage.setImageBitmap(bitmap);

                    uploadImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}