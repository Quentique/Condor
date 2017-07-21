package com.cvlcondorcet.condor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 18/07/2017.
 */

public class RecyclerViewAdapterPosts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<Post> list, filteredList;
    public Context ctx;

    public RecyclerViewAdapterPosts(Context ctx, List<Post> items, int item) {
        this.ctx = ctx;
        this.list = items;
        this.filteredList = items;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_layout, parent, false);
            return new RecyclerViewAdapterPosts.ViewHolder(v, ctx);
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
            ((RecyclerViewAdapterPosts.ViewHolder) holder).date.setText(post.getFormatedDate());
        ((ViewHolder) holder).categories.setText(post.getFormatedCategories());
            try {
                Picasso.with(ctx).load(post.getPicture()).into(((ViewHolder) holder).pic);
                ((RecyclerViewAdapterPosts.ViewHolder) holder).pic.setVisibility(View.VISIBLE);
            }catch (IllegalArgumentException e ) { e.printStackTrace(); ((RecyclerViewAdapterPosts.ViewHolder) holder).pic.setVisibility(View.GONE);}
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
                if (list != null) {
                    try {
                        filteredList.clear();
                    } catch (NullPointerException e) {
                        filteredList = new ArrayList<>();
                    }
                    final String qu = query.toLowerCase();
                    Log.i("e", "Filter : " + qu);
                    for (Post post : list) {
                        if (post.getName().toLowerCase().contains(qu)) {
                            filteredList.add(post);
                        }
                        Collections.sort(filteredList, Collections.<Post>reverseOrder());
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, content, date, categories;
        public ImageView pic, expand;
        public LinearLayout lay;
        private final Context context;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            name = (TextView) itemView.findViewById(R.id.post_title);
            content = (TextView) itemView.findViewById(R.id.post_desc);
            date = (TextView) itemView.findViewById(R.id.post_date);
            categories = (TextView) itemView.findViewById(R.id.post_categories);
            pic = (ImageView) itemView.findViewById(R.id.post_pic);
            lay = (LinearLayout) itemView.findViewById(R.id.post_lay);
            expand = (ImageView) itemView.findViewById(R.id.content_button);
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lay.getVisibility() == View.VISIBLE) {
                        lay.setVisibility(View.GONE);
                        expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand_more_black_24dp));
                        //expand.setImageDrawable(Reso));
                        //collapse(lay);
                    } else {
                        lay.setVisibility(View.VISIBLE);
                        expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand_less_black_24dp));
                       // expand(lay);
                        //expand.setImageDrawable(Resources.getSystem().getDrawable(R.drawable.ic_expand_less_black_24dp));
                    }
                }
            });
            itemView.setOnClickListener(this);
           // (CardView) itemView.findViewById(R.id.post_card).
            //secondaryText = (TextView) itemView.findViewById(R.id.beginning);
        }
        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Post post = filteredList.get(pos);
                if (post.getId() != "0") {
                    Intent intent = new Intent(context, PostViewerActivity.class);
                    intent.putExtra("id", post.getId());
                    context.startActivity(intent);
                }
            }
        }
    }
}
