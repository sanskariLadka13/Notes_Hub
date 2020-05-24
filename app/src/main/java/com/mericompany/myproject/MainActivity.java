package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    Button startButton;
    ImageView startImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startImage = findViewById((R.id.startImageView));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int density = (int) getResources().getDisplayMetrics().density;

        ViewGroup.LayoutParams params = startImage.getLayoutParams();
        params.height = heightPixels - (180*density);
        params.width = ((heightPixels - (180*density)))/2;

    }

    public  void login(){

        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
    }

    public void onClickStart(View view){
        login();
    }

    public void onClickHelp(View view){
        Intent helpIntent = new Intent(getApplicationContext(),Help.class);
        startActivity(helpIntent);
    }

    // some features needs to be added.....
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}