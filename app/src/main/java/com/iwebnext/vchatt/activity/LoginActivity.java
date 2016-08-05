package com.iwebnext.vchatt.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.model.User;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int TIME_OUT = 3000;
    private SharedPreferences mPrefs;
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText inputPassword;
    private EditText inputEmail;
    private Button btnEnter;
    private TextView tvSignUp, tvForgetPassword;
    private DotProgressBar progressBar;
    private static final int REQUEST_CODE_REGISTER = 1234;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private GoogleSignInOptions gso;
    private SignInButton signInButton;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        printHashKey();

        FacebookSdk.sdkInitialize(LoginActivity.this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        /**
         * Check for login session. It user is already logged in
         * redirect him to main activity
         * */
        if (BaseApplication.getInstance().getPrefManager().getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.iwebnext.vchatt",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_fb_button);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnEnter = (Button) findViewById(R.id.btn_enter);

        tvSignUp = (TextView) findViewById(R.id.tv_sign_up);
        tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        progressBar = (DotProgressBar) findViewById(R.id.login_progress_bar);
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInButton = (SignInButton) findViewById(R.id.login_gplus_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //Setting onclick listener to signing button

        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, callback);


        // configure controls
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));


        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent forgetIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                LoginActivity.this.startActivity(forgetIntent);
            }
        });


        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                LoginActivity.this.startActivityForResult(registerIntent, REQUEST_CODE_REGISTER);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
                login();

            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == signInButton) {
                    //Calling signin
                    signIn();
                }
            }


        });
    }

    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result == null)
            return;
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email

            inputEmail.setText(acct.getEmail());

//            //Initializing image loader
//            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
//                    .getImageLoader();
//
//            imageLoader.get(acct.getPhotoUrl().toString(),
//                    ImageLoader.getImageListener(profilePhoto,
//                            R.mipmap.ic_launcher,
//                            R.mipmap.ic_launcher));
//
//            //Loading image
//            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }


//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//    }


    //gplus end

    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "info.vchatt.gcm.activity",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /**
     * logging in user. Will make http post request with password, email
     * as parameters
     */
    private void login() {
        if (!validatePassword()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();


        StringRequest strReq = new StringRequest(Request.Method.POST, EndPoints.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in

                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(userObj.getString("user_id"),
                                userObj.getString("name"),
                                userObj.getString("email"));

                        // storing user in shared preferences
                        BaseApplication.getInstance().getPrefManager().storeUser(user);

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Network error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(strReq);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    // Validating email
    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            //inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    // Validating password
    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputPassword.setError(getString(R.string.err_msg_name));
            requestFocus(inputPassword);
            return false;
        } else {
            //  inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_REGISTER) {
                String email = data.getStringExtra(Constants.EXTRA_KEY_USER_EMAIL);
                inputEmail.setText(email);
            }
        } else {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }


    private void displayMessage(Profile profile) {
        if (profile != null) {
            //extra
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {
            Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);

            startActivity(myIntent);


            String accessToken = loginResult.getAccessToken().getToken();
            Log.i("accessToken", accessToken);

            // AccessToken accessToken = loginResult.getAccessToken();
            // System.out.println("access token"+accessToken);


            Profile profile = Profile.getCurrentProfile();

            displayMessage(profile);
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {


                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login
                    //  Bundle bFacebookData = getFacebookData(object);
                    Profile profile = Profile.getCurrentProfile();

                    try {
                        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                        String Email = object.getString("email");
                        Bundle bundle = new Bundle();
                        bundle.putString("email", Email);
                        i.putExtras(bundle);


                        inputEmail.setText(object.getString("email"));
                        inputPassword.setText(object.getString("password"));

                        // String inputEmail = object.getString("email");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //saveNewUser();

                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email"); // Parámetros que pedimos a facebook
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
