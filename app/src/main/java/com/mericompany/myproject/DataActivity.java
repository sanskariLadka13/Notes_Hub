package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    TextView noFileText;
    ImageView lookingIcon;
    ImageView noWifi;
    TextView noWifiText;

    FirebaseAuth mAuth;

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


        setSpinnerList();

        spinnerSetup();

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //can be enabled if want to download from default browser...
                //Intent readerIntent = new Intent(Intent.ACTION_VIEW);
                //readerIntent.setData(Uri.parse(fileUrl.get(i)));
                //startActivity(readerIntent);

                view.setClickable(false);
                selectedFileName = fileName.get(i) + ".pdf";
                DownloadingTask downLoader = new DownloadingTask(DataActivity.this,view);
                downLoader.execute(fileUrl.get(i));
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
            case R.id.signout_data :
                mAuth.signOut();
                Intent loginActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginActivity);
                return true;
            case R.id.contribute_data :
                Intent uploadIntent = new Intent(getApplicationContext(), ChooseActivity.class);
                startActivity(uploadIntent);
                return true;
            default:
                return true;
        }
    }


    public void showList(View view){
        if(isConnected()) {
            setWifiVisibility(false);
                if (selectedBatch == batch.get(0))
                    Toast.makeText(this, "Please select any batch...", Toast.LENGTH_SHORT).show();
                else if (selectedSem == sem.get(0))
                    Toast.makeText(this, "Please select any sem...", Toast.LENGTH_SHORT).show();
                else if (selectedBranch == branch.get(0))
                    Toast.makeText(this, "Please select any branch...", Toast.LENGTH_SHORT).show();
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
            fileName.clear();
            fileUrl.clear();
            fileSize.clear();
            MyListAdapter adapter = new MyListAdapter(DataActivity.this,fileName,fileSize,R.drawable.new_pdf_icon,R.drawable.download_icon);
            fileListView.setAdapter(adapter);

            Log.i("bgfgfh",FirebaseDatabase.getInstance().getReference().child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).toString());

            FirebaseDatabase.getInstance().getReference().child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        fileName.add(dataSnapshot.child("fileName").getValue().toString());
                        fileUrl.add(dataSnapshot.child("fileUrl").getValue().toString());
                        fileSize.add("3 MB");
                        //TODO file size needs to be added
                        // automaticlly refresser to default view..
                        setSearchingVisibility(false);
                        MyListAdapter adapter = new MyListAdapter(DataActivity.this,fileName,fileSize,R.drawable.new_pdf_icon,R.drawable.download_icon);
                        fileListView.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.i("errrrrrr","errorr 1");
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
            Log.i("errrrrrr","errorr 2");
            e.printStackTrace();
            //TODO some error occurred...
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
        batch.add("2k16");
        batch.add("2k17");
        batch.add("2k18");
        batch.add("2k19");
        batch.add("2k20");
        batch.add("2k21");
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
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()==NetworkInfo.State.CONNECTED){
            return true;
        }
        else return false;
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
}