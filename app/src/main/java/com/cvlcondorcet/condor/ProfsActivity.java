package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProfsActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<List<TeachersAbsence>> {

    protected RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profs);
        setTitle("Absences des profs");
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        List<TeachersAbsence> teacher = new ArrayList<>();
        TeachersAbsence ab = new TeachersAbsence("Mme Georges", "2", "3");
        teacher.add(ab);
        adapter = new RecyclerViewAdapter(teacher, R.layout.profs_one_day_layout);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<List<TeachersAbsence>> onCreateLoader(int id, Bundle args) {
        return new TeachersLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<TeachersAbsence>> loader, List<TeachersAbsence> data) {
        Log.i("Hllo", "");
        adapter.setData(data);
        Log.i("HELLO", "LOAD FINISHED");
    }

    @Override
    public void onLoaderReset(Loader<List<TeachersAbsence>> loader) {
        adapter.setData(null);
    }
}
