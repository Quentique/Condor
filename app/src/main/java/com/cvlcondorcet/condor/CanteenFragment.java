package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

/**
 * Displays a PDF File, uses another library.
 * @author Quentin DE MUYNCK
 * @see com.github.barteksc.pdfviewer.PDFView
 */
public class CanteenFragment extends Fragment {

    private PDFView view;
    private File file;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_canteen, parent, false);
    }

    /**
     * Sets view up and loads PDF file from Database
     * @param view  the view
     * @param savedInstanceState    oldState
     * @see Database#timestamp(String)
     * @see com.github.barteksc.pdfviewer.PDFView
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.canteen_menu));
        Database db = new Database(getActivity());
        db.open();
        this.view = view.findViewById(R.id.pdfView);
        file = new File(getActivity().getFilesDir().toString() + "/" + db.timestamp("canteen"));
        db.close();
        loadPdf();
    }

    /**
     * Loads PDF File and sets parameters.
     */
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

                 try {
                     getActivity().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             try {
                                 view.zoomWithAnimation(20, 710, 2.3f);
                             } catch (NullPointerException e) {
                             }
                         }
                     });
                 } catch (NullPointerException e) { }
            }
        }).start();
    }
}
