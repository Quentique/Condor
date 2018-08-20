package com.cvlcondorcet.condor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;
import static android.view.View.GONE;

/**
 *  Activity that displays the content of a post. Determines whether post is from RSS
 *  (therefore should be downloaded from internet) or from {@link Database DB} (which must be
 *  retrieved from db).
 *
 *  @author Quentin DE MUYNCK
 */
public class PostViewerActivity extends AppCompatActivity {

    private WebView view;
    private Database db;
    private TextView title, date, cat;
    private ProgressBar progress;
    private String id;

    /**
     * Sets up activity, starts loading post.
     * @param savedInstanceState    old state of activity
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        db = new Database(this);
        db.open();
        view = findViewById(R.id.post_view);

        title = findViewById(R.id.post_display_title);
        date = findViewById(R.id.post_date_display);
        cat = findViewById(R.id.post_display_cat);
        Toolbar bar = findViewById(R.id.toolbar_viewer_post);
        progress = findViewById(R.id.loading_layout);
        progress.setVisibility(View.VISIBLE);
        view.getSettings().setSupportZoom(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.getSettings().setDisplayZoomControls(false);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
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
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError err) {
                if (err.getPrimaryError() == SslError.SSL_EXPIRED) {
                    handler.cancel();
                } else {
                    handler.proceed();
                }
            }
        });
        setSupportActionBar(bar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {}

        id = getIntent().getStringExtra("id");
        if (!id.equals("0")) {
            if (getIntent().getExtras().containsKey("link")) {
                view.getSettings().setDefaultFontSize(10);
                view.getSettings().setTextZoom(100);

                view.loadUrl(getIntent().getStringExtra("link"));

                title.setVisibility(GONE);
                date.setVisibility(GONE);
                cat.setVisibility(GONE);
                setTitle(view.getTitle());
            } else {
                new Loading().execute("id", id);
                ArrayList<Integer> newArt = Database.parsePrefNot("posts", this);
                newArt.remove(Integer.valueOf(id));
                Database.updatePrefValue("posts", newArt, this);
            }
        } else {
            new LoadingWeb().execute(getIntent().getStringExtra("link"));
        }
    }


    /**
     * Comes back to main activity (when home button pressed).
     * @param item  Item clicked by menu (home only)
     * @return  Nothing important
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent relaunch = new Intent(this, MainActivity.class);
            relaunch.putExtra("fragment","posts");
            NavUtils.navigateUpTo(this, relaunch);
            return true;
        } else if (item.getItemId() == R.id.share_button) {
            Intent intent = new Intent(ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(EXTRA_SUBJECT, "Condor");
            if (id.equals("0")) {
                intent.putExtra(EXTRA_TEXT, getString(R.string.look_post_out)+getIntent().getStringExtra("link"));
            } else {
                intent.putExtra(EXTRA_TEXT, getString(R.string.look_posts_condor) + id);
            }
            startActivity(Intent.createChooser(intent, "Choose one"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    /**
     * Loads post from web (high-school website) (comes from RSS feed).
     * Uses the supplied stylesheet.
     */
    @SuppressLint("StaticFieldLeak")
    private class LoadingWeb extends AsyncTask<String, Void, Void> {
        String toDisplay = "";
        protected Void doInBackground(String... args) {
            try {
                Document doc = Jsoup.connect(args[0]).postDataCharset("UTF-8").get();
                toDisplay = "<link rel=\"stylesheet\" href=\"style.css\"/>";
                toDisplay += doc.select(".post").first().html();

            } catch (IOException e) { Crashlytics.logException(e); }
            return null;
        }

        protected void onPostExecute(Void nothing) {
            view.loadDataWithBaseURL("file:///android_asset/", toDisplay, "text/html", "utf-8", "");
            title.setVisibility(GONE);
            date.setVisibility(GONE);
            cat.setVisibility(GONE);
        }
    }

    /**
     * Gets post from database.
     * @see Database#getPost(String)
     * @see Post
     */
    @SuppressLint("StaticFieldLeak")
    private class Loading extends AsyncTask<String, Void, Void> {
        String type;
        Post post;
        protected Void doInBackground(String... args) {
            type = args[0];
            if (type.equals("id")) {
                post = db.getPost(args[1]);
            }

            return null;
        }

        protected void onPostExecute(Void nothing) {
            if (post != null) {
                view.loadDataWithBaseURL("file:///android_asset/", "<link rel=\"stylesheet\" href=\"style2.css\"/>" + post.getContent(), "text/html", "utf-8", "");
                setTitle(post.getName());
                title.setText(post.getName());
                date.setText(post.getFormatedDate());
                cat.setText(post.getFormatedCategories());
                db.close();
            } else {
                db.close();
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.putExtra("fragment", "posts");
                startActivity(intent);
                finish();
            }
        }
    }
}
