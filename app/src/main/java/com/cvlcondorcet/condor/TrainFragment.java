package com.cvlcondorcet.condor;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainFragment extends Fragment {

    private static final String url = "http://www.sncf.com/sncf/gare?libelleGare=Belfort";
    private WebView web_view;
    private String user;
    private ProgressBar bar;
  /*  // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/


    public TrainFragment() {
        // Required empty public constructor
    }

  /*  /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainFragment.
     */
    /*// TODO: Rename and change types and number of parameters
    public static TrainFragment newInstance(String param1, String param2) {
        TrainFragment fragment = new TrainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        bar = (ProgressBar) view.findViewById(R.id.loading_layout);
        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
                return true;
            }

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

        Log.i("g", "FRAMGNET STARTED");
        bar.setVisibility(View.VISIBLE);
        new Loading().execute(url);
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
                    Document doc = Jsoup.connect(args[0]).userAgent(user).header("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3").get();
                    doc.charset(Charset.forName("UTF-8"));
                    Element el = doc.select("head").first();
                    Element el2 = doc.select(".page-content").first();
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
            web_view.loadDataWithBaseURL("http://m.sncf.com", element.toString(), "text/html", "gzip", "");
          //  web_view.loadUrl(url);

           // web_view.loadUrl(url);
            Log.i("END", "LOADING");
        }
    }

}
