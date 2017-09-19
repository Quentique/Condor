package com.cvlcondorcet.condor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class RecyclerViewAdapterEvents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> events;

    public RecyclerViewAdapterEvents(List<Event> events) {
        this.events = events;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int view_type) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_layout,parent, false);
        return new ViewHolder(v);
    }

    public int getItemCount() { return (events != null) ? events.size() : 0; }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Event event = events.get(position);
        ((ViewHolder) holder).name.setText(event.getName());
        ((ViewHolder) holder).date.setText("From " + event.getDateBegin() + " " + event.getHourBegin() + " to " + event.getDateEnd() + " " + event.getHourEnd());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, date;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.title_event);
            date = v.findViewById(R.id.date_info);
        }
    }
}