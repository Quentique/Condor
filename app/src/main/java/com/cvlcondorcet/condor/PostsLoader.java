package com.cvlcondorcet.condor;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

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
