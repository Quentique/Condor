package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Quentin DE MUYNCK on 20/09/2017.
 */

public class EventsFragment extends Fragment {

    private RecyclerView recycler;
    private RecyclerViewAdapterEvents adapter;
    private Database db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_fragment, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.events);
        recycler = view.findViewById(R.id.recycler_events);

        db= new Database(getActivity());
        db.open();

        adapter = new RecyclerViewAdapterEvents(db.getEvents(), getActivity());
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i("START", "EVENTS FRAGMENT LOADED");
        db.close();
    }
}
