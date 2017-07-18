package com.cvlcondorcet.condor;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 18/07/2017.
 */

public class RecyclerViewAdapterPosts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Post> list, filteredList;
    public Context ctx;

    public RecyclerViewAdapterPosts(Context ctx, List<Post> items, int item) {
        this.ctx = ctx;
        this.list = items;
        this.filteredList = items;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_layout, parent, false);
            return new RecyclerViewAdapterPosts.ViewHolder(v);
    }

    public int getItemCount() {
        return (filteredList != null) ? filteredList.size() : 0;
    }

    public void setData(List<Post> list) {
        this.list = list;
        filter("");
        notifyDataSetChanged();
        Log.i("Hello", "Data has changed");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post post = filteredList.get(position);
        Log.i("DEBUGGGGG", "ICH BIN DA");
            ((RecyclerViewAdapterPosts.ViewHolder) holder).name.setText(post.getName());
            ((RecyclerViewAdapterPosts.ViewHolder) holder).content.setText(post.getContent());
        // holder.secondaryText.setText("no matter");
    }

   /* @Override
    public int getItemViewType(int position) {
        if (filteredList.get(position).getMultipleDays())
        {
            Log.i("DE", "SEVERAL");
            return SEVERAL;
        } else { Log.i("de", "SINGLE"); return SINGLE; }
    }*/

    public void filter(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    filteredList.clear();
                }catch (NullPointerException e ) { filteredList = new ArrayList<>(); }
                final String qu = query.toLowerCase();
                Log.i("e", "Filter : " + qu);
                for (Post post : list) {
                    if (post.getName().toLowerCase().contains(qu)) {
                        filteredList.add(post);
                    }
                }

                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, content;
        public ImageView pic;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.post_title);
            content = (TextView) itemView.findViewById(R.id.post_desc);
            pic = (ImageView) itemView.findViewById(R.id.post_pic);
            //secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
    }


}
