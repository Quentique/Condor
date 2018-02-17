package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Simple WebView Fragment, displays CVL information (by a web page)
 * @author Quentin DE MUYNCK
 */

public class CVLFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.cvl));
        Database db = new Database(getActivity());
        db.open();
        WebView wview = view.findViewById(R.id.web_view_train);
        wview.getSettings().setSupportZoom(true);
        wview.getSettings().setBuiltInZoomControls(true);
        wview.getSettings().setDisplayZoomControls(false);
        wview.loadUrl("file:///"+getActivity().getApplicationContext().getFilesDir().toString()+"/"+ db.timestamp("cvl"));
        db.close();
        view.findViewById(R.id.loading_layout).setVisibility(View.GONE);
    }
}
