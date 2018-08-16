package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Displays in a RecyclerView the different events
 * @author Quentin DE MUYNCK
 * @see RecyclerViewAdapterEvents
 * @see Event
 */

public class EventsFragment extends Fragment {
    private RecyclerViewAdapterEvents adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_fragment, parent, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.events);
        RecyclerView recycler = view.findViewById(R.id.recycler_events);

        Database db = new Database(getActivity());
        db.open();
        adapter = new RecyclerViewAdapterEvents(db.getEvents(), getActivity());
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        db.close();
    }

    @Override
    public void onResume() {
        adapter.actualise();
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}
