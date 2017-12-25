package com.cvlcondorcet.condor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * WebView that displays a restricted part of web page and handling some navigation.
 * @author Quentin DE MUYNCK
 */

public class BusFragment extends Fragment {
    private static final String url = "http://www.optymo.fr/infos_trafic/";
    private WebView web_view;
    private ProgressBar progress;
    private String user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        getActivity().setTitle(R.string.optymo);
        progress = view.findViewById(R.id.loading_layout);
        web_view = view.findViewById(R.id.web_view_bus);
        web_view.setWebViewClient(new WebViewClient() {
            /**
             * Avoids default method and loads the page inside the current WebView if it corresponds to the genuine website.
             * @param view  the WebView
             * @param url   the URL that must be loaded
             * @return  True if we handle, False otherwise
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.i("E", "OVERRIDE");
                if (url.contains("optymo")) {
                    progress.setVisibility(View.VISIBLE);
//                    Log.i("E", "INSIDE");
                    new Loading().execute(url);
                    return true;
                } else { return false; }

            }

            /**
             * Hides loader spinner
             * @param view  No use
             * @param url   nn use
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }
        });
        user = web_view.getSettings().getUserAgentString();
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.getSettings().setDefaultTextEncodingName("utf-8");

//        Log.i("g", "FRAMGNET STARTED");
        new Loading().execute(url);
    }

    /**
     * Handles back pressed button and redirects it to WebView to go back.
     * @see MainActivity#onBackPressed()
     */
    public boolean backPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Loads the website, cleans it and removes useless parts
     */
    private class Loading extends AsyncTask<String, Void, Void> {
        private Elements element;
        private String url;

        @Override
        public void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }
        @Override
        public Void doInBackground(String... args) {
//            Log.i("DO", "BACKGROUND");
            if (MainActivity.allowConnect(getActivity())) {
                try {
                    url = args[0];
                    element = new Elements();
                    //Log.i("e", user);
                    Document doc = Jsoup.connect(url).userAgent(user).header("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3").header("Accept-Encoding", "gzip, deflate").get();
                    Element el = doc.select("head").first();
                    Element el2 = doc.select("#go-to-main").first();
                   // Log.i("HELLO", el2.toString());
                    element.add(el);
                    element.add(el2);
                    //Log.i("DO", "FOREGOUND");
                } catch (IOException e) {}
            }
            return null;
        }
        @Override
        public void onPostExecute(Void result) {
            //Log.i("START", "LOADING");
            web_view.loadDataWithBaseURL(null, element.toString(), "text/html", "UTF-8", url);
            //Log.i("END", "LOADING");
        }
    }

}
