package com.mericompany.myproject;

import android.app.Activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> title;
    private ArrayList<String> subTitle;
    Integer pdfIcon;
    Integer downloadIcon;
    boolean isDownloadIcon;
    public int size;


    public ArrayList<View> viewGroup = new ArrayList<>();
    public ArrayList<Integer> downloadLinkState = new ArrayList<>();
    public static ArrayList<Integer> percent = new ArrayList<>();

    TextView titleText;
    ImageView pdfIconView;
    TextView subtitleText;
    ImageView downloadIconView;
    ProgressBar progressBar;
    TextView percentText;

    public final int DOWNLOADING = 3,
                DOWNLOAD_ERROR = 1,
                DOWNLOAD_COMPLETE = 2,
                DOWNLOAD_START = 0;


    public MyListAdapter(Activity context, ArrayList<String> title,ArrayList<String> subTitle, Integer pdfIcon,Integer downloadIcon ) {
        super(context, R.layout.customized, title);

        this.context=context;
        this.title=title;
        this.subTitle=subTitle;
        this.pdfIcon = pdfIcon;
        this.downloadIcon = downloadIcon;
        isDownloadIcon = true;
        size = title.size();
        updateLength(title.size());

    }

    public MyListAdapter(Activity context, ArrayList<String> title,ArrayList<String> subTitle, Integer pdfIcon) {
        super(context, R.layout.customized, title);

        this.context=context;
        this.title=title;
        this.subTitle=subTitle;
        this.pdfIcon = pdfIcon;
        isDownloadIcon = false;
        size = title.size();
        updateLength(title.size());
    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        //This thing seems to be very ordinary but
        if(viewGroup.get(position)==null) {
            Log.i("position",position+"");
            viewGroup.set(position, inflater.inflate(R.layout.customized, null, true));
        }

        titleText           =   viewGroup.get(position).findViewById(R.id.title);
        pdfIconView          = viewGroup.get(position).findViewById(R.id.icon);
        subtitleText       = viewGroup.get(position).findViewById(R.id.subtitle);
        downloadIconView = viewGroup.get(position).findViewById(R.id.downloadIcon);
        progressBar     = viewGroup.get(position).findViewById(R.id.progressBarDownload);
        percentText        = viewGroup.get(position).findViewById(R.id.downloadPercent);


        if (isDownloadIcon == true) {
            downloadIconView.setVisibility(View.VISIBLE);
            switch (downloadLinkState.get(position)){
                case DOWNLOAD_START:
                    downloadIconView.setImageResource(R.drawable.download_icon);
                    break;
                case DOWNLOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(percent.get(position));
                    percentText.setText(percent.get(position)+" %");
                    downloadIconView.setImageResource(R.drawable.pause);
                    break;
                case DOWNLOAD_ERROR:
                    downloadIconView.setImageResource(R.drawable.reload);
                    break;
                case DOWNLOAD_COMPLETE:
                    downloadIconView.setImageResource(R.drawable.complete);
                    break;
            }
        }

        titleText.setText(title.get(position));
        pdfIconView.setImageResource(pdfIcon);
        subtitleText.setText(subTitle.get(position));

    //return rowView;
        return viewGroup.get(position);
    };

    public void setDownloadState(int i,int state){
            downloadLinkState.set(i,state);
        }

    public void updateFileName_Size(ArrayList<String> fileName,ArrayList<String> fileSize){
        title.clear();
        title.addAll(fileName);
        subTitle.clear();
        subTitle.addAll(fileSize);
        updateLength(fileName.size());
        this.notifyDataSetChanged();
    }

    public void updateLength(int size){
            this.size = size;
            viewGroup.clear();
        for (int i = 0; i < size; ++i) {
            viewGroup.add(null);
        }
            if(isDownloadIcon) {
                percent.clear();
                for (int i = 0; i < size; ++i) {
                    downloadLinkState.add(DOWNLOAD_START);
                    percent.add(0);
                }
                Log.i("sixe",viewGroup.size()+"");
            }
    }

}