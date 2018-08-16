package com.cvlcondorcet.condor;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.List;

import static android.view.View.GONE;

/**
 * Displays a RecyclerView (showing posts), a spinner (to select categories) and a search field (to filter posts).
 * @author Quentin DE MUYNCK
 * @see RecyclerViewAdapterPosts
 * @see MultiSelectionSpinner
 * @see PostsLoader
 */
public class PostsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Post>>, SearchView.OnQueryTextListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    private RecyclerView recycler;
    private RecyclerViewAdapterPosts adapter;
    private MultiSelectionSpinner spinner;
    private ProgressBar progress;
    private android.support.v7.app.ActionBar bar;
    private Task loader;
    private String query;
    private RelativeLayout lay;
    private boolean rssAllowed;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        lay = (RelativeLayout) inflater.inflate(R.layout.multispinner, null);
        spinner = lay.findViewById(R.id.spinner_post);
        return inflater.inflate(R.layout.fragment_posts, parent, false);
    }
    // Pattern for RSS date "EEE, d MMM yyyy HH:mm:ss Z"

    /**
     * Sets view up, starts loading posts.
     * @param view  view
     * @param savedInstanceState oldState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.news);
        progress = view.findViewById(R.id.loading_layout);
        progress.setVisibility(View.VISIBLE);
        recycler = view.findViewById(R.id.recycler_posts);
        adapter = new RecyclerViewAdapterPosts(getActivity(), null, R.layout.posts_layout);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setHasFixedSize(true);
        loader = new Task();
        loader.execute();
        spinner.setListener(this);
        rssAllowed = MainActivity.default_preferences.getBoolean("rss_display", true);

        getLoaderManager().initLoader(2, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bar = ((MainActivity) getActivity()).getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setCustomView(lay);
        adapter.actualise();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);
    }

    /**
     * Create a loader and starts it to retrieve posts.
     * @param id    the id of the loader
     * @param args  bundle of arguments (not used)
     * @return  a PostLoader object
     * @see PostsLoader
     */
    @Override
    public Loader<List<Post>> onCreateLoader(int id, Bundle args) {
        return new PostsLoader(getActivity(), rssAllowed);
    }

    /**
     * Gets the data and sets them to the RecyclerViewAdapter
     * @param loader    the loader that retrieved the data
     * @param data  the awaited data
     * @see RecyclerViewAdapterPosts#setData(List)
     */
    @Override
    public void onLoadFinished(Loader<List<Post>> loader, List<Post> data) {
        adapter.setData(data);
        progress.setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Post>> loader) {
        adapter.setData(null);
    }

    /**
     * Sets up the menu & search field.
     * @param menu  menu
     * @param inflater  inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_profs, menu);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                query = "";
                return false;
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Event listeners to search field text changes, gets query and filters the RecyclerView according to it
     * @param query the text query
     * @return  False
     * @see RecyclerViewAdapterPosts#filter(String)
     */
    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        this.query = query;
        recycler.getRecycledViewPool().clear();
        adapter.filter(query);
        recycler.scrollToPosition(0);
        return false;
    }
    @Override
    public void selectedIndices(List<Integer> indices) {
    }

    /**
     * Interface implementation, gets the selected categories and filter the RecyclerView
     * @param strings   the selected categories
     * @see RecyclerViewAdapterPosts#filterByCategories(List, String)
     * @see MultiSelectionSpinner#getSelectedStrings()
     */
    @Override
    public void selectedStrings(List<String> strings) {
        recycler.getRecycledViewPool().clear();
        adapter.filterByCategories(strings, query);
        recycler.scrollToPosition(0);
    }

    @Override
    public void onDetach() {
        try {
            super.onDetach();
            spinner = null;
            bar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE | android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);
            loader.cancel(true);
        } catch (NullPointerException ignored) {}
    }

    /**
     * AsyncTask to get categories and displays them to the spinner
     * @author Quentin DE MUYNCK
     * @see Database#getCategories()
     */
    private class Task extends AsyncTask<Void, Void, Void> {
        List<String> results;

        /**
         * Out of the UI Thread
         * @param args not used
         * @return  null
         */
        @Override
        protected Void doInBackground(Void... args) {
            Database db = new Database(getActivity());
            db.open();
            results = db.getCategories();
            db.close();
            results.add(0, getString(R.string.all_category));
            if (rssAllowed)
                results.add("RSS");
            return null;
        }

        /**
         * On the UI Thread
         * @param nothing as the name, nothing
         */
        @Override
        protected void onPostExecute(Void nothing) {
            spinner.setItems(results);
            spinner.setSelection(0);
        }

    }
}
