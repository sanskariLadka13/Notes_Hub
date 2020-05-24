package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class SignInCheck extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    String name, email;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    ImageButton refreshButton;
    TextView refreshText;

    private TextView noWifiText,TnC;
    private ImageView noWifi;

    HashMap fileDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        noWifi = findViewById(R.id.noWifi);
        noWifiText = findViewById(R.id.noWifiText);
        TnC = findViewById(R.id.TnC);
        signInButton = findViewById(R.id.sign_in_button);
        refreshButton = findViewById(R.id.refreshButton);
        refreshText = findViewById(R.id.refreshText);

        fileDetail = new HashMap();

        setWifiVisibility(false);
        signInButton.setEnabled(true);
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not

        user = FirebaseAuth.getInstance().getCurrentUser();
        //if user is signed in, we call a helper method to save the user details to Firebase

        if (isConnected()) {
            if (user != null) {
                setWifiVisibility(false);

                name = user.getDisplayName();
                email = user.getEmail();
                gotoChooseActivity();
            }
            else{
                firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
                authStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        // Get signedIn user
                        FirebaseUser user1 = firebaseAuth.getCurrentUser();
                        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        //if user is signed in, we call a helper method to save the user details to Firebase
                        if (user1 != null) {
                            // User is signed in
                            // you could place other firebase code
                            //logic to save the user details to Firebase
                        } else {
                            // User is signed out
                        }
                    }
                };

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                        .requestEmail()
                        .build();
                googleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                signInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected()) {
                            signInButton.setEnabled(false);
                            setWifiVisibility(false);
                            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                            startActivityForResult(intent, RC_SIGN_IN);
                        }
                        else {
                            setWifiVisibility(true);
                        }
                    }
                });
            }

        }
        else {
            setWifiVisibility(true);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();

            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        }else{
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. "+result);
            Toast.makeText(this, "Login Unsuccessful Try Again...", Toast.LENGTH_SHORT).show();
        }
    }
    private void firebaseAuthWithGoogle(final AuthCredential credential){

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
                            fileDetail.put("Name",name);
                            fileDetail.put("Email",email);
                            FirebaseDatabase.getInstance().getReference().child("users Record").push().setValue(fileDetail);

                            Toast.makeText(SignInCheck.this, "Login successful", Toast.LENGTH_SHORT).show();
                            gotoChooseActivity();
                        }else{
                            signInButton.setEnabled(true);
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(SignInCheck.this, "Authentication failed Try again...",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void gotoChooseActivity(){
        Intent intent = new Intent(SignInCheck.this, ChooseActivity.class);
        intent.putExtra("Sender",name);
        intent.putExtra("Email",email);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isConnected()) {
            if (authStateListener != null) {
                FirebaseAuth.getInstance().signOut();
            }
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isConnected()) {
            signInButton.setEnabled(true);
            if (authStateListener != null) {
                firebaseAuth.removeAuthStateListener(authStateListener);
            }
        }
    }

    public void setWifiVisibility(boolean visible){
        if(visible){
            noWifiText.setVisibility(View.VISIBLE);
            noWifi.setVisibility(View.VISIBLE);
            TnC.setVisibility(View.INVISIBLE);
            refreshButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            refreshText.setVisibility(View.VISIBLE);
        }
        else {
            noWifiText.setVisibility(View.INVISIBLE);
            noWifi.setVisibility(View.INVISIBLE);
            TnC.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            refreshButton.setVisibility(View.INVISIBLE);
            refreshText.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()!= NetworkInfo.State.CONNECTED&&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()!=NetworkInfo.State.CONNECTED){
            return false;
        }
        else {
            return true;
        }
    }

    public void onClickRefresh(View view){
        finish();
        startActivity(getIntent());
    }


}

