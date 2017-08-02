package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        WebView view = (WebView) findViewById(R.id.view_license);
        //view.getSettings().setSupportZoom(true);
       // view.getSettings().setLoadWithOverviewMode(true);
       // view.getSettings().setUseWideViewPort(true);
        view.loadUrl("file:///android_asset/licenses.html");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
