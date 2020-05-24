package com.mericompany.myproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView read;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.home_help :
                Intent helpActivity = new Intent(getApplicationContext(), Help.class);
                startActivity(helpActivity);
                return true;
            default:
                return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        read = findViewById(R.id.textViewread);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/lemon_font.otf");
        read.setTypeface(typeface);

        requestAppPermissions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

    }

    public void onClickRead(View view){
        Intent fileIntent = new Intent(getApplicationContext(),FileView.class);
        startActivity(fileIntent);
    }

    public void onClickDownload(View view){
        Intent dataIntent = new Intent(getApplicationContext(),DataActivity.class);
        startActivity(dataIntent);
    }
    public void onClickContribute(View view){
        Intent chooseIntent = new Intent(getApplicationContext(),SignInCheck.class);
        startActivity(chooseIntent);
    }
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(homeIntent);
    }
    private void requestAppPermissions(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            return;
        }
        if(hasReadPermissions()&&hasWritePermissions()){
            return;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                ,2);
    }
    private boolean hasReadPermissions(){
        return (ContextCompat.checkSelfPermission(getBaseContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions(){
        return (ContextCompat.checkSelfPermission(getBaseContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED);
    }

}
