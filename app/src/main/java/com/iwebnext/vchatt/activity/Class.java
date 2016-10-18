//package com.iwebnext.vchatt.activity;
//
///**
// * Created by PRIYANKA on 10/15/2016.
// */
//
//import android.widget.Toast;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Base64;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.OvershootInterpolator;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.abmessenger.R;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Calendar;
//
//public class TabFragmentDiary extends Fragment implements View.OnClickListener {
//
//    private Button ChooseBtn;
//    private Button _btn_upload_photo;
//
//    private int PICK_IMAGE_REQUEST = 1;
//    private int viewHeight = 0;
//    private Bitmap _bitmap;
//
//    private ImageView _imgDairy;
//
//    private BufferedReader bufferedReader;
//    private ProgressDialog _progress;
//
//    private EditText etTitle;
//    private EditText etEntry;
//
//    public static final String UPLOAD_URL = "http://inextwebs.com/arbic/upload_image.php";
//    public static final String SHOWDIARYURL = "http://inextwebs.com/arbic/show_diary.php";
//    public static final String homeUrl = "http://inextwebs.com/arbic/";
//    private SharedPreferences preferences;
//
//    private String userName;
//    private String time;
//    private ArrayList<String> _allImageUrl = new ArrayList<String>();
//    private ArrayList<String> _title = new ArrayList<String>();
//    private ArrayList<String> _content = new ArrayList<String>();
//    private ArrayList<String> _idList = new ArrayList<String>();
//
//    public static ArrayList<Bitmap> _bitmapList;
//    public JSONArray json;
//    public int index;
//
//    private RecyclerView recyclerView;
//    DiaryAdapter diaryAdapter;
//    View view;
//
//
//    public TabFragmentDiary() {
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.tab_diary, container, false);
//
//        viewHeight = view.getMeasuredHeight();
//
//        ChooseBtn = (Button) view.findViewById(R.id.btn_add_photo);
//        _btn_upload_photo = (Button) view.findViewById(R.id.btn_upload_photo);
//        etTitle = (EditText) view.findViewById(R.id.et_title);
//        etEntry = (EditText) view.findViewById(R.id.et_sub_title);
//        _imgDairy = (ImageView) view.findViewById(R.id.dairy_img);
//
//        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        userName = preferences.getString("username", "DEFAULT");
//
//        Showdiary showdiary = new Showdiary();
//        showdiary.execute(userName);
//
//        recyclerView = (RecyclerView) view.findViewById(R.id.userdairy);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
//
//
//        ChooseBtn.setOnClickListener(this);
//        _btn_upload_photo.setOnClickListener(this);
//
//        Calendar c = Calendar.getInstance();
//        int seconds = c.get(Calendar.SECOND);
//        int minute = c.get(Calendar.MINUTE);
//        int hr = c.get(Calendar.HOUR);
//        int am = c.get(Calendar.AM_PM);
//        int zero = 0;
//
//        if (Integer.valueOf(am) == Integer.valueOf(zero)) {
//            time = hr + ":" + minute + ":" + seconds + " AM";
//        } else {
//            time = hr + ":" + minute + ":" + seconds + " PM";
//        }
//
//        //get user Name from SharedPreference.
//
//
//        return view;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_add_photo:
//                showFileChooser();
//                return;
//            case R.id.btn_upload_photo:
//                String title = etTitle.getText().toString();
//                String entry = etEntry.getText().toString();
//                if (_bitmap != null && !title.isEmpty() && !entry.isEmpty()) {
//                    uploadImage(_bitmap, title, entry);
//                } else {
//                    if (_bitmap == null) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Choose Image", Toast.LENGTH_SHORT).show();
//                    } else if (title.isEmpty()) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Title Missing", Toast.LENGTH_SHORT).show();
//
//                    } else if (entry.isEmpty()) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Entry Missing", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                return;
//        }
//
//
//    }
//
//    private void showFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST) {
//            if (resultCode == Activity.RESULT_OK) {
//                // Get the Image from data
//                Uri filePath = data.getData();
//                try {
//                    _bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
//                    _imgDairy.setVisibility(View.VISIBLE);
//                    _imgDairy.getLayoutParams().height = 200;
//                    _imgDairy.setImageBitmap(_bitmap);
//
//                } catch (Exception e) {
//                    e.getMessage();
//
//                }
//            }
//        }
//    }
//
//    private void uploadImage(Bitmap bmp, String title, String entry) {
//        _progress = ProgressDialog.show(getActivity(), "Uploading...", "Please Wait", true);
//        _progress.show();
//        new UploadImage(bmp, title, entry).execute();
//
//
//    }
//
//    //async task to upload image
//    private class UploadImage extends AsyncTask<Void, Void, String> {
//        private Bitmap bitmapbyteArray;
//        private String title;
//        private String entry;
//        private int k;
//
//
//        public UploadImage(Bitmap bitImage, String title, String entry) {
//            this.bitmapbyteArray = bitImage;
//            this.title = title;
//            this.entry = entry;
//
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            //Convert image to String
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmapbyteArray.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//            String encodeImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//            _bitmapList = new ArrayList<Bitmap>();
//
//            try {
//                URL url = new URL(UPLOAD_URL);
//
//                String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(encodeImage, "UTF-8");
//                data = data + "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
//                data = data + "&" + URLEncoder.encode("entry", "UTF-8") + "=" + URLEncoder.encode(entry, "UTF-8");
//                data = data + "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8");
//                data = data + "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
//
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setDoOutput(true);
//                conn.setInstanceFollowRedirects(false);
//                conn.setRequestMethod("POST");
//                conn.connect();
//
//                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//                wr.write(data);
//                wr.flush();
//
//                System.out.println("url value resposncode:" + conn.getResponseCode());
//
//                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//                String result = bufferedReader.readLine();
//
//                encodeImage = null;
//                byteArray = null;
//
//                return result;
//
//            } catch (Exception e) {
//                System.out.println("url value:" + e.getLocalizedMessage());
//                return e.getMessage();
//
//            }
//
//        }
//
//
//        @Override
//        protected void onPostExecute(String response) {
//
//            try {
//                JSONObject jsonResponse = new JSONObject(response);
//                boolean success = jsonResponse.getBoolean("success");
//
//                if (success) {
//                    _progress.dismiss();
//                    new Showdiary().execute(userName);
//                    etTitle.setText("");
//                    etEntry.setText("");
//                    _imgDairy.setImageBitmap(null);
//                    _imgDairy.setVisibility(View.GONE);
//
//                    Toast.makeText(getActivity().getApplicationContext(), "Diary Added Successfully!!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), "Try Again!!", Toast.LENGTH_SHORT).show();
//
//                }
//
//
//            } catch (JSONException e) {
//                System.out.println("test123" + e.getMessage());
//            }
//
//        }
//    }
//
//    //async task to Show Diary details
//    private class Showdiary extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String user = params[0];
//            String urlSuffix = "?user=" + user;
//            BufferedReader bufferedReader;
//
//            try {
//                URL url = new URL(SHOWDIARYURL + urlSuffix);
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                String result = bufferedReader.readLine();
//                return result;
//            } catch (IOException e) {
//                return "" + e.getLocalizedMessage();
//            }
//            //return null;
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//
//            if (_allImageUrl != null) {
//                _allImageUrl.clear();
//                _title.clear();
//                _content.clear();
//            }
//
//            super.onPostExecute(response);
//            String jsonResponse = "[" + response + "]";
//
//
//            try {
//                json = new JSONArray(jsonResponse);
//
//                for (index = 0; index < json.length(); index++) {
//                    JSONObject jsonObject = json.getJSONObject(index);
//                    String id = jsonObject.getString("id");
//                    String date = jsonObject.getString("date");
//                    String time = jsonObject.getString("time");
//                    String title = jsonObject.getString("title");
//                    String content = jsonObject.getString("content");
//                    String imageURL = jsonObject.getString("imgurl");
//
//                    System.out.println("title value" + "  " + title);
//
//                    _idList.add(id);
//                    _allImageUrl.add(homeUrl + imageURL);
//                    _title.add(title);
//                    _content.add(content);
//
//                }
//
//                System.out.println("arraylist size" + _allImageUrl.size());
//
//                if (_allImageUrl.size() > 0) {
//                    diaryAdapter = new DiaryAdapter(_allImageUrl, _title, _content, _idList);
//                    diaryAdapter.notifyDataSetChanged();
//                    recyclerView.setAdapter(diaryAdapter);
//                } else {
//                    Toast.makeText(getActivity().getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//                }
//
//            } catch (JSONException e) {
//                System.out.println("exception json" + e.getLocalizedMessage());
//            }
//        }
//    }
//
//}
