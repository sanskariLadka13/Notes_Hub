package com.mericompany.myproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
            case R.id.signout_data :
                mAuth.signOut();
                Intent loginActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginActivity);
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
        Intent chooseIntent = new Intent(getApplicationContext(),ChooseActivity.class);
        startActivity(chooseIntent);
    }
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(homeIntent);
    }

}
