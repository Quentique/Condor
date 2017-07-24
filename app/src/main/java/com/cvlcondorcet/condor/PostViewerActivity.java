package com.cvlcondorcet.condor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webview, String url) {
                progress.setVisibility(View.GONE);
            }
        });
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String id = getIntent().getStringExtra("id");
        if (id != "0") {
            new Loading().execute("id", id);
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
