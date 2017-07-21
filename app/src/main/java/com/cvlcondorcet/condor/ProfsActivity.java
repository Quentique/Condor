package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ProfsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<TeachersAbsence>>, SearchView.OnQueryTextListener {

    protected RecyclerViewAdapterProfs adapter;
    protected RecyclerView recycler;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profs);
        setTitle("Absences des profs");
        recycler = (RecyclerView) findViewById(R.id.recycler);
        adapter = new RecyclerViewAdapterProfs(this,null, R.layout.profs_one_day_layout);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_profs);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        LinearLayout layout = (LinearLayout) findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.profs)).setText("Professeur");
        ((TextView) layout.findViewById(R.id.date)).setText("Date");
        ((TextView) layout.findViewById(R.id.morning)).setText("Matin");
        ((TextView) layout.findViewById(R.id.afternoon)).setText("Aprem");
        getSupportLoaderManager().initLoader(1, null, this);
    }

    private void refresh() {
        getSupportLoaderManager().restartLoader(1, null, this);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public Loader<List<TeachersAbsence>> onCreateLoader(int id, Bundle args) {
        return new TeachersLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<TeachersAbsence>> loader, List<TeachersAbsence> data) {
        adapter.setData(data);
        Log.i("HELLO", "LOAD FINISHED");
    }

    @Override
    public void onLoaderReset(Loader<List<TeachersAbsence>> loader) {
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
