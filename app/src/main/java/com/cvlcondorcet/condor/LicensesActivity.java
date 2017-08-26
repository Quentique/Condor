package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Displays license file in a WebView
 * @author Quentin DE MUYNCK
 */
public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.licenses);
        setContentView(R.layout.activity_licenses);
        WebView view = (WebView) findViewById(R.id.view_license);
        view.loadUrl("file:///android_asset/licenses.html");
    }
}
