package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import static com.mericompany.myproject.MyListAdapter.percent;

public class ChooseActivity extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener{

     Intent intent, homeIntent;
     Uri selectedFile;

    final static int PICK_PDF_CODE = 2342;

    StorageReference mStorageRefrence;
    DatabaseReference mDatabaseRefrence;

    ProgressBar uploadProgress;
    TextView fileNameView;
    TextView showName,uploadPercent;

    ImageButton uploadButton;
    TextView uploadText;
    ImageButton chooseButton;

    Spinner branchSpinner,semSpinner,batchSpinner,typeSpinner;

    MySpinnerAdapter branchAdapter;
    MySpinnerAdapter batchAdapter;
    MySpinnerAdapter semAdapter;
    MySpinnerAdapter typeAdapter;

    RelativeLayout chooseLayout;

    ArrayList<String> branch = new ArrayList<>();
    ArrayList<String> batch = new ArrayList<>();
    ArrayList<String> sem = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();

    String selectedBranch,selectedSem,selectedBatch,selectedType;
    HashMap fileDetail;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id ;

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

        homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
        intent = getIntent();
        // getting firebase object...
        mStorageRefrence = FirebaseStorage.getInstance().getReference();
        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference("uploads");

        chooseLayout = findViewById(R.id.chooseLayout);
        chooseLayout.setOnClickListener(this);

        fileNameView = findViewById(R.id.fileNameTextView);
        showName = findViewById(R.id.showName);
        chooseButton = findViewById(R.id.chooseFileButton);

        uploadButton = findViewById(R.id.uploadButtonS);
        uploadText = findViewById(R.id.uploading_text);

        uploadProgress = findViewById(R.id.uploadProgress);
        uploadPercent = findViewById(R.id.uploadPercent);

        uploadProgress.setVisibility(View.INVISIBLE);
        uploadPercent.setVisibility(View.INVISIBLE);

        setSpinnerList();
        spinnerSetup();

    }

    public  void setSpinnerList(){
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_PDF_CODE);
    }

    private void uploadFile(final Uri data) {
        /***************************
        // changes made + System.currentTimeMillis()
         ***************************/
        final String name = fileNameView.getText().toString()  + ".pdf";
        uploadProgress.setVisibility(View.VISIBLE);
        uploadPercent.setVisibility(View.VISIBLE);
        uploadProgress.setProgress(0);


        final StorageReference sRef = mStorageRefrence.child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).child(name);
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        uploadProgress.setVisibility(View.GONE);
                        uploadPercent.setVisibility(View.GONE);
                        setButtonClickable(true);

                        mStorageRefrence.child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                            fileDetail.put("Email",intent.getStringExtra("Email"));
                            fileDetail.put("Sender",intent.getStringExtra("Sender"));
                            fileDetail.put("fileName",selectedType+fileNameView.getText().toString()+".pdf");
                            fileDetail.put("fileUrl",uri.toString());
                            fileDetail.put("fileSize",taskSnapshot.getTotalByteCount());

                                mNotifyManager.cancel(id);
                                createNotification("File Uploaded Successfully");

                            FirebaseDatabase.getInstance().getReference().child("uploads").child(selectedBatch).child(selectedSem).child(selectedBranch).push().setValue(fileDetail);
                            startActivity(homeIntent);
                            Log.i("download url",uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                setButtonClickable(true);

                                mNotifyManager.cancel(id);
                                createNotification("Uploading Failed");

                                Toast.makeText(ChooseActivity.this, "Some error occurred, Try Again...", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        setButtonClickable(true);
                        mNotifyManager.cancel(id);
                        createNotification("Uploading Failed");
                        Toast.makeText(ChooseActivity.this, "Some error occurred, Try Again...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress =  (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        uploadPercent.setText((int)progress+"%");
                        uploadProgress.setProgress((int)progress);
                        if(progress>0.5) {
                            //notification
                            mBuilder.setProgress(100, (int) progress, false)
                                    .setContentText((int) progress + " %");
                            mNotifyManager.notify(id, mBuilder.build());
                        }

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
            setButtonClickable(false);
            createNotification("Uploading ...");
            uploadFile(selectedFile);
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

    public void setButtonClickable(boolean lock){

        if(!lock) showName.setInputType(InputType.TYPE_NULL);
        else showName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        if(!lock){
            uploadText.setText("Uploading");
        }
        else uploadText.setText("Upload");

        showName.setClickable(lock);
        showName.setEnabled(lock);
        batchSpinner.setClickable(lock);
        batchSpinner.setEnabled(lock);
        semSpinner.setClickable(lock);
        semSpinner.setEnabled(lock);
        branchSpinner.setClickable(lock);
        branchSpinner.setEnabled(lock);
        uploadText.setEnabled(lock);
        uploadText.setClickable(lock);
        uploadButton.setClickable(lock);
        uploadButton.setEnabled(lock);
        chooseButton.setClickable(lock);
        chooseButton.setEnabled(lock);
        typeSpinner.setEnabled(lock);
    }

    public void spinnerSetup(){
        branchSpinner = findViewById(R.id.branchChooseFileSpinner);
        semSpinner = findViewById(R.id.semChooseFileSpinner);
        batchSpinner = findViewById(R.id.batchChooseFileSpinner);
        typeSpinner = findViewById(R.id.typeSpinner);


        typeAdapter = new MySpinnerAdapter(getApplicationContext(),type);
        batchAdapter   = new MySpinnerAdapter(getApplicationContext(),batch);
        branchAdapter = new MySpinnerAdapter(getApplicationContext(),branch);
        semAdapter = new MySpinnerAdapter(getApplicationContext(),sem);

        semSpinner.setAdapter(semAdapter);
        branchSpinner.setAdapter(branchAdapter);
        batchSpinner.setAdapter(batchAdapter);
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
                    case 1:selectedType = "[Notes] ";
                        break;
                    case 2:selectedType = "[Ques] ";
                        break;
                    case 3:selectedType = "[Lab] ";
                        break;
                    default:selectedType = type.get(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == chooseLayout.getId()){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(),0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
    }

    public void createNotification(String msg){

        //Notification stuff
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this,"notify_001");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotifyManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mBuilder.setContentTitle(fileNameView.getText().toString() +".pdf")
                .setContentText(msg)
                .setSmallIcon(R.drawable.outbox)
                .setOnlyAlertOnce(true);

        mNotifyManager.notify(id,mBuilder.build());
    }

}
