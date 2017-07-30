package com.cvlcondorcet.condor;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 18/07/2017.
 */

public class PostsLoader extends AsyncTaskLoader<List<Post>> {
    private List<Post> list;
    private Database db;

    public PostsLoader(Context ctx) {
        super(ctx);
        db = new Database(ctx);
    }

    @Override
    public List<Post> loadInBackground() {
        db.open();
        List<Post> data = db.getPosts();
        db.close();
        String answer = "";
        List<Post> rssFeed = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(Sync.rssURL).postDataCharset("UTF-8").get();
            doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml).prettyPrint(true);
            Elements rss_articles = doc.select("item");
            for (Element element : rss_articles) {
                //Date date = Post.getDateObject(element.select("pubDate").first().text(), "EEE, d MMM yyyy HH:mm:ss Z");
                Post post = new Post("0",
                        element.select("title").first().text(),
                        Parser.unescapeEntities(Jsoup.clean(element.select("description").first().text(), Whitelist.simpleText()), false),
                        "",
                        Post.formatDate(element.select("pubDate").first().text(), "EEE, d MMM yyyy HH:mm:ss Z", "yyyy-MM-dd hh:mm:ss"),
                        "[\"RSS\"]");
                rssFeed.add(post);
                //Log.i("EEEE", element.select("description").first().html());
                Log.i("EEEE", Jsoup.clean(element.select("description").first().text(), Whitelist.none()));
               // Log.i("EEEE", element.select("description").first().data());


            }
            data.addAll(rssFeed);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("HELLO", "Background done2");
        return data;
    }

    @Override
    public void deliverResult(List<Post> data){
       /* if (isReset()) {
            releaseResources(data);
            return;
        }*/

        List<Post> oldData = list;
        list = data;

        if (isStarted()) { super.deliverResult(data); }
        // if (oldData != null & oldData != data) { releaseResources(oldData); }
    }

    @Override
    protected void onStartLoading() {
        if (list != null) {
            deliverResult(list);
        }

        if (takeContentChanged() || list == null ){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (list != null) { list = null; }
    }

    @Override
    public void onCanceled(List<Post> data) {
        super.onCanceled(data);
    }

}
