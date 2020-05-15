package com.mericompany.myproject;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DownloadingTask extends AsyncTask<String, Void, Void> {

    Context context;
    String directory ,fileName;
    ProgressBar progressBar;
    TextView downloadPercent;
    ImageView downloadIcon;

    View view;

    int percent = 0;

    DownloadingTask(Context context,View view){
        this.context = context;
        directory = DataActivity.getDirectory();
        fileName = DataActivity.getFileName();
        this.view = view;
        progressBar = view.findViewById(R.id.progressBarDownload);
        downloadPercent = view.findViewById(R.id.downloadPercent);
        downloadIcon = view.findViewById(R.id.downloadIcon);
    }

    File apkStorage = null;
    File outputFile = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        downloadPercent.setText("0 %");
        downloadIcon.setImageResource(R.drawable.pause);
    }

    @Override
    public void onPostExecute(Void result) {
        try {
            if (outputFile != null) {
                progressBar.setVisibility(View.INVISIBLE);
                downloadPercent.setVisibility(View.GONE);
                downloadIcon.setImageResource(R.drawable.complete);
                Toast.makeText(context, "DownloadSuccess", Toast.LENGTH_SHORT).show();

            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3000);
                view.setClickable(true);
                downloadIcon.setImageResource(R.drawable.download_failed);
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            e.printStackTrace();
            //Change button text if an exception occurs
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 3000);
            Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
        }

        super.onPostExecute(result);
    }

    @Override
    protected Void doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);//Create Download URl
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();//Open Url Connection
            urlConnection.setRequestMethod("GET");//Set Request Method to "GET" since we are getting data
            urlConnection.connect();//connect the URL Connection

            //If Connection response is not OK then show Logs
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + urlConnection.getResponseCode()
                        + " " + urlConnection.getResponseMessage());
            }

            //Get File if SD card is present
            if (new CheckForSDCard().isSDCardPresent()) {
                apkStorage = new File(
                        Environment.getExternalStorageDirectory(), directory);
            } else {
                Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();
            }

            //If File is not present create directory
            if (!apkStorage.exists()) {
                apkStorage.mkdirs();
                Log.e(TAG, "Directory Created.");
            }
            outputFile = new File(apkStorage,fileName);//Create Output file in Main File

            //Create New File if not present
            if (!outputFile.exists()) {
                outputFile.createNewFile();
                Log.e(TAG, "File Created");
            }

            FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

            InputStream in = urlConnection.getInputStream();//Get InputStream for connection

            byte[] buffer = new byte[1024];//Set buffer type
            int len1 = 0;//init length


            int downloadedSize = 0;
            int totalSize = urlConnection.getContentLength();

            while ((len1 = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);//Write new file
                downloadedSize += len1;
                //Log.e("Progress:", "downloadedSize:" + Math.abs(downloadedSize * 100 / totalSize));
                percent = Math.abs(downloadedSize * 100 / totalSize);
                progressBar.setProgress(percent);
                //downloadPercent.setText(percent+"%");
                Log.i("downloading ","Downloading ... "+ percent + "%");
            }

            //Close all connection after doing task
            fos.close();
            in.close();

        } catch (Exception e) {

            //Read exception if something went wrong
            e.printStackTrace();
            outputFile = null;
            Log.e(TAG, "Download Error Exception " + e.getMessage());
        }
        return null;
    }

    public class CheckForSDCard {


        //Check If SD Card is present or not method
        public boolean isSDCardPresent() {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return true;
            }
            return false;
        }
    }
}

