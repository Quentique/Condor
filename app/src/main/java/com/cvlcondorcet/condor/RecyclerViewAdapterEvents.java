package com.cvlcondorcet.condor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for RecyclerView of {@link Event}
 * @author Quentin DE MUYNCK
 * @see Event
 * @see EventsFragment
 * @see EventViewerActivity
 */
class RecyclerViewAdapterEvents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Event> events;
    private final Context ctx;
    private ArrayList<Integer> newEvents;
    private final Animation anim;

    public RecyclerViewAdapterEvents(List<Event> events, Context ctx) {
        this.events = events;
        this.ctx = ctx;
        this.newEvents = Database.parsePrefNot("events", ctx);
        anim = AnimationUtils.loadAnimation(ctx, R.anim.blink);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int view_type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_layout,parent, false);
        return new ViewHolder(v, ctx);
    }

    public int getItemCount() { return (events != null) ? events.size() : 0; }

    public void actualise() { newEvents = Database.parsePrefNot("events", ctx); }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Event event = events.get(position);
        ViewHolder viewHolder = ((ViewHolder) holder);
        if (newEvents.contains(Integer.valueOf(event.getId()))) {
            viewHolder.name.setTypeface(null, Typeface.BOLD);
             viewHolder.name.setAnimation(anim);
        } else {
           viewHolder.name.setTypeface(null, Typeface.NORMAL);
           viewHolder.name.setAnimation(null);
        }
        viewHolder.name.setText(Html.fromHtml(event.getName()));
        String date;        if (event.getDateBegin().equals(event.getDateEnd())) {
            date = ctx.getResources().getString(R.string.from_the) + event.getDateBegin() + " " + ctx.getResources().getString(R.string.from_single_day).toLowerCase() + event.getHourBegin() + ctx.getResources().getString(R.string.to_single_day) + event.getHourEnd();
        } else {
            date = ctx.getResources().getString(R.string.from) + event.getDateBegin() + ctx.getResources().getString(R.string.to) + event.getDateEnd();
        }
        viewHolder.date.setText(date);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView name, date;
        private final Context ctx;

        ViewHolder(View v, Context ctx) {
            super(v);
            name = v.findViewById(R.id.title_event);
            date = v.findViewById(R.id.date_info);
            v.setOnClickListener(this);
            this.ctx = ctx;
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Event e = events.get(pos);
                Intent intent = new Intent(ctx, EventViewerActivity.class);
                intent.putExtra("id", e.getId());
                ctx.startActivity(intent);
            }
        }
    }
}