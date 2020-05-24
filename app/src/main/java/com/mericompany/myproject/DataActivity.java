package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.params.HttpParams;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {

    ArrayList<String> fileName = new ArrayList<>();
    ArrayList<String> fileUrl = new ArrayList<>();
    ArrayList<String> fileSize = new ArrayList<>();

    MySpinnerAdapter branchAdapter,batchAdapter,semAdapter;

    ArrayList<String> branch = new ArrayList<>();
    ArrayList<String> batch = new ArrayList<>();
    ArrayList<String> sem = new ArrayList<>();

    ListView fileListView;
    ProgressBar progressBar;
    TextView noFileText,gangsterText;
    ImageView lookingIcon,gangsterImage;
    ImageView noWifi;
    TextView noWifiText;
    TextView help;

    FirebaseAuth mAuth;

    MyListAdapter adapter;

    static String selectedBranch,selectedSem,selectedBatch,selectedFileName;

    Spinner branchSpinner,semSpinner,batchSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        mAuth = FirebaseAuth.getInstance();

        fileListView = findViewById(R.id.dataList);
        progressBar = findViewById(R.id.progressBarData);
        noFileText = findViewById(R.id.noFileText);
        lookingIcon = findViewById(R.id.searching_icon);
        noWifi = findViewById(R.id.noWifi);
        noWifiText = findViewById(R.id.noWifiText);
        gangsterImage = findViewById(R.id.gangsterImage);
        gangsterText = findViewById(R.id.gangsterText);
        help = findViewById(R.id.help_dataText);

        help.setVisibility(View.INVISIBLE);

        adjustHeight();

        setSpinnerList();

        spinnerSetup();

        fileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent webViewIntent = new Intent(getApplicationContext(),WebPdfView.class);
                webViewIntent.putExtra("fileName",fileName.get(i));
                webViewIntent.putExtra("directory","uploads/"+selectedBatch+"/"+selectedSem+"/"+selectedBranch+"/");
                startActivity(webViewIntent);
                return true;
            }
        });

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //can be enabled if want to download from default browser...
                //Intent readerIntent = new Intent(Intent.ACTION_VIEW);
                //readerIntent.setData(Uri.parse(fileUrl.get(i)));
                //startActivity(readerIntent);

                if(adapter.downloadLinkState.get(i) == adapter.DOWNLOAD_START  || adapter.downloadLinkState.get(i) == adapter.DOWNLOAD_ERROR) {
                    selectedFileName = fileName.get(i);
                    DownloadingTask downLoader = new DownloadingTask(DataActivity.this, view,i,adapter);
                    downLoader.execute(fileUrl.get(i));
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.data_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.data_help :
                Intent helpActivity = new Intent(getApplicationContext(), Help.class);
                startActivity(helpActivity);
                return true;
            case R.id.upload_data :
                Intent uploadIntent = new Intent(getApplicationContext(), SignInCheck.class);
                startActivity(uploadIntent);
                return true;
            default:
                return true;
        }
    }

    public void showList(View view){
        help.setVisibility(View.INVISIBLE);
        if(isConnected()) {
            setWifiVisibility(false);

            fileName.clear();
            fileUrl.clear();
            fileSize.clear();
            adapter = new MyListAdapter(DataActivity.this,fileName,fileSize,R.drawable.new_pdf_icon,R.drawable.download_icon);
            fileListView.setAdapter(adapter);

            setGangstaMessage(false,"");
            String warning;
            if(selectedBatch == batch.get(0)) {
                warning = "Please select any batch...";
                Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
                setGangstaMessage(true,warning);
            }
            else if(selectedSem == sem.get(0)) {
                warning = "Please select any sem...";
                Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
                setGangstaMessage(true,warning);
            }
            else if(selectedBranch == branch.get(0)){
                warning = "Please select any branch...";
                Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
                setGangstaMessage(true,warning);
            }
                else {
                    getFilesFromDatabase();
                }
        }
        else{
            setSearchingVisibility(false);
            setWifiVisibility(true);
        }
    }

    public void getFilesFromDatabase(){
        setSearchingVisibility(true);
        try {

            FirebaseDatabase.getInstance().getReference().child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        help.setVisibility(View.VISIBLE);
                        fileName.add(dataSnapshot.child("fileName").getValue().toString());
                        fileUrl.add(dataSnapshot.child("fileUrl").getValue().toString());
                        long len = (long)dataSnapshot.child("fileSize").getValue();
                        if(( len/1048576.0)>1.0) {
                            fileSize.add(String.format("%.2f", len / (1048576.0)) + " MB");
                        }
                        else {
                            fileSize.add(String.format("%.2f", len / (1024.0)) + " KB");
                        }
                        setSearchingVisibility(false);

                        adapter.updateLength(fileName.size());
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        help.setVisibility(View.INVISIBLE);
                        e.printStackTrace();
                        Toast.makeText(DataActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { Log.i("checkk","changed"); }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { Log.i("checkk","removed"); }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {Log.i("checkk","moved"); }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {Log.i("checkk","error"); }
            });
        }catch (Exception e){
            e.printStackTrace();
            setWifiVisibility(isConnected());
            setSearchingVisibility(false);
            Toast.makeText(this, "Some error occurred...", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSpinnerList(){

        // adding branch to the spinner
        branch.add("Select Branch");
        branch.add("ECE");
        branch.add("CSE");
        branch.add("Civil");
        branch.add("EE");
        branch.add("Architecture");
        branch.add("IMSc");
        branch.add("Mechanical");
        //=======//
        sem.add("Select Sem");
        sem.add("1st");
        sem.add("2nd");
        sem.add("3rd");
        sem.add("4th");
        sem.add("5th");
        sem.add("6th");
        sem.add("7th");
        sem.add("8th");
        sem.add("9th");
        sem.add("10th");
        //====//
        batch.add("Select Batch");
        batch.add("2k15");
        batch.add("2k16");
        batch.add("2k17");
        batch.add("2k18");
        batch.add("2k19");
        batch.add("2k20");
        batch.add("2k21");
        batch.add("2k22");
        batch.add("2k23");
        batch.add("2k24");
        batch.add("2k25");
        //========//

    }

    public  static String getDirectory(){
        return ".uploads/"+selectedBatch + "/" + selectedSem + "/" +selectedBranch;
    }

    public  static String getFileName(){
        return selectedFileName;
    }

    public boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()!= NetworkInfo.State.CONNECTED&&
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()!=NetworkInfo.State.CONNECTED){
            return false;
        }
        else {
            try {
                String link = "";
                URL url = new URL(link);//Create Download URl
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();//Open Url Connection
                urlConnection.setConnectTimeout(6000);

            } catch (Exception e) {

            }
            return true;
        }
    }

    public void setWifiVisibility(boolean visible){
        if(visible){
            noWifiText.setVisibility(View.VISIBLE);
            noWifi.setVisibility(View.VISIBLE);
        }
        else {
            noWifiText.setVisibility(View.INVISIBLE);
            noWifi.setVisibility(View.INVISIBLE);
        }
    }

    public void setSearchingVisibility(boolean visible){
        if(visible){
            progressBar.setVisibility(View.VISIBLE);
            noFileText.setVisibility(View.VISIBLE);
            lookingIcon.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            noFileText.setVisibility(View.INVISIBLE);
            lookingIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void spinnerSetup(){

        branchSpinner = findViewById(R.id.branchSpinner);
        semSpinner = findViewById(R.id.semSpinner);
        batchSpinner = findViewById(R.id.batchSpinner);

        batchAdapter   = new MySpinnerAdapter(getApplicationContext(),batch);
        branchAdapter = new MySpinnerAdapter(getApplicationContext(),branch);
        semAdapter = new MySpinnerAdapter(getApplicationContext(),sem);

        semSpinner.setAdapter(semAdapter);
        branchSpinner.setAdapter(branchAdapter);
        batchSpinner.setAdapter(batchAdapter);

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("branch",branch.get(i));
                selectedBranch = branch.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.removeView(branchSpinner);
            }
        });

        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("sem",sem.get(i));
                selectedSem = sem.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.removeView(semSpinner);
            }
        });

        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("batch",batch.get(i));
                selectedBatch = batch.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    public void adjustHeight(){

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int density = (int) getResources().getDisplayMetrics().density;

        ViewGroup.LayoutParams params = fileListView.getLayoutParams();
        params.height = heightPixels - (300*density);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(homeIntent);
    }

    public void setGangstaMessage(boolean visible,String message){
        if(visible){
            gangsterImage.setVisibility(View.VISIBLE);
            gangsterText.setVisibility(View.VISIBLE);
            gangsterText.setText(message);
        }
        else {
            gangsterImage.setVisibility(View.INVISIBLE);
            gangsterText.setVisibility(View.INVISIBLE);
        }
    }

}