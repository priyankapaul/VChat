//package com.abmessenger.ui.TabFragments;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Base64;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.MediaController;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.VideoView;
//
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//
//import info.vchatt.gcm.R;
//import info.vchatt.gcm.model.Post;
//
//
//
//public class TabFragmentUserUpdate extends Fragment implements View.OnClickListener{
//    RecyclerView rvTimeline;
//    SharedPreferences preferences ;
//    String userName;
//    public static final String UPLOADUSERPOST_URL="http://inextwebs.com/arbic/upload_userpost.php";
//    public static final String UPLOAD_URL="http://inextwebs.com/arbic/upload_video.php";
//    private String selectedPath=null;
//    EditText postTitle;
//    TextView addphto;
//    TextView uservideoselect;
//    private int PICK_IMAGE_REQUEST = 1;
//    private int PICK_VIDEO_REQUEST = 2;
//    public int serverResponseCode;
//    Bitmap _bitmap=null;
//    ImageView _imageView;
//    ImageButton _imgButton;
//    MediaController mc;
//
//    VideoView videoView;
//
//    int WRITE_EXTERNAL_PERMISSIONS_REQUEST;
//
//    Button putBtn;
//
//    BufferedReader bufferedReader;
//
//    private ProgressDialog _progress;
//    private ProgressDialog _progressVideo;
//
//
//
//    public TabFragmentUserUpdate() {
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        View rootView = inflater.inflate(R.layout.tab_user_update, container, false);
//
//        postTitle=(EditText) rootView.findViewById(R.id.userwhatsup);
//        addphto=(TextView) rootView.findViewById(R.id.userupdatephoto);
//        _imageView=(ImageView)rootView.findViewById(R.id.userselectphoto);
//        uservideoselect=(TextView)rootView.findViewById(R.id.uservideoselect);
//        _imgButton=(ImageButton)rootView.findViewById(R.id.undoselectedimg);
//        putBtn=(Button)rootView.findViewById(R.id.userupdateput);
//        videoView=(VideoView)rootView.findViewById(R.id.userselectvideo);
//
//        mc = new MediaController(getActivity().getApplicationContext());
//
//        int version = Build.VERSION.SDK_INT;
//
//        System.out.println("sdk versioncheck"+version);
//
//        postTitle.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start,int before, int count)
//            {
//                if(s.length()> 0)
//                    putBtn.setVisibility(View.VISIBLE);
//                else
//                {putBtn.setVisibility(View.GONE);}
//            }
//
//        });
//
//        rvTimeline = (RecyclerView) rootView.findViewById(R.id.rv_user_timeline);
//
//        rvTimeline.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//
//        userName = preferences.getString("username", "DEFAULT");
//
//        System.out.println("test3" + userName);
//
//
//        addphto.setOnClickListener(this);
//        uservideoselect.setOnClickListener(this);
//        _imgButton.setOnClickListener(this);
//        putBtn.setOnClickListener(this);
//
//        BaseApplication.getInstance().addToRequestQueue(new TimelineRequest(userName, listener, errorListener));
//        return rootView;
//    }
//
//    Response.Listener<ArrayList<Post>> listener = new Response.Listener<ArrayList<Post>>()
//    {
//        @Override
//        public void onResponse(ArrayList<Post> posts)
//        {
//            rvTimeline.setAdapter(new TimelineAdapter(posts));
//        }
//    };
//
//    Response.ErrorListener errorListener = new Response.ErrorListener()
//    {
//        @Override
//        public void onErrorResponse(VolleyError error)
//        {
//
//        }
//    };
//
//
//    @Override
//    public void onClick(View v)
//    {
//        switch (v.getId())
//        {
//            case R.id.userupdatephoto:
//                showImageChooser();
//                break;
//            case R.id.uservideoselect:
//                showvideoChooser();
//                break;
//            case R.id.undoselectedimg:
//                _imageView.setImageBitmap(null);
//                _imageView.getLayoutParams().height = 0;
//                _imgButton.setVisibility(View.GONE);
//                videoView.setVisibility(View.GONE);
//
//                break;
//            case R.id.userupdateput:
//                _progress = ProgressDialog.show(getActivity(), "Uploading...","Please Wait", true);
//                _progress.show();
//                uploaduserpost(userName);
//        }
//    }
//    //Choose Image from Device
//    private void showImageChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    //show video from device
//    private void showvideoChooser()
//    {
//        Intent intent = new Intent();
//        intent.setType("video/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST)
//        {
//            if (resultCode == Activity.RESULT_OK)
//            {
//                // Get the Image path from data
//                Uri filePath = data.getData();
//                try{
//                    _bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
//
//                    _imageView.setVisibility(View.VISIBLE);
//                    _imageView.getLayoutParams().height = 200;
//                    _imageView.setImageBitmap(_bitmap);
//                    _imgButton.setVisibility(View.VISIBLE);
//                }
//                catch (Exception e)
//                {
//                    e.getMessage();
//
//                }
//            }
//        }
//        if (requestCode==PICK_VIDEO_REQUEST)
//        {
//            if (resultCode == Activity.RESULT_OK)
//            {
//                //get Run time Permission
//                getPermissionToRead();
//
//
//                Uri selectedVideoUri = data.getData();
//
//                if (videoView.isPlaying())
//                {
//                    videoView.stopPlayback();
//                    videoView.setVideoPath(null);
//                }
//
//
//                videoView.setVisibility(View.VISIBLE);
//
//                selectedPath = getPath(selectedVideoUri);
//
//                videoView.setVideoPath(selectedPath);
//
//                videoView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return true;
//                    }
//                });
//
//                System.out.println("selectedpath" + selectedPath);
//
//                videoView.start();
//
//
//                _imgButton.setVisibility(View.VISIBLE);
//
//
//
//            }
//        }
//    }
//    public void uploaduserpost(String user)
//    {
//        if (_bitmap!=null || !postTitle.getText().toString().isEmpty() || selectedPath!=null  )
//        {
//            if (_bitmap!=null)
//            {
//                String postTitleStr=postTitle.getText().toString();
//                String imageStr= bitmapToBase64(_bitmap);
//                new UploadUserPost().execute(imageStr, postTitleStr, user);
//
//                System.out.println("imagestr:-"+imageStr);
//            }
//
//            if (selectedPath!=null)
//            {
//
//
//                new Uploadvideo().execute(selectedPath);
//            }
//
//        }
//
//
//    }
//
//    private String bitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream .toByteArray();
//        return Base64.encodeToString(byteArray, Base64.DEFAULT);
//    }
//
//    public class UploadUserPost extends AsyncTask<String,Void,String>{
//        @Override
//        protected String doInBackground(String... params) {
//            try{
//                String bitmapstr=params[0];
//                String postTitleStr=params[1];
//                String Username=params[2];
//
//                URL url = new URL(UPLOADUSERPOST_URL);
//
//                String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(bitmapstr, "UTF-8");
//                data = data + "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(postTitleStr, "UTF-8");
//                data = data + "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(Username, "UTF-8");
//
//                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
//                conn.setDoOutput(true);
//                conn.setInstanceFollowRedirects(false);
//                conn.setRequestMethod("POST");
//                conn.connect();
//
//                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//                wr.write(data);
//                wr.flush();
//
//                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//                String result=bufferedReader.readLine();
//
//                return result;
//            }
//            catch (Exception e)
//            {
//                return e.getLocalizedMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            _imageView.setImageBitmap(null);
//            _imageView.setVisibility(View.GONE);
//            _imgButton.setVisibility(View.GONE);
//            postTitle.setText(null);
//            _progress.dismiss();
//
//            Toast.makeText(getActivity().getApplicationContext(), "Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public String getPath(Uri uri) {
//        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getActivity().getApplicationContext().getContentResolver().query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
//        cursor.close();
//
//        return path;
//    }
//    public void getPermissionToRead() {
//        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
//        // checking the build version since Context.checkSelfPermission(...) is only available
//        // in Marshmallow
//        // 2) Always check for permission (even if permission has already been granted)
//        // since the user can revoke permissions at any time through Settings
//        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // The permission is NOT already granted.
//            // Check if the user has been asked about this permission already and denied
//            // it. If so, we want to give more explanation about why the permission is needed.
//            if (shouldShowRequestPermissionRationale(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // Show our own UI to explain to the user why we need to read the contacts
//                // before actually requesting the permission and showing the default UI
//            }
//
//            // Fire off an async request to actually get the permission
//            // This will show the standard permission request dialog UI
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSIONS_REQUEST);
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
//        // Make sure it's our original READ_CONTACTS request
//        if (requestCode == WRITE_EXTERNAL_PERMISSIONS_REQUEST) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                System.out.println("hello there user granted permisssion");
//                //  Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
//            }
//           /* else {
//                // showRationale = false if user clicks Never Ask Again, otherwise true
//                boolean showRationale = shouldShowRequestPermissionRationale( getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//                if (showRationale)
//                {
//                    // do something here to handle degraded mode
//
//                        Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
//
//                }
//            } */
//        }
//        else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//
//    }
//
//
//    //Class upload video
//    private class Uploadvideo extends AsyncTask<String,Void,String>
//    {
//
//        @Override
//        protected String doInBackground(String... params)
//        {
//            String msg=   uploadVideo(params[0]);
//            return msg;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            System.out.println("test code"+s);
////            videoView.setVideoPath(null);
//            _progress.dismiss();
//            super.onPostExecute(s);
//        }
//    }
//
//    public String uploadVideo(String file) {
//
//        String fileName = file;
//        HttpURLConnection conn = null;
//        DataOutputStream dos = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024;
//
//        File sourceFile = new File(file);
//        if (!sourceFile.isFile()) {
//            Log.e("Huzza", "Source File Does not exist");
//            return null;
//        }
//
//        try {
//            FileInputStream fileInputStream = new FileInputStream(sourceFile);
//            URL url = new URL(UPLOAD_URL);
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            conn.setRequestProperty("myFile", fileName);
//            dos = new DataOutputStream(conn.getOutputStream());
//
//            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
//            dos.writeBytes(lineEnd);
//
//            bytesAvailable = fileInputStream.available();
//            Log.i("Huzza", "Initial .available : " + bytesAvailable);
//
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            buffer = new byte[bufferSize];
//
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0) {
//                dos.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            }
//
//            dos.writeBytes(lineEnd);
//            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//            serverResponseCode = conn.getResponseCode();
//
//            System.out.println("responsecode"+serverResponseCode);
//
//            fileInputStream.close();
//            dos.flush();
//            dos.close();
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (serverResponseCode == 200) {
//            StringBuilder sb = new StringBuilder();
//            try {
//                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String line;
//                while ((line = rd.readLine()) != null) {
//                    sb.append(line);
//                }
//                rd.close();
//            } catch (IOException ioex) {
//            }
//            return sb.toString();
//        }else {
//            return "Could not upload";
//        }
//    }
//
//
//}
