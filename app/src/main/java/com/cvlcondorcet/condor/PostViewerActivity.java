package com.cvlcondorcet.condor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

public class PostViewerActivity extends AppCompatActivity {

    private WebView view;
    private Database db;
    private TextView title, date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        db = new Database(this);
        db.open();
        view = (WebView) findViewById(R.id.post_view);
        title = (TextView) findViewById(R.id.post_display_title);
        date = (TextView) findViewById(R.id.post_date_display);
        String id = getIntent().getStringExtra("id");
        if (id != "0") {
            new Loading().execute("id", id);
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
        }
    }
}
