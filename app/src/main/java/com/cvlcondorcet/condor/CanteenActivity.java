package com.cvlcondorcet.condor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class CanteenActivity extends Fragment {

    private PDFView view;
    private File file;
    private Context ctx;
   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_canteen);
        setTitle("Menu de la cantine");

        view = (PDFView) findViewById(R.id.pdfView);
        file = new File(getApplicationContext().getFilesDir().toString() + "/" + "menus-du-6-au-30-juin-2017.pdf");
        ctx = this;

        loadPdf();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.activity_canteen, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle("Menu de la cantine");

        this.view = (PDFView) view.findViewById(R.id.pdfView);
        file = new File(getActivity().getFilesDir().toString() + "/" + "menus-du-6-au-30-juin-2017.pdf");
        /*ctx = this;*/

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

                 getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.zoomWithAnimation(20, 710, 2.3f);

                    }
                });
            }
        }).start();
    }
}
