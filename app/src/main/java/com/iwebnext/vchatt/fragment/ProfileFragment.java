package com.iwebnext.vchatt.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.activity.ImageConverter;
import com.iwebnext.vchatt.app.MyApplication;
import com.iwebnext.vchatt.model.Profile;
import com.iwebnext.vchatt.request.ProfileRequest;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ProfileFragment extends Fragment {

    private static final int REQ_CODE_CREATE_ALBUM = 1111;

    private String TAG = ProfileFragment.class.getSimpleName();
    private ArrayList<Profile> profileArrayList = new ArrayList<>();

    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL = "http://inextwebs.com/gcm_chat/include/profile_photo.php";

    private String KEY_IMAGE = "image";
    //   private String KEY_ID = "USERID";


    EditText etName;
    EditText etEmail;
    EditText etPhone;
    EditText etPassword;
    EditText etAddress;
    EditText etMedicalLicenseNo;
    EditText etProfession;
    EditText etWebsite;
    Button btnAction;
    Button btnCancel;


    private Bitmap bitmap;
    FrameLayout flContent;
    ImageButton fabProfile;
    ImageView imageView;
    //    NetworkImageView nivProfile;
    ProgressDialog dialog;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_profile_layout, container, false);
        flContent = (FrameLayout) rootView.findViewById(R.id.flContent);
        etName = (EditText) rootView.findViewById(R.id.et_name);
        etEmail = (EditText) rootView.findViewById(R.id.et_email);
        etPhone = (EditText) rootView.findViewById(R.id.et_phone);
        etPassword = (EditText) rootView.findViewById(R.id.et_password);
        etAddress = (EditText) rootView.findViewById(R.id.et_address);
        etProfession = (EditText) rootView.findViewById(R.id.et_profession);
        etWebsite = (EditText) rootView.findViewById(R.id.et_website);
        fabProfile = (ImageButton) rootView.findViewById(R.id.change_picture);
        etMedicalLicenseNo = (EditText) rootView.findViewById(R.id.et_medicalLicenseNo);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.user_profile);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        imageView = (ImageView) rootView.findViewById(R.id.show_image);
        imageView.setImageBitmap(circularBitmap);



        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == fabProfile) {
                    showFileChooser();
                }
            }
        });

        //call profile method
        fetchProfile();
        // disable all editable fields at startup
        enableAllEditableFields(false);

        final String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
        updateRow(selfUserId);

        btnAction = (Button) rootView.findViewById(R.id.btn_action);
        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);


        // set onclick listener for action button
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = btnAction.getText().toString();
                if (txt.equalsIgnoreCase("Edit")) {
                    btnAction.setText("Save");
                    btnCancel.setVisibility(View.VISIBLE);

                    // enable all editable fields
                    enableAllEditableFields(true);
                } else {
                    //show progress in the meantime
                    showProgress();
                    // SEND YOUR SIGNUP REQUEST WITH NEW DATA


                    final String name = etName.getText().toString();
                    final String email = etEmail.getText().toString();
                    final String password = etPassword.getText().toString();
                    final String address = etAddress.getText().toString();
                    final String telephone = etPhone.getText().toString();

                    final String medicalLicenseNo = etMedicalLicenseNo.getText().toString();
                    final String profession = etProfession.getText().toString();
                    final String website = etWebsite.getText().toString();


                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Profile Updated Successfully")
                                            .setNegativeButton("ok", null)
                                            .create()
                                            .show();
                                    // Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    //SignUpActivity.this.startActivity(intent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Loading Profile Failed")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    ProfileRequest registerRequest = new ProfileRequest(selfUserId, name, email, password, address, telephone, medicalLicenseNo, profession, website, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    queue.add(registerRequest);

                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancel.setVisibility(View.GONE);
                btnAction.setText("Edit");

                // disable all editable fields
                enableAllEditableFields(false);
            }
        });
        return rootView;


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


    private void uploadImage(Bitmap bitmap) {
        //Converting Bitmap to String
        final String image = getStringImage(bitmap);

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

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
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                    //Setting the Bitmap to ImageView
                    imageView.setImageBitmap(bitmap);

                    uploadImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void updateRow(String profileId) {
        for (Profile cr : profileArrayList) {
            if (cr.getId().equals(profileId)) {
                int index = profileArrayList.indexOf(cr);
                profileArrayList.remove(index);
                profileArrayList.add(index, cr);
                break;
            }
        }
        //mAdapter.notifyDataSetChanged();
    }


    private void enableAllEditableFields(boolean enable) {
        etName.setEnabled(enable);
        etEmail.setEnabled(enable);
        etPhone.setEnabled(enable);
        etAddress.setEnabled(enable);
        etProfession.setEnabled(enable);
        etWebsite.setEnabled(enable);
        etMedicalLicenseNo.setEnabled(enable);
        etPassword.setEnabled(enable);
    }

    private void showProgress() {
        dialog = ProgressDialog.show(getActivity(), "Please wait!", "Saving data");
        long delayInMillis = 5000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, delayInMillis);

    }


    private void cancelProgress() {
        if (dialog != null)
            dialog.cancel();
    }


//response part
/*
    private void populateDummyData() {
        ///   etName.setText("Mimi Name");
        //   etEmail.setText("mimi@gmail.com");
        //   etPhone.setText("12345678");
        //   etAddress.setText("City Center");
        final String name = etName.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String medicalLicenseNo = etMedicalLicenseNo.getText().toString();
        final String address = etAddress.getText().toString();
        final String phone = etPhone.getText().toString();
        final String profession = etProfession.getText().toString();
       // final String nivUser = ivImage.getIm()
       */

    private void fetchProfile() {

        final String selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();
        String endPoint = EndPoints.PROFILE.replace("_ID_", selfUserId);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error

                    JSONArray profileArray = obj.getJSONArray("information");
                    for (int i = 0; i < profileArray.length(); i++) {
                        JSONObject profileObj = (JSONObject) profileArray.get(i);

                        etEmail.setText(profileObj.getString("email"));
                        etName.setText(profileObj.getString("name"));
                        etPhone.setText(profileObj.getString("telephone"));
                        etPassword.setText(profileObj.getString("password"));
                        etAddress.setText(profileObj.getString("address"));
                        etProfession.setText(profileObj.getString("profession"));
                        etWebsite.setText(profileObj.getString("website"));
                        etMedicalLicenseNo.setText(profileObj.getString("medical_license_no"));
                        new DownloadImage().execute(profileObj.getString("image"));

                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                //  mAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                // Toast.makeText(getActivity(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        /**
         * Called after the nivUser has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image) {
            setImage(image);
        }


        /**
         * Actually download the Image from the _url
         *
         * @param _url
         * @return
         */
        private Drawable downloadImage(String _url) {
            //Prepare to download nivUser
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();

                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return new BitmapDrawable(bMap);

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }
    }

    protected void setImage(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }
}
