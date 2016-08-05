package com.iwebnext.vchatt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.request.SignUpRequest;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;
import com.iwebnext.vchatt.utils.FilePathUtils;
import com.iwebnext.vchatt.utils.FileUploadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private int PICK_IMAGE_REQUEST = 1;

    Spinner spProfession;
    EditText etMedicalLicenseNo;
    EditText etEmail;
    private EditText etName, etState;

    ProgressDialog progressDialog;
    private Bitmap bitmap;
    ImageView profileImage;

    String uploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle bundle = getIntent().getExtras();
        String fbEmail = bundle.getString("email");
        etEmail.setText(fbEmail);

        spProfession = (Spinner) findViewById(R.id.spinner);
        etName = (EditText) findViewById(R.id.input_name);
        etEmail = (EditText) findViewById(R.id.input_email);
        final EditText etPassword = (EditText) findViewById(R.id.input_password);
        etMedicalLicenseNo = (EditText) findViewById(R.id.input_medical_license_number);
        etState = (EditText) findViewById(R.id.input_state);
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
                            boolean error = jsonResponse.getBoolean("error");
                            if (!error) {
                                String userId = jsonResponse.getString("user_id");
                                invokeImageUploadTask(userId);
                            } else {
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

                SignUpRequest registerRequest = new SignUpRequest(name, email, password, medicalLicenseNo, profession, state, responseListener, "", "", "", "0", "0");
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
                    bitmap = MediaStore.Images.Media.getBitmap(SignUpActivity.this.getApplicationContext().getContentResolver(), filePath);
                    //Setting the Bitmap to ImageView
                    profileImage.setImageBitmap(bitmap);

                    uploadFile = FilePathUtils.getPath(SignUpActivity.this, filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void invokeImageUploadTask(String userId) {
        String[] params = new String[]{uploadFile, userId, EndPoints.UPDATE_AVATAR};
        new UploadImageAsyncTask().execute(params);
    }

    /**
     * Inner class -- AsyncTask to upload image
     */
    class UploadImageAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return FileUploadUtils.uploadImage(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String v) {
            super.onPostExecute(v);
            dismissProgress();
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage("Registered Successfully")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.EXTRA_KEY_USER_EMAIL, etEmail.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {
            String accessToken = loginResult.getAccessToken().getToken();
            Log.i("accessToken", accessToken);

            // AccessToken accessToken = loginResult.getAccessToken();
            // System.out.println("access token"+accessToken);


            Profile profile = Profile.getCurrentProfile();

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {


                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login
                    //  Bundle bFacebookData = getFacebookData(object);
                    Profile profile = Profile.getCurrentProfile();

                    try {

                        etEmail.setText(object.getString("email"));
                        etName.setText(object.getString("name"));
                        etState.setText(object.getString("location"));


                        // String inputEmail = object.getString("email");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //saveNewUser();

                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,name,location"); // Parámetros que pedimos a facebook
            request.setParameters(parameters);
            request.executeAsync();
        }


        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };
}