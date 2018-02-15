package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Simple WebView Fragment, displays CVL information (by a web page)
 * @author Quentin DE MUYNCK
 */

public class CVLFragment extends Fragment {

    WebView Wview;
    Database db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.cvl));
        db = new Database(getActivity());
        db.open();
        Wview = view.findViewById(R.id.web_view_train);
        Wview.getSettings().setSupportZoom(true);
        Wview.getSettings().setBuiltInZoomControls(true);
        Wview.getSettings().setDisplayZoomControls(false);
        Wview.loadUrl("file:///"+getActivity().getApplicationContext().getFilesDir().toString()+"/"+db.timestamp("cvl"));
        db.close();
        ((ProgressBar)view.findViewById(R.id.loading_layout)).setVisibility(View.GONE);
    }
}
