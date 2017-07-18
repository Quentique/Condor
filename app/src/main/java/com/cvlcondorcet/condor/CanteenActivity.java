package com.cvlcondorcet.condor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class CanteenActivity extends AppCompatActivity {

    private PDFView view;
    private File file;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen);
        setTitle("Menu de la cantine");
        view = (PDFView) findViewById(R.id.pdfView);
        file = new File(getApplicationContext().getFilesDir().toString() + "/" + "menus-du-6-au-30-juin-2017.pdf");
        ctx =this;
        loadPdf();
    }

    private void loadPdf() {
        view.fromFile(file)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableAnnotationRendering(true)
                .load();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) { e.printStackTrace(); }
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.zoomWithAnimation(20, 710, 2.3f);

                    }
                });
            }
        }).start();
    }
}
