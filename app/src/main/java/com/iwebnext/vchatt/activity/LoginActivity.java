package com.iwebnext.vchatt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
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
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.iwebnext.vchatt.request.SignUpSocialMediaUserRequest;
import com.iwebnext.vchatt.utils.Constants;
import com.iwebnext.vchatt.utils.EndPoints;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int TIME_OUT = 3000;

    private static final int RC_VCHAT_REGISTER = 1234;
    private static final int RC_GPLUS_SIGN_IN = 100;

    private ProgressDialog progressDialog;

    private SharedPreferences mPrefs;
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText inputPassword;
    private EditText inputEmail;
    private Button btnEnter;
    private TextView tvSignUp, tvForgetPassword;
    private DotProgressBar progressBar;

    //google api client
    private SignInButton btnSignInGPlus;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient mGoogleApiClient;
    GoogleSignInAccount googleSignInAccount;

    // Used for FB
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private AccessToken accessToken;
    String emailSocial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        FB Initialization
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        printHashKey();


        /**
         * Check for login session. It user is already logged in
         * redirect him to main activity
         * */
        if (BaseApplication.getInstance().getPrefManager().getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*
        Configuration - Facebook
         */
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if (oldProfile == null && newProfile != null)
                    loginWithFBProfile(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginButton btnFBLogin = (LoginButton) findViewById(R.id.login_fb_button);

        //Setting onclick listener to signing button
        if (btnFBLogin != null) {
            btnFBLogin.setReadPermissions(Arrays.asList(
                    "public_profile", "email", "user_birthday", "user_friends"));

            btnFBLogin.registerCallback(callbackManager, callback);
        }

        // FB configuration done

        /*
        Configuration - Google Sign In
         */
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        btnSignInGPlus = (SignInButton) findViewById(R.id.login_gplus_button);
        btnSignInGPlus.setSize(SignInButton.SIZE_WIDE);
        btnSignInGPlus.setScopes(googleSignInOptions.getScopeArray());

        btnSignInGPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gPlusSignIn();
            }


        });
        // Google SignIn configuration done

        // welcome view
        TextView tx = (TextView) findViewById(R.id.tv_welcome);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/painting_the_light.ttf");
        tx.setTypeface(customFont);

        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnEnter = (Button) findViewById(R.id.btn_enter);

        tvSignUp = (TextView) findViewById(R.id.tv_sign_up);
        tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        progressBar = (DotProgressBar) findViewById(R.id.login_progress_bar);
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

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
                LoginActivity.this.startActivityForResult(registerIntent, RC_VCHAT_REGISTER);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
                login();

            }
        });
    }

    /*
    GPlus integration
     */
    private void gPlusSignIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_GPLUS_SIGN_IN);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result == null)
            return;
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            googleSignInAccount = result.getSignInAccount();
            lookupUser(Constants.USER_TYPE_GPLUS, googleSignInAccount.getId());
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //GPlus integration END

    /*
    FB integration
     */
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

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            BaseApplication.getInstance().getPrefManager().storeFBAccessToken(accessToken.getToken());
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    private void displayMessage(Profile profile) {
        if (profile != null) {
            //extra
        }
    }

    private void loginWithFBProfile(final Profile profile) {
        if (profile != null) {


            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());
                            String email = object.optString("email");
                            emailSocial = email;
                            // String birthday = object.getString("birthday"); // 01/31/1980 format

                            Profile profile = Profile.getCurrentProfile();
                            lookupUser(Constants.USER_TYPE_FACEBOOK, profile.getId());
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    /**
     * This method is for look up any user
     *
     * @param type = 0 for Normal User, type = 1 for Facebook, type = 2 for GPlus
     * @param id
     */
    private void lookupUser(final String type, final String id) {
        showProgress();
        String endPoint = EndPoints.LOOKUP_SOCIAL_MEDIA_USER + "?type=" + type + "&social_media_id=" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, endPoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getBoolean("error")) {
                        dismissProgress();
                        // User exists
                        JSONObject userObj = response.getJSONObject("user");
                        String userId = userObj.getString("user_id");
                        String name = userObj.getString("name");
                        String email = userObj.getString("email");
                        String type = userObj.getString("type");
                        User user = new User(userId, name, email);

                        BaseApplication.getInstance().getPrefManager().storeUser(user);
                        BaseApplication.getInstance().getPrefManager().setUserType(type);

                        // Show home screen
                        showHomeScreen();
                    } else {
                        if (type.equals(Constants.USER_TYPE_FACEBOOK)) {
                            Profile profile = Profile.getCurrentProfile();
                            sendNewSocialMediaRegisterRequest(type, profile.getName(), emailSocial, profile.getId());
                        } else if (type.equals(Constants.USER_TYPE_GPLUS)) {
                            if (googleSignInAccount != null) {
                                sendNewSocialMediaRegisterRequest(type, googleSignInAccount.getDisplayName(), googleSignInAccount.getEmail(), googleSignInAccount.getId());
                            }
                        }
                    }
                } catch (JSONException e) {
                    dismissProgress();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Log.i(TAG, "lookup request error: " + error);
                if (error.equals("Volley Timeout Error"))
                    Toast.makeText(LoginActivity.this, "Server is not reachable", Toast.LENGTH_SHORT).show();
            }
        });
        BaseApplication.getInstance().addToRequestQueue(request);
    }

    private void sendNewSocialMediaRegisterRequest(String type, String name, String email, String id) {
        if (email == null) {
            email = " ";
        }
        SignUpSocialMediaUserRequest registerRequest = new SignUpSocialMediaUserRequest(name, email, type, id, fbRegisterListener);
        BaseApplication.getInstance().addToRequestQueue(registerRequest);
    }

    Response.Listener<String> fbRegisterListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            dismissProgress();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean error = jsonResponse.getBoolean("error");
                if (!error) {
                    String userId = jsonResponse.getString("user_id");
                    String name = Profile.getCurrentProfile().getName();
                    User user = new User(userId, name, emailSocial);
                    BaseApplication.getInstance().getPrefManager().storeUser(user);
                    BaseApplication.getInstance().getPrefManager().setUserType(Constants.USER_TYPE_FACEBOOK);
                    showHomeScreen();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
    // FB integration END

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
                        BaseApplication.getInstance().getPrefManager().setUserType(Constants.USER_TYPE_NORMAL);

                        // Show home screen
                        showHomeScreen();
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

        if (requestCode == RC_GPLUS_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleGoogleSignInResult(result);
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_VCHAT_REGISTER) {
                String email = data.getStringExtra(Constants.EXTRA_KEY_USER_EMAIL);
                inputEmail.setText(email);
            }
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

    private void showHomeScreen() {
        // start main activity
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
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
}
