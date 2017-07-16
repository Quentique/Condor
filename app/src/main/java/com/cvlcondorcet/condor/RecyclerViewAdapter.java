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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<TeachersAbsence> list;
    private int itemsLayout;
    private static int SINGLE = 0, SEVERAL = 1;

    public RecyclerViewAdapter(List<TeachersAbsence> items, int item) {
        this.list = items;
        this.itemsLayout = item;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemsLayout, parent, false);
        Log.i("DEBUG", "ViewHolder requested");
        return new ViewHolder(v);
    }

    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public void setData(List<TeachersAbsence> list) {
        this.list = list;
        notifyDataSetChanged();
        Log.i("Hello", "Data has changed");
    }

    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        String item = list.get(position).getName();
        Log.i("DEBUGGGGG", "ICH BIN DA");
        holder.primaryText.setText(item);
       // holder.secondaryText.setText("no matter");
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).)
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView primaryText, secondaryText;

        public ViewHolder(View itemView) {
            super(itemView);
            primaryText = (TextView) itemView.findViewById(R.id.profs);
            //secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
    }

}
