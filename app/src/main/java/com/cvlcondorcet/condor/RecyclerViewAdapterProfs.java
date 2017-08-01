package com.cvlcondorcet.condor;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 16/07/2017.
 */

class RecyclerViewAdapterProfs extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TeachersAbsence> list, filteredList;
    private int itemsLayout;
    private int SINGLE = 0, SEVERAL = 1, SINGLE_SPE = 2;
    private Context ctx;

    RecyclerViewAdapterProfs(Context ctx, List<TeachersAbsence> items, int item) {
        this.ctx = ctx;
        this.list = items;
        this.filteredList = items;
        this.itemsLayout = item;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        if (viewtype == SINGLE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profs_one_day_layout, parent, false);
            return new ViewHolder(v);
        } else if (viewtype == SEVERAL){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profs_several_days_layout, parent, false);
            return new ViewHolder2(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profs_one_day_spe_hours_layout, parent, false);
            Log.i("HELLO", "VIEWHOLDER3");
            return new ViewHolder3(v);
        }

    }

    public int getItemCount() {
        return (filteredList != null) ? filteredList.size() : 0;
    }

    public void setData(List<TeachersAbsence> list) {
        this.list = list;
        filter("");
        notifyDataSetChanged();
        //Log.i("Hello", String.valueOf(list.size()));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TeachersAbsence absence = filteredList.get(position);
        String result;
        switch (absence.getTitle()) {
            case "F":
                result = ctx.getString(R.string.female_title);
                break;
            case "M":
                result = ctx.getString(R.string.male_title);
                break;
            default:
                result = ctx.getString(R.string.neutral_title);
                break;
        }
        Log.i("DEBUGGGGG", "ICH BIN DA");
        if (absence.getMultipleDays()) {
            Log.i("EEE", absence.getTitle());
            ((ViewHolder2) holder).name.setText(result + " " + absence.getName());
            ((ViewHolder2) holder).date.setText(ctx.getString(R.string.from) + absence.getBeginning() + ctx.getString(R.string.to) + absence.getEnd());
        } else if (absence.getDate() == null)
        {
            ((ViewHolder) holder).name.setText(result + " " + absence.getName());
            ((ViewHolder) holder).date.setText(absence.getBeginning());
            ((ViewHolder) holder).morning.setText((absence.getMorning()) ? "X" : "");
            ((ViewHolder) holder).afternoon.setText((absence.getAfternoon()) ? "X" : "");
        } else {
            ((ViewHolder3) holder).name.setText(result + " " + absence.getName());
            ((ViewHolder3) holder).date.setText(absence.getDate());
            ((ViewHolder3) holder).hours.setText(ctx.getString(R.string.from_single_day) + absence.getBeginning() + ctx.getString(R.string.to_single_day) + absence.getEnd());
        }
       // holder.secondaryText.setText("no matter");
    }

    @Override
    public int getItemViewType(int position) {
        if (filteredList.get(position).getMultipleDays())
        {
            Log.i("DE", "SEVERAL");
            return SEVERAL;
        } else if (filteredList.get(position).getDate() == null){ Log.i("de", "SINGLE"); return SINGLE; } else { Log.i("DE", "SINGLE SPE"); return SINGLE_SPE; }
    }

    void filter(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (list != null) {
                    try {
                        filteredList.clear();
                    } catch (NullPointerException e) {
                        filteredList = new ArrayList<>();
                    }
                    final String qu = query.toLowerCase();
                    Log.i("e", "Filter : " + qu);
                    for (TeachersAbsence absence : list) {
                        if (absence.getName().toLowerCase().contains(qu)) {
                            filteredList.add(absence);
                        }
                    }

                    ((Activity) ctx).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }


            }
        }).start();
    }


    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, morning, afternoon;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.profs);
            date = (TextView) itemView.findViewById(R.id.date);
            morning = (TextView) itemView.findViewById(R.id.morning);
            afternoon = (TextView) itemView.findViewById(R.id.afternoon);
            //secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
    }

    private static class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView name, date;

        ViewHolder2(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.prof_name1);
            date = (TextView) itemView.findViewById(R.id.date2);
            //secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
    }

    private static class ViewHolder3 extends RecyclerView.ViewHolder {
        TextView name, date, hours;

        ViewHolder3(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.profs);
            date = (TextView) itemView.findViewById(R.id.date);
            hours = (TextView) itemView.findViewById(R.id.weirdHours);
        }
    }

}
