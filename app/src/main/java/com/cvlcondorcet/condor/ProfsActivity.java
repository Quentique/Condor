package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profs);
        setTitle("Absences des profs");
        List<String> items = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            // new item
            items.add("test " + i);
        }
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items, R.layout.profs_one_day_layout);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
