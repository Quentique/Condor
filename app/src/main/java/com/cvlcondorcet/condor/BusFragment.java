package com.cvlcondorcet.condor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Quentin DE MUYNCK on 24/07/2017.
 */

public class BusFragment extends Fragment {
    private static final String url = "http://www.optymo.fr/infos_trafic/";
    private WebView web_view;
    private String user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        web_view = view.findViewById(R.id.web_view_bus);
        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("optymo")) {
                    new Loading().execute(url);
                    return true;
                } else { return false; }

            }
        });
        user = web_view.getSettings().getUserAgentString();
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.getSettings().setDefaultTextEncodingName("utf-8");

        Log.i("g", "FRAMGNET STARTED");
        new Loading().execute(url);
    }

    public void backPressed() {
        web_view.goBack();
    }

    private class Loading extends AsyncTask<String, Void, Void> {
        private Elements element;
        @Override
        public Void doInBackground(String... args) {
            Log.i("DO", "BACKGROUND");
            if (MainActivity.allowConnect(getActivity())) {
                try {
                    element = new Elements();
                    Log.i("e", user);
                    Document doc = Jsoup.connect(args[0]).userAgent(user).header("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3").header("Accept-Encoding", "gzip, deflate").get();
                    //doc.charset(Charset.forName("UTF-8"));
                    Element el = doc.select("head").first();
                    Element el2 = doc.select("#go-to-main").first();
                    Log.i("HELLO", el2.toString());
                    //   el.wrap("<body></body>");
                    element.add(el);
                    element.add(el2);
                    // element = doc.getAllElements();
                   /* element.getElementsByAttributeValue("class", "container_12").first().remove();
                    element.getElementsByAttributeValue("class", "container_12").first().remove();*/
                    Log.i("DO", "FOREGOUND");
                } catch (IOException e) {}
            }
            return null;
        }
        @Override
        public void onPostExecute(Void result) {
            Log.i("START", "LOADING");
            web_view.loadDataWithBaseURL(null, element.toString(), "text/html", "UTF-8", "");
            //  web_view.loadUrl(url);

            // web_view.loadUrl(url);
            Log.i("END", "LOADING");
        }
    }

}
