package com.cvlcondorcet.condor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Shows an event (from {@link EventsFragment} or from {@link AlarmReceiver#onReceive(Context, Intent)} and displays its information
 * @author Quentin DE MUYNCK
 */

public class EventViewerActivity extends AppCompatActivity {

    private Database db;
    private TextView name, desc, when, where;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_viewer);

        name = (TextView) findViewById(R.id.name_event);
        desc = (TextView) findViewById(R.id.desc_event);
        when = (TextView) findViewById(R.id.when_event);
        where = (TextView) findViewById(R.id.where_event);
        image = (ImageView) findViewById(R.id.image_event);

        Toolbar toolbar = (Toolbar)  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.event));
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch( NullPointerException e) {}

        db = new Database(this);
        db.open();
        String id = getIntent().getStringExtra("id");
        Event event = db.getEvent(id);
        db.close();
        name.setText(Html.fromHtml(event.getName()));
        desc.setText(Html.fromHtml(event.getDesc()));
        where.setText(Html.fromHtml(event.getPlace()));
        String date;
        if (event.getDateBegin().equals(event.getDateEnd())) {
            date = getResources().getString(R.string.from_the) + event.getDateBegin() + " " + getResources().getString(R.string.from_single_day).toLowerCase() + event.getHourBegin() + getResources().getString(R.string.to_single_day) + event.getHourEnd();
        } else {
            date = getResources().getString(R.string.from) + event.getDateBegin() + getResources().getString(R.string.to) + event.getDateEnd();
        }
        when.setText(date);
        try {
            Picasso.with(this).load(event.getPicture()).into(image);
        } catch(IllegalArgumentException e) {}
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
