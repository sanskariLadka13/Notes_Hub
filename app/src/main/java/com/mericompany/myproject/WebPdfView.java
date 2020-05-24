package com.mericompany.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

import static android.content.ContentValues.TAG;

public class WebPdfView extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    PDFView pdfView;
    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        pdfView= findViewById(R.id.PDFview);
        progressBar = findViewById(R.id.pdfProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        header = findViewById(R.id.tv_header);

        Intent intent = getIntent();
        String path = intent.getStringExtra("directory");
        String fileName = intent.getStringExtra("fileName");

        header.setText(fileName);

        final Context context = WebPdfView.this;

        final long HUNDRED_MEGA_BYTE = 500*1024*1024;
        FirebaseStorage.getInstance().getReference().child(path + getActualName(fileName)).getBytes(HUNDRED_MEGA_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                progressBar.setVisibility(View.INVISIBLE);
                pdfView.fromBytes(bytes)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(context))
                        .load();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WebPdfView.this, "Some error occured ...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        /*

        To open in WebView..........................

        Intent intent= getIntent();

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        //webView.setWebChromeClient(new WebChromeClient());

        //webView.setWebViewClient(new WebViewClient());
        //webView.loadUrl("http://www.google.com");


        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
                progressBar.setVisibility(View.GONE);
            }
        });


        //https://docs.google.com/viewerng/viewer?embedded=true&url=
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+intent.getStringExtra("fileUrl"));

         */

    }
    public String getActualName(String name){

        String newName ;
        int index = name.indexOf(' ') + 1;
        newName = name.substring(index);
        return newName;
    }




}
