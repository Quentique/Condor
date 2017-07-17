package com.cvlcondorcet.condor;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 16/07/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TeachersAbsence> list;
    private int itemsLayout;
    private int SINGLE = 0, SEVERAL = 1;

    public RecyclerViewAdapter(List<TeachersAbsence> items, int item) {
        this.list = items;
        this.itemsLayout = item;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        if (viewtype == SINGLE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profs_one_day_layout, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profs_several_days_layout, parent, false);
            return new ViewHolder2(v);
        }

    }

    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public void setData(List<TeachersAbsence> list) {
        this.list = list;
        notifyDataSetChanged();
        Log.i("Hello", "Data has changed");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TeachersAbsence absence = list.get(position);
        Log.i("DEBUGGGGG", "ICH BIN DA");
        if (absence.getMultipleDays()) {
            ((ViewHolder2) holder).name.setText(absence.getName());
            ((ViewHolder2) holder).date.setText("Du " + absence.getBeginning() + " au " + absence.getEnd());
        } else
        {
            ((ViewHolder) holder).name.setText(absence.getName());
            ((ViewHolder) holder).date.setText(absence.getBeginning());
            ((ViewHolder) holder).morning.setText((absence.getMorning()) ? "X" : "");
            ((ViewHolder) holder).afternoon.setText((absence.getAfternoon()) ? "X" : "");
        }
       // holder.secondaryText.setText("no matter");
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getMultipleDays())
        {
            Log.i("DE", "SEVERAL");
            return SEVERAL;
        } else { Log.i("de", "SINGLE"); return SINGLE; }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date, morning, afternoon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.profs);
            date = (TextView) itemView.findViewById(R.id.date);
            morning = (TextView) itemView.findViewById(R.id.morning);
            afternoon = (TextView) itemView.findViewById(R.id.afternoon);
            //secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView name, date;

        public ViewHolder2(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.prof_name1);
            date = (TextView) itemView.findViewById(R.id.date2);
            //secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
    }

}
