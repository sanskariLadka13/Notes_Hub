package com.mericompany.myproject;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MySpinnerAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> countryNames;
    LayoutInflater inflter;
    Typeface typeface;

    public MySpinnerAdapter(Context applicationContext, ArrayList<String> countryNames) {
        this.context = applicationContext;
        this.countryNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryNames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        //ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        //icon.setImageResource(flags[i]);
        typeface = Typeface.createFromAsset(names.getContext().getAssets(),"fonts/lemon_font.otf");
        names.setText(countryNames.get(i));
        names.setTypeface(typeface);
        return view;
    }
}