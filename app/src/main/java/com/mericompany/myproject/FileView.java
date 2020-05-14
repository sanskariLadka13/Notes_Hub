package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;

public class FileView extends AppCompatActivity{

    ArrayList<String> filePath = new ArrayList<>();
    ArrayList<String> fileName = new ArrayList<>();
    ArrayList<String> fileSize = new ArrayList<String>();


    ArrayAdapter<String> branchAdapter;
    ArrayAdapter<String> batchAdapter;
    ArrayAdapter<String> semAdapter;

    Spinner batchSpinner;
    Spinner semSpinner;
    Spinner branchSpinner;

    ArrayList<String> branch = new ArrayList<>();
    ArrayList<String> sem = new ArrayList<>();
    ArrayList<String> batch = new ArrayList<>();

    String selectedBranch,selectedSem,selectedBatch;
    FirebaseAuth mAuth;

    TextView noFileText;
    ListView list;
    ImageView noFileFace;

    Integer image;

    MyListAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.file_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.addDocs:
                Intent dataActivity = new Intent(getApplicationContext(), DataActivity.class);
                startActivity(dataActivity);
                return true;
            case R.id.logout :
                mAuth.signOut();
                Intent loginActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginActivity);
                return true;
            case R.id.contribute :
                Intent chooseIntent = new Intent(getApplicationContext(), ChooseActivity.class);
                startActivity(chooseIntent);
                return true;
            default:
                return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showFilesFromStorage();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mAuth = FirebaseAuth.getInstance();

        batchSpinner = findViewById(R.id.batchFileSpinner);
        semSpinner = findViewById(R.id.semFileSpinner);
        branchSpinner = findViewById(R.id.branchFileSpinner);

        setSpinner();

        noFileText = findViewById(R.id.noFileText);
        noFileFace = findViewById(R.id.noFileFace);
        noFileFace.setVisibility(View.INVISIBLE);
        noFileText.setVisibility(View.INVISIBLE);
        list = findViewById(R.id.list);
        image = R.drawable.new_pdf_icon;

        batchAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,batch);
        branchAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,branch);
        semAdapter =  new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,sem);

        semSpinner.setAdapter(semAdapter);
        branchSpinner.setAdapter(branchAdapter);
        batchSpinner.setAdapter(batchAdapter);

        // All spinner management............

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBranch = branch.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSem = sem.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBatch = batch.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent pdfIntent = new Intent(getApplicationContext(),PdfReader.class);
                pdfIntent.putExtra("filePath",filePath.get(i));
                startActivity(pdfIntent);
            }
        });

    }

    public void onClickLoadFile(View view){
        if(selectedBatch == batch.get(0))
            Toast.makeText(this, "Please select any batch...", Toast.LENGTH_SHORT).show();
        else if(selectedSem == sem.get(0))
            Toast.makeText(this, "Please select any sem...", Toast.LENGTH_SHORT).show();
        else if(selectedBranch == branch.get(0))
            Toast.makeText(this, "Please select any branch...", Toast.LENGTH_SHORT).show();
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else {
                showFilesFromStorage();
            }
        }
    }

    public void showFilesFromStorage(){
        noFileText.setVisibility(View.INVISIBLE);
        noFileFace.setVisibility(View.INVISIBLE);
        fileName.clear();
        filePath.clear();
        fileSize.clear();
        adapter=new MyListAdapter(this, fileName, fileSize,image);

        String path = Environment.getExternalStorageDirectory().toString()+"/" +".uploads/"+ selectedBatch + "/" + selectedSem + "/" + selectedBranch;
        Log.d("Files", "Path: " + path);

        File directory = new File(path);
        if(directory.exists()) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName: " + files[i].getName());
                Log.d("Files", "FileName: " + files[i].getAbsolutePath());
                fileName.add(files[i].getName());
                filePath.add(files[i].getPath());
                // need to corrct hte decimal places...
                if((files[i].length()/1048576.0)>1.0) {
                    fileSize.add(String.format("%.2f", files[i].length() / (1048576.0)) + " MB");
                }
                else {
                    fileSize.add(String.format("%.2f", files[i].length() / (1024.0)) + " KB");
                }
            }
        }
        else{
            noFileText.setVisibility(View.VISIBLE);
            noFileFace.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Nothing's there...", Toast.LENGTH_SHORT).show();
        }
        ///////////////////////////////////////////////////////////
        adapter=new MyListAdapter(this, fileName, fileSize,image);
        list.setAdapter(adapter);
    }
    public  void setSpinner(){
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
        //==//


    }

}
