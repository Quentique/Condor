package com.cvlcondorcet.condor;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

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
 * Loader class to retrieve posts from Database and Internet
 * @author Quentin DE MUYNCK
 */

class PostsLoader extends AsyncTaskLoader<List<Post>> {
    private List<Post> list;
    private final Database db;
    private final boolean rssAllowed;
    private final boolean connection;

    public PostsLoader(Context ctx, boolean rssAllowed) {
        super(ctx);
        db = new Database(ctx);
        this.rssAllowed = rssAllowed;
        this.connection = MainActivity.allowConnect(ctx);
    }

    /**
     * Makes requests : RSS if authorised and database
     * @return  the posts list
     * @see Database#getPosts()
     */
    @Override
    public List<Post> loadInBackground() {
        db.open();
        List<Post> data = db.getPosts();
        db.close();
        if (rssAllowed && connection) {
            List<Post> rssFeed = new ArrayList<>();
            try {
                try {
                Document doc = Jsoup.connect(Sync.rssURL).postDataCharset("UTF-8").get();
                doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml).prettyPrint(true);
                Elements rss_articles = doc.select("item");
                for (Element element : rss_articles) {
                    Post post = new Post("0",
                            element.select("title").first().text(),
                            Parser.unescapeEntities(Jsoup.clean(element.select("description").first().text(), Whitelist.simpleText()), false),
                            "",
                            Post.formatDate(element.select("pubDate").first().text(), "EEE, d MMM yyyy HH:mm:ss Z", "yyyy-MM-dd hh:mm:ss"),
                            "[\"RSS\"]");
                    post.setLink(element.select("link").first().text());
                    rssFeed.add(post);
                }

                }catch (IllegalArgumentException ignored) { }
                data.addAll(rssFeed);
            } catch (IOException ignored) { }
        }
        return data;
    }

    @Override
    public void deliverResult(List<Post> data){
        list = data;
        if (isStarted()) { super.deliverResult(data); }
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
