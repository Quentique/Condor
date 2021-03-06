package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * // NO MORE USED //
 * Displays a RecyclerView (for absences) and a search field (to filter absences by name)
 * @author Quentin DE MUYNCK
 * @see TeachersAbsence
 * @see TeachersLoader
 * @see RecyclerViewAdapterProfs
 */
public class TeachersFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<TeachersAbsence>>, SearchView.OnQueryTextListener {

    private RecyclerViewAdapterProfs adapter;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_teachers, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        // setTitle("Absences des profs");
        getActivity().setTitle(getString(R.string.teachers_absences));
        recycler = view.findViewById(R.id.recycler);
        adapter = new RecyclerViewAdapterProfs(getActivity(),null, R.layout.profs_one_day_layout);
        swipeContainer = view.findViewById(R.id.swipe_profs);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayout layout = view.findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.profs)).setText(R.string.teacher);
        ((TextView) layout.findViewById(R.id.date)).setText(R.string.date);
        ((TextView) layout.findViewById(R.id.morning)).setText(R.string.morning);
        ((TextView) layout.findViewById(R.id.afternoon)).setText(R.string.afternoon);
        getLoaderManager().initLoader(1, null, this);
    }
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_teachers);
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
    }*/

    /**
     * Refreshing the data if user wants it
     */
    private void refresh() {
        // getSupportLoaderManager().restartLoader(1, null, this);
        getLoaderManager().restartLoader(1, null, this);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public Loader<List<TeachersAbsence>> onCreateLoader(int id, Bundle args) {
        return new TeachersLoader(getActivity());
    }

    /**
     * Transfers data to adapter after load finished
     * @param loader    the loader that has loaded the data
     * @param data  the awaited data
     * @see RecyclerViewAdapterProfs#setData(List)
     */
    @Override
    public void onLoadFinished(Loader<List<TeachersAbsence>> loader, List<TeachersAbsence> data) {
        adapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<TeachersAbsence>> loader) {
        adapter.setData(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_profs, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        @SuppressWarnings("deprecation") final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    /**
     * Filters absences by name
     * @param query the user entry
     * @return  True
     * @see RecyclerViewAdapterProfs#filter(String)
     */
    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        adapter.filter(query);
        recycler.scrollToPosition(0);
        return true;
    }
}
