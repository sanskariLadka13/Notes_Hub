package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView emailView;
    TextView passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            login();
        }
        emailView = findViewById(R.id.emailView);
        passwordView = findViewById(R.id.passwordView);

    }

    public void onClickLogin(final View view){
        mAuth.signInWithEmailAndPassword(emailView.getText().toString(), passwordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            login();
                        } else {
                            signUp();
                        }
                    }
                });
    }

    public  void login(){
        // Move to next activity...
        Toast.makeText(this, "Login done", Toast.LENGTH_SHORT).show();

        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
    }

    public  void signUp(){
        try {
            // If sign in fails, display a message to the user.
            mAuth.createUserWithEmailAndPassword(emailView.getText().toString(), passwordView.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // updating firebase...
                                login();
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("Email").setValue(emailView.getText().toString());
                                Toast.makeText(MainActivity.this, "Signup done", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w("result", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error occurred while Signing...", Toast.LENGTH_SHORT).show();
        }
    }

    // some features needs to be added.....
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}