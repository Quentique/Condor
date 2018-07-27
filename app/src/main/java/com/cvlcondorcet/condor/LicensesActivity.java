package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Displays license file in a WebView
 * @author Quentin DE MUYNCK
 */
public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name;
        setContentView(R.layout.activity_licenses);
        WebView view = findViewById(R.id.view_license);
        view.setWebViewClient(new WebViewClient() {
            /**
             * Avoids default method and loads the page inside the current WebView if it corresponds to the genuine website.
             * @param view  the WebView
             * @param url   the URL that must be loaded
             * @return  True if we handle, False otherwise
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.i("E", "OVERRIDE");
                if (url.contains("conf")) {
                    view.loadUrl("file:///android_asset/conf.html");
                    return true;
                } else if (url.contains("cgu")) {
                    view.loadUrl("file:///android_asset/cgu.html");
                    return true;
                } else { return false; }

            }
        });
        try {
            name = getIntent().getStringExtra("name");
        } catch (Exception e) {name ="licenses";}
        if (name.equals("licenses")) {
            setTitle(R.string.licenses);
            view.loadUrl("file:///android_asset/licenses.html");
        } else if (name.equals("cgu")) {
            setTitle("CGU");
            view.loadUrl("file:///android_asset/cgu.html");
        } else {
            setTitle("Confidentialité");
            view.loadUrl("file:///android_asset/conf.html");
        }
    }
}
