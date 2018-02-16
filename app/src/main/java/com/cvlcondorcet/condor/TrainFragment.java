package com.cvlcondorcet.condor;


import android.content.Intent;
import android.net.Uri;
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
import java.nio.charset.Charset;

import static android.view.View.GONE;

/**
 * Displays in a WebView part of website
 * @author Quentin DE MUYNCK
 *
 */
public class TrainFragment extends Fragment {

    private static final String url = "http://www.sncf.com/sncf/gare?libelleGare=Belfort";
    private WebView web_view;
    private String user;
    private ProgressBar bar;


    public TrainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_train, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        getActivity().setTitle(R.string.sncf);
        web_view = view.findViewById(R.id.web_view_train);
        bar = view.findViewById(R.id.loading_layout);
        web_view.setWebViewClient(new WebViewClient() {
            /**
             * Handles the click on a URL by user by starting browser (too complicated to handle this inside the fragment)
             * @param view  the webview
             * @param url   the requested URL
             * @return  True
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
                return true;
            }

            /**
             * Removes the loader spinner
             * @param view  useless
             * @param url   useless
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                bar.setVisibility(GONE);
            }
        });
        user = web_view.getSettings().getUserAgentString();
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);

       // Log.i("g", "FRAMGNET STARTED");
        bar.setVisibility(View.VISIBLE);
        new Loading().execute(url);
    }


    /**
     * Loads the website inside the WebView and filters elements to display only main content.
     * @author Quentin DE MUYNCK
     */
    private class Loading extends AsyncTask<String, Void, Void> {
        private Elements element;
        @Override
        public Void doInBackground(String... args) {
          //  Log.i("DO", "BACKGROUND");
            if (MainActivity.allowConnect(getActivity())) {
                try {
                    element = new Elements();
                 //   Log.i("e", user);
                    Document doc = Jsoup.connect(args[0]).userAgent(user).header("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3").get();
                    doc.charset(Charset.forName("UTF-8"));
                    Element el = doc.select("head").first();
                    Element el2 = doc.select(".page-content").first();
                    element.add(el);
                    element.add(el2);
                  //  Log.i("DO", "FOREGOUND");
                } catch (IOException e) {}
            }
            return null;
        }
        @Override
        public void onPostExecute(Void result) {
           // Log.i("START", "LOADING");
            web_view.loadDataWithBaseURL("http://m.sncf.com", element.toString(), "text/html", "gzip", "");
           // Log.i("END", "LOADING");
        }
    }

}
