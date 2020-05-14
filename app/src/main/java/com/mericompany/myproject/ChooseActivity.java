package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseActivity extends AppCompatActivity {

     Intent intent ;
     Uri selectedFile;

    final static int PICK_PDF_CODE = 2342;

    StorageReference mStorageRefrence;
    DatabaseReference mDatabaseRefrence;

    ProgressBar progressBar ,uploadProgress;
    TextView fileNameView;
    TextView showName,uploadPercent;

    Button uploadButton;

    Spinner branchSpinner,semSpinner,batchSpinner,typeSpinner;

    ArrayAdapter<String> branchAdapter;
    ArrayAdapter<String> batchAdapter;
    ArrayAdapter<String> semAdapter;
    ArrayAdapter<String> typeAdapter;
    ArrayAdapter<String> nameAdapter;

    ArrayList<String> branch = new ArrayList<>();
    ArrayList<String> batch = new ArrayList<>();
    ArrayList<String> sem = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();

    String selectedBranch,selectedSem,selectedBatch,selectedType;
    HashMap fileDetail;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getPDF();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        fileDetail = new HashMap();

        intent = new Intent(getApplicationContext(),DataActivity.class);

        // getting firebase object...
        mStorageRefrence = FirebaseStorage.getInstance().getReference();
        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference("uploads");

        fileNameView = findViewById(R.id.fileNameTextView);
        showName = findViewById(R.id.showName);

        uploadButton = findViewById(R.id.uploadButton);
        uploadProgress = findViewById(R.id.uploadProgress);
        uploadPercent = findViewById(R.id.uploadPercent);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        uploadProgress.setVisibility(View.INVISIBLE);
        uploadPercent.setVisibility(View.INVISIBLE);

        branchSpinner = findViewById(R.id.branchChooseFileSpinner);
        semSpinner = findViewById(R.id.semChooseFileSpinner);
        batchSpinner = findViewById(R.id.batchChooseFileSpinner);
        typeSpinner = findViewById(R.id.typeSpinner);

        setSpinner();

        branchAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, branch);
        semAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sem);
        batchAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, batch);
        typeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,type);

        branchSpinner.setAdapter(branchAdapter);
        batchSpinner.setAdapter(batchAdapter);
        semSpinner.setAdapter(semAdapter);
        typeSpinner.setAdapter(typeAdapter);

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("branch",branch.get(i));
                intent.putExtra("branch",branch.get(i));
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
                intent.putExtra("sem",sem.get(i));
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
                intent.putExtra("batch",batch.get(i));
                selectedBatch = batch.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1:selectedType = "[Notes]";
                    break;
                    case 2:selectedType = "[Ques]";
                    break;
                    case 3:selectedType = "[Lab]";
                    break;
                    default:selectedType = type.get(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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

        //===//
        type.add("Select Type");
        type.add("Notes");
        type.add("Question Paper");
        type.add("Lab File");
        ///////


    }


    private void getPDF() {

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setAction(Intent.ACTION_APP_ERROR)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_PDF_CODE);
    }

    private void uploadFile(Uri data) {
        final String name = fileNameView.getText().toString() + System.currentTimeMillis() + ".pdf";
        progressBar.setVisibility(View.VISIBLE);
        uploadProgress.setVisibility(View.VISIBLE);
        uploadPercent.setVisibility(View.VISIBLE);
        uploadProgress.setProgress(0);
        final StorageReference sRef = mStorageRefrence.child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).child(name);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        uploadProgress.setVisibility(View.GONE);
                        uploadPercent.setVisibility(View.GONE);
                        uploadButton.setClickable(true);

                        mStorageRefrence.child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                            fileDetail.put("fileName",selectedType+fileNameView.getText().toString());
                            fileDetail.put("fileUrl",uri.toString());
                            FirebaseDatabase.getInstance().getReference().child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).push().setValue(fileDetail);
                            startActivity(intent);
                            Log.i("download url",uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                uploadButton.setClickable(true);
                                e.printStackTrace();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        uploadButton.setClickable(true);
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress =  (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        uploadPercent.setText((int)progress+"%");
                        uploadProgress.setProgress((int)progress);
                        Log.i("progress",  progress+"%");
                    }
                });
    }

    public  void onClickChooseFile(View view){
        //file uploaded to thr server in an organized way.....
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else {
            getPDF();
        }
    }

    public void onClickUpload(View view){
        if(selectedBatch == batch.get(0))
            Toast.makeText(this, "Please select any batch...", Toast.LENGTH_SHORT).show();
        else if(selectedSem == sem.get(0))
            Toast.makeText(this, "Please select any sem...", Toast.LENGTH_SHORT).show();
        else if(selectedBranch == branch.get(0))
            Toast.makeText(this, "Please select any branch...", Toast.LENGTH_SHORT).show();
        else if(selectedType == type.get(0))
            Toast.makeText(this, "Please select type of Document...", Toast.LENGTH_SHORT).show();
        else if(fileNameView.getText().length()==0)
            Toast.makeText(this, "Give your file some descripive name...", Toast.LENGTH_SHORT).show();
        else if(selectedFile == null){
            Toast.makeText(this, "Please select any file...", Toast.LENGTH_SHORT).show();
        }
        else {
            uploadFile(selectedFile);
            uploadButton.setClickable(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_PDF_CODE:
                if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    //if a file is selected
                    if (data.getData() != null) {
                        //uploading the file
                        HelperClass helper = new HelperClass();
                        selectedFile = data.getData();
                        showName.setText(helper.getFileName(this,selectedFile));
                        Log.i("check",data.getData().getPath());
                        //uploadFile(data.getData());
                    }else{
                        Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }
    ///just check

    //again double check
}
