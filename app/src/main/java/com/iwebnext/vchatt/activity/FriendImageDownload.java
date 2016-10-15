package com.iwebnext.vchatt.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.toolbox.NetworkImageView;
import com.iwebnext.vchatt.R;
import com.iwebnext.vchatt.app.BaseApplication;
import com.iwebnext.vchatt.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by PRIYANKA on 9/19/2016.
 */
public class FriendImageDownload extends AppCompatActivity {

    private NetworkImageView imgShow;
    private Toolbar toolbar;
    private String peerImage;
    private Button btnDownload;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_image_download);

        final Intent intent = getIntent();

        peerImage = intent.getStringExtra(Constants.EXTRA_KEY_FRIEND_IMAGE);

        imgShow = (NetworkImageView) findViewById(R.id.img_show);
        btnDownload = (Button) findViewById(R.id.btn_download);

        imgShow.setImageUrl(peerImage, BaseApplication.getInstance().getImageLoader());
        Window window = FriendImageDownload.this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(FriendImageDownload.this.getResources().getColor(R.color.black));
             }


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAsyncTask myWebFetch = new myAsyncTask();
                myWebFetch.execute();
            }
        });
    }


    class myAsyncTask extends AsyncTask<Void, Void, Void>    {
        TextView tv;
        public ProgressDialog dialog;
        myAsyncTask()
        {
            dialog = new ProgressDialog(FriendImageDownload.this);
            dialog.setMessage("Downloading...");
            dialog.setCancelable(true);
            dialog.setIndeterminate(true);
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
        protected Void doInBackground(Void... arg0) {
            try {
                //set the download URL, a url that points to a file on the internet
                //this is the file to be downloaded
                URL url = new URL(peerImage);

                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //and connect!
                urlConnection.connect();

                //set the path where we want to save the file
                //in this case, going to save it on the root directory of the
                //sd card.
                File SDCardRoot = Environment.getExternalStorageDirectory();
                //create a new file, specifying the path, and the filename
                //which we want to save the file as.
                
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String formattedDate = df.format(c.getTime());

                File file = new File(SDCardRoot, formattedDate + ".jpg");

                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);

                //this will be used in reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                //variable to store total downloaded bytes
                int downloadedSize = 0;

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);

                }
                //close the output stream when done
                fileOutput.close();

                //catch some possible errors...
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}