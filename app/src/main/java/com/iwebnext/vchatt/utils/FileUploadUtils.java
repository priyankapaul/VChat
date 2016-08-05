package com.iwebnext.vchatt.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by priyanka on 8/5/16.
 */
public class FileUploadUtils {
    private static final String TAG = FileUploadUtils.class.getSimpleName();

    private static String LINE_END = "\r\n";
    private static String TWO_HYPHENS = "--";
    private static String BOUNDARY = "*****";
    private static int MAX_BUFFER_SIZE = 1 * 1024 * 1024;

    public static final int PERMISSIONS_REQUEST_READ_MEDIA = 3;

    public static String uploadImage(String fileName, String userId, String uploadUrl) {
        int serverResponseCode = -1;

        HttpURLConnection conn = null;
        DataOutputStream dataOutputStream = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;

        File sourceFile = new File(fileName);
        if (!sourceFile.isFile()) {
            Log.i(TAG, "Source File Does not exist");
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(uploadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            dataOutputStream = new DataOutputStream(conn.getOutputStream());


            /**
             * 1st - File Part
             */
            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);

            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + LINE_END);
            dataOutputStream.writeBytes(LINE_END);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(LINE_END);


            /**
             * 2nd - String Part
             */
            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"user_id\"");
            dataOutputStream.writeBytes(LINE_END);

            dataOutputStream.writeBytes(LINE_END);
            dataOutputStream.writeBytes(userId);
            dataOutputStream.writeBytes(LINE_END);

            /**
             * SIGNALS END OF REQUEST PARTS
             */
            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);

            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (MalformedURLException ex) {
            return null;
        } catch (Exception e) {
            return null;
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            Log.i(TAG, sb.toString());
            return sb.toString();
        } else {
            return null;
        }
    }

    /**
     * This function upload the large file to server with other POST values.
     *
     * @return
     */
    public static String uploadVideo(String fileName, String userId, String peerId, String uploadUrl) {
        int serverResponseCode = -1;

        HttpURLConnection conn = null;
        DataOutputStream dataOutputStream = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;

        File sourceFile = new File(fileName);
        if (!sourceFile.isFile()) {
            Log.i(TAG, "Source File Does not exist");
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(uploadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            dataOutputStream = new DataOutputStream(conn.getOutputStream());


            /**
             * 1st - File Part
             */
            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);

            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + LINE_END);
            dataOutputStream.writeBytes(LINE_END);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(LINE_END);


            /**
             * 2nd - String Part
             */
            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"user_id\"");
            dataOutputStream.writeBytes(LINE_END);

            dataOutputStream.writeBytes(LINE_END);
            dataOutputStream.writeBytes(userId);
            dataOutputStream.writeBytes(LINE_END);

            /**
             * 3rd - String Part
             */
            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"peer_id\"");
            dataOutputStream.writeBytes(LINE_END);

            dataOutputStream.writeBytes(LINE_END);
            dataOutputStream.writeBytes(peerId);
            dataOutputStream.writeBytes(LINE_END);

            /**
             * SIGNALS END OF REQUEST PARTS
             */
            dataOutputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);

            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            Log.i(TAG, sb.toString());
            return sb.toString();
        } else {
            return "Could not upload";
        }
    }
}
