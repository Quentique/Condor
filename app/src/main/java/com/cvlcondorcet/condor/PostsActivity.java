package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class PostsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Post>>, SearchView.OnQueryTextListener {

    private RecyclerView recycler;
    private RecyclerViewAdapterPosts adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        recycler = (RecyclerView) findViewById(R.id.recycler_posts);
        adapter = new RecyclerViewAdapterPosts(this, null, R.layout.posts_layout);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(2, null, this);
    }

    @Override
    public Loader<List<Post>> onCreateLoader(int id, Bundle args) {
        return new PostsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Post>> loader, List<Post> data) {
        adapter.setData(data);
        Log.i("HELLO", "LOAD FINISHED");
    }

    @Override
    public void onLoaderReset(Loader<List<Post>> loader) {
        adapter.setData(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_profs, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        Log.i("e", "Query");
        adapter.filter(query);
        recycler.scrollToPosition(0);
        return true;
    }
}
