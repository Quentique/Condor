package com.cvlcondorcet.condor;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static android.view.View.GONE;

public class PostViewerActivity extends AppCompatActivity {

    private WebView view;
    private Database db;
    private TextView title, date;
    private Toolbar bar;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        db = new Database(this);
        db.open();
        view = (WebView) findViewById(R.id.post_view);

        title = (TextView) findViewById(R.id.post_display_title);
        date = (TextView) findViewById(R.id.post_date_display);
        bar = (Toolbar) findViewById(R.id.toolbar_viewer_post);
        progress = (ProgressBar) findViewById(R.id.loading_layout);
        progress.setVisibility(View.VISIBLE);
        view.getSettings().setSupportZoom(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
       // view.getSettings().setDefaultFontSize(30);
        view.getSettings().setTextZoom(250);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webview, String url) {
                progress.setVisibility(GONE);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browser);
                return true;
            }
        });
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String id = getIntent().getStringExtra("id");
        Log.i("ID", "'" + id +"'");
        if (!id.equals("0")) {
            new Loading().execute("id", id);
        } else {
            new LoadingWeb().execute(getIntent().getStringExtra("link"));
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent relaunch = new Intent(this, MainActivity.class);
            relaunch.putExtra("HELLO","test");
            startActivity(relaunch);
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadingWeb extends AsyncTask<String, Void, Void> {
        String toDisplay = "";
        protected Void doInBackground(String... args) {
            try {
                Document doc = Jsoup.connect(args[0]).postDataCharset("UTF-8").get();
               // toDisplay = doc.select(".post").first().html();
               /* Elements elements = new Elements();
                Element el = doc.select("head").first();
                Element el2 = doc.select(".post").first();
                elements.add(el);
                elements.add(el2);*/
               //toDisplay = doc.select("head").first().html();
                toDisplay = "<link rel=\"stylesheet\" href=\"style.css\"/>";
                toDisplay += doc.select(".post").first().html();

            } catch (IOException e) {}
            return null;
        }

        protected void onPostExecute(Void nothing) {
            view.loadDataWithBaseURL("file:///android_asset/", toDisplay, "text/html", "utf-8", "");
            title.setVisibility(GONE);
            date.setVisibility(GONE);
        }
    }

    private class Loading extends AsyncTask<String, Void, Void> {
        String type;
        Post post;
        protected Void doInBackground(String... args) {
            type = args[0];
            if (type == "id") {
                post = db.getPost(args[1]);
            }

            return null;
        }

        protected void onPostExecute(Void nothing) {
            view.loadData(post.getContent(), "text/html", "utf-8");
            setTitle(post.getName());
            title.setText(post.getName());
            date.setText(post.getFormatedDate());
            db.close();
            //progress.setVisibility(GONE);
        }
    }
}
