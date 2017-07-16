package com.cvlcondorcet.condor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 16/07/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> list;
    private int itemsLayout;

    public RecyclerViewAdapter(List<String> items, int item) {
        this.list = items;
        this.itemsLayout = item;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemsLayout, parent, false);

        return new ViewHolder(v);
    }

    public int getItemCount() {
        return list.size();
    }

    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        String item = list.get(position);
        holder.primaryText.setText(item);
        holder.secondaryText.setText("no matter");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView primaryText, secondaryText;

        public ViewHolder(View itemView) {
            super(itemView);
            primaryText = (TextView) itemView.findViewById(R.id.profs);
            secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
    }
}
