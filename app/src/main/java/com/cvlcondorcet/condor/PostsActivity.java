package com.cvlcondorcet.condor;

import android.app.ActionBar;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PostsActivity extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Post>>, SearchView.OnQueryTextListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    private RecyclerView recycler;
    private RecyclerViewAdapterPosts adapter;
    private MultiSelectionSpinner spinner;
    private Database db;
    private android.support.v7.app.ActionBar bar;

  /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        recycler = (RecyclerView) findViewById(R.id.recycler_posts);
        adapter = new RecyclerViewAdapterPosts(this, null, R.layout.posts_layout);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        spinner = (MultiSelectionSpinner) findViewById(R.id.spinner_post);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    db = new Database(getApplicationContext());
                    db.open();
                    spinner.setItems(db.getCategories());
                    db.close();
                    spinner.setSelection(0);
                } catch (ArrayIndexOutOfBoundsException e ) { }
                catch (SQLException e) {}
            }
        }).start();
        spinner.setListener(this);

        getSupportLoaderManager().initLoader(2, null, this);
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Defines the xml file for the fragment
        spinner = (MultiSelectionSpinner) inflater.inflate(R.layout.multispinner, null);
        return inflater.inflate(R.layout.activity_posts, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bar = ((MainActivity) getActivity()).getSupportActionBar();
        recycler = (RecyclerView) view.findViewById(R.id.recycler_posts);
        adapter = new RecyclerViewAdapterPosts(getActivity(), null, R.layout.posts_layout);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setHasFixedSize(true);
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setCustomView(spinner);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    db = new Database(getActivity());
                    db.open();
                    spinner.setItems(db.getCategories());
                    db.close();
                    spinner.setSelection(0);
                } catch (ArrayIndexOutOfBoundsException e ) { }
                catch (SQLException e) {}
            }
        }).start();
        spinner.setListener(this);

        getLoaderManager().initLoader(2, null, this);
    }
    @Override
    public Loader<List<Post>> onCreateLoader(int id, Bundle args) {
        return new PostsLoader(getActivity());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_profs, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
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
    @Override
    public void selectedIndices(List<Integer> indices) {
        Log.i("hey", "hello");
    }

    @Override
    public void selectedStrings(List<String> strings) {
        //Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
        adapter.filterByCategories(strings);
        recycler.scrollToPosition(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        spinner = null;
        bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);
    }
}
