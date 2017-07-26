package com.cvlcondorcet.condor;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import android.widget.RelativeLayout;

import java.util.List;

public class PostsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Post>>, SearchView.OnQueryTextListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    private RecyclerView recycler;
    private RecyclerViewAdapterPosts adapter;
    private MultiSelectionSpinner spinner;
    private Database db;
    private android.support.v7.app.ActionBar bar;
    private Task loader;
    private String query;
    private RelativeLayout lay;
    private SearchView search;
    private MenuItem item;

  /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_posts);
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
        //setRetainInstance(true);
        // Defines the xml file for the fragment
        lay = (RelativeLayout) inflater.inflate(R.layout.multispinner, null);
        spinner = lay.findViewById(R.id.spinner_post);
        return inflater.inflate(R.layout.fragment_posts, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.news);
        bar = ((MainActivity) getActivity()).getSupportActionBar();
        recycler = (RecyclerView) view.findViewById(R.id.recycler_posts);
        adapter = new RecyclerViewAdapterPosts(getActivity(), null, R.layout.posts_layout);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setHasFixedSize(true);
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setCustomView(lay);
        loader = new Task();
        loader.execute();
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
        search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        item = menu.findItem(R.id.action_search);
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                query = "";
                return false;
            }
        });
        /*item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                query = search.getQuery().toString();
                return true;
            }
        });*/
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        this.query = query;
        Log.i("e", "Query");
        recycler.getRecycledViewPool().clear();
        adapter.filter(query);
        recycler.scrollToPosition(0);
        return false;
    }
    @Override
    public void selectedIndices(List<Integer> indices) {
        //Log.i("hey", "hello");
    }

    @Override
    public void selectedStrings(List<String> strings) {
        //Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
        recycler.getRecycledViewPool().clear();
       // Log.i("EEEE", query);
        adapter.filterByCategories(strings, query);
        recycler.scrollToPosition(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        spinner = null;
        bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);
        loader.cancel(true);

    }

    private class Task extends AsyncTask<Void, Void, Void> {
        List<String> results;
        @Override
        protected Void doInBackground(Void... args) {
            db = new Database(getActivity());
            db.open();
            results = db.getCategories();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            spinner.setItems(results);
            spinner.setSelection(0);
        }

    }
}
