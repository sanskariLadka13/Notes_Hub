package com.mericompany.myproject;

import android.app.Activity;

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

    public MyListAdapter(Activity context, ArrayList<String> title,ArrayList<String> subTitle, Integer pdfIcon,Integer downloadIcon ) {
        super(context, R.layout.customized, title);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.title=title;
        this.subTitle=subTitle;
        this.pdfIcon = pdfIcon;
        this.downloadIcon = downloadIcon;
        isDownloadIcon = true;
    }

    public MyListAdapter(Activity context, ArrayList<String> title,ArrayList<String> subTitle, Integer pdfIcon) {
        super(context, R.layout.customized, title);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.title=title;
        this.subTitle=subTitle;
        this.pdfIcon = pdfIcon;
        isDownloadIcon = false;
    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.customized, null,true);

        TextView titleText = rowView.findViewById(R.id.title);
        ImageView imageView = rowView.findViewById(R.id.icon);
        TextView subtitleText = rowView.findViewById(R.id.subtitle);
        ImageView downloadIconView = rowView.findViewById(R.id.downloadIcon);

        if(isDownloadIcon == true) {
            downloadIconView.setVisibility(View.VISIBLE);
            downloadIconView.setImageResource(downloadIcon);
        }
        else {
            downloadIconView.setVisibility(View.INVISIBLE);
        }
        titleText.setText(title.get(position));
        imageView.setImageResource(pdfIcon);
        subtitleText.setText(subTitle.get(position));

        return rowView;

    };
}