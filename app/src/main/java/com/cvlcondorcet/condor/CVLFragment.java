package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Created by Quentin DE MUYNCK on 30/09/2017.
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
        Wview.loadUrl("file:///"+getActivity().getApplicationContext().getFilesDir().toString()+"/"+db.timestamp("cvl"));
        db.close();
        ((ProgressBar)view.findViewById(R.id.loading_layout)).setVisibility(View.GONE);
    }
}
