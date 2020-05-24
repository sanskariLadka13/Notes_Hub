package com.mericompany.myproject;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.mericompany.myproject.MyListAdapter.percent;

public class DownloadingTask extends AsyncTask<String, Void, Void> {

    Context context;
    String directory ,fileName;
    ProgressBar progressBar;
    TextView downloadPercent;
    ImageView downloadIcon;
    HttpURLConnection urlConnection;

    View view;

    int pos;
    MyListAdapter adapter;
    Activity activity;

    public NotificationManager mNotifyManager;
    public NotificationCompat.Builder mBuilder;
    int id ;
    DownloadingTask(Context context,View view,int pos,MyListAdapter adapter){
        this.activity = (Activity)context;
        this.context = context;
        directory = DataActivity.getDirectory();
        fileName = DataActivity.getFileName();
        this.view = view;
        this.pos = pos;
        progressBar = view.findViewById(R.id.progressBarDownload);
        downloadPercent = view.findViewById(R.id.downloadPercent);
        downloadIcon = view.findViewById(R.id.downloadIcon);
        this.adapter = adapter;

        Random rand = new Random();
        id = rand.nextInt(1000);
    }

    File apkStorage = null;
    File outputFile = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        downloadPercent.setText("0 %");
        downloadPercent.setVisibility(View.VISIBLE);
        downloadIcon.setImageResource(R.drawable.pause);
        adapter.setDownloadState(pos,adapter.DOWNLOADING);

        createNotification("Downloading Started");
    }

    @Override
    public void onPostExecute(Void result) {
        try {
            if (outputFile != null) {
                progressBar.setVisibility(View.INVISIBLE);
                downloadPercent.setVisibility(View.GONE);
                downloadIcon.setImageResource(R.drawable.complete);
                adapter.setDownloadState(pos,adapter.DOWNLOAD_COMPLETE);
                Toast.makeText(context, "DownloadSuccess", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();

                mNotifyManager.cancel(id);

                createNotification("Download Completed");



            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3000);
                downloadIcon.setImageResource(R.drawable.reload);
                progressBar.setVisibility(View.INVISIBLE);
                downloadPercent.setVisibility(View.GONE);
                adapter.setDownloadState(pos,adapter.DOWNLOAD_ERROR);
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();

                mNotifyManager.cancel(id);
                createNotification("Download Failed");

            }
        } catch (Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            downloadPercent.setVisibility(View.GONE);
            adapter.setDownloadState(pos,adapter.DOWNLOAD_ERROR);

            mNotifyManager.cancel(id);
            createNotification("Download Failed");
            e.printStackTrace();
            //Change button text if an exception occurs
            Toast.makeText(context, "Download Failed...", Toast.LENGTH_SHORT).show();
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
            urlConnection = (HttpURLConnection) url.openConnection();//Open Url Connection
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
             outputFile = new File(apkStorage, fileName);//Create Output file in Main File

            //Create New File if not present
            if (!outputFile.exists()) {
                outputFile.createNewFile();
                Log.e(TAG, "File Created");
            }
            ///////////////////////////////////////dont know why.....

            FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

            InputStream in = urlConnection.getInputStream();//Get InputStream for connection

            byte[] buffer = new byte[1024];//Set buffer type
            int len1 = 0;//init length

            int downloadedSize = 0;
            int totalSize = urlConnection.getContentLength();
            int tempPercent = 0;

            while ((len1 = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);//Write new file
                downloadedSize += len1;
                percent.set(pos,Math.abs(downloadedSize * 100 / totalSize));
                progressBar.setProgress(percent.get(pos));
                if(percent.get(pos)- tempPercent >= 1) {
                    setText(downloadPercent);
                }
                tempPercent = percent.get(pos);
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

    public void setText(final TextView textView){
        activity.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                textView.setText(percent.get(pos) + "%");

                //notification
                mBuilder.setProgress(100, percent.get(pos), false)
                    .setContentText(+percent.get(pos) + " %");
                mNotifyManager.notify(id, mBuilder.build());
            }
        });
    }
    public HttpURLConnection getUrlConnection(){
        adapter.setDownloadState(pos,adapter.DOWNLOAD_START);
        return urlConnection;
    }

    public void createNotification(String msg){

        //Notification stuff
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context,"notify_001");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mBuilder.setContentTitle(fileName)
                .setContentText(msg)
                .setSmallIcon(R.drawable.download_icon)
                .setOnlyAlertOnce(true);


        mNotifyManager.notify(id,mBuilder.build());
    }
}
