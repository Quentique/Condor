package com.cvlcondorcet.condor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter for RecyclerView of Post
 * @author Quentin DE MUYNCK
 * @see PostsLoader
 * @see PostsFragment
 * @see Post
 */

class RecyclerViewAdapterPosts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Post> list, filteredList, catList;
    private ArrayList<Integer> newArt;
    private final Context ctx;
    private final Animation anim;

    public RecyclerViewAdapterPosts(Context ctx, List<Post> items, int item) {
        this.ctx = ctx;
        this.list = items;
        this.filteredList = items;
        this.anim = AnimationUtils.loadAnimation(ctx, R.anim.blink);
        newArt = Database.parsePrefNot("posts", ctx);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_layout, parent, false);
        return new RecyclerViewAdapterPosts.ViewHolder(v, ctx);
    }

    public int getItemCount() {
        return (filteredList != null) ? filteredList.size() : 0;
    }

    public void actualise() { newArt = Database.parsePrefNot("posts", ctx); }

    /**
     * Transfers data to object
     * @param list  data
     * @see PostsFragment#onLoadFinished(Loader, List)
     * @see RecyclerViewAdapterPosts#filterByCategories(List, String)
     */
    public void setData(List<Post> list) {
        this.list = list;
        filterByCategories(null, "");
    }

    /**
     * Displays the information of a post into the view.
     * @param holder    the holder
     * @param position  the position in the recycler
     * @see RecyclerViewAdapterPosts.ViewHolder
     * @see Post
     */
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post post = filteredList.get(position);
       // Log.i("DEBUGGGGG", "ICH BIN DA");
        RecyclerViewAdapterPosts.ViewHolder viewHolder = ((RecyclerViewAdapterPosts.ViewHolder) holder);
        if (newArt.contains(Integer.valueOf(post.getId()))) {
            viewHolder.name.setTypeface(null, Typeface.BOLD);
            viewHolder.name.setAnimation(anim);
            viewHolder.button.setVisibility(View.VISIBLE);
            Log.i("POSTS", "WORKED");
        } else {
            viewHolder.name.setTypeface(null, Typeface.NORMAL);
            viewHolder.name.setAnimation(null);
            viewHolder.button.setVisibility(View.GONE);
            Log.i("POSTS", "DIDN't WORKED");
        }
        Log.i("POSTS", "ID:"+post.getId());
        viewHolder.name.setText(Jsoup.parse(post.getName()).text());
        Log.i("GT", post.getName());
        viewHolder.content.setText(Jsoup.parse(post.getContent()).text());
        viewHolder.date.setText(post.getFormatedDate());
        viewHolder.categories.setText(post.getFormatedCategories());
        try {
            Picasso.with(ctx).load(post.getPicture()).into(viewHolder.pic);
            viewHolder.pic.setVisibility(View.VISIBLE);
        }catch (IllegalArgumentException e ) { viewHolder.pic.setVisibility(View.GONE);}
    }

    /**
     * Filters the list (of posts) from a given query, then sort them by descending data
     * @param query the query
     * @see PostsFragment#onQueryTextChange(String)
     * @see Post#compareTo(Post)
     */
    public void filter(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (catList != null) {
                    try {
                        filteredList.clear();
                    } catch (NullPointerException e) {
                        filteredList = new ArrayList<>();
                    }
                    String qu;
                    try {
                        qu = query.toLowerCase();

                        for (Post post : catList) {
                            if (post.getName().toLowerCase().contains(qu)) {
                                filteredList.add(post);
                            }
                        }
                    } catch (NullPointerException e) { filteredList = new ArrayList<>(); filteredList.addAll(catList); }
                    try {
                        Collections.sort(filteredList, Collections.<Post>reverseOrder());
                    } catch (NullPointerException ignored) {}
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

    /**
     * Filters the list (of posts) by the given categories, then filter by the old query (double-entry filter)
     * @param array the selected categories
     * @param queryy    the old query
     * @see RecyclerViewAdapterPosts#filter(String)
     * @see PostsFragment#selectedStrings(List)
     */
    public void filterByCategories(final List<String> array, final String queryy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (array != null && !array.get(0).equals(ctx.getResources().getString(R.string.all_category))) {
                        List<Post> copy;
                        copy = new ArrayList<>();
                        copy.addAll(list);
                        try {
                            catList.clear();
                        } catch (NullPointerException e) {
                            catList = new ArrayList<>();
                        }
                        for (int i = 0; i < array.size(); i++) {
                            for (int j = 0; j < copy.size(); j++) {
                                Post post = copy.get(j);
                                if (post.getCategories().contains(array.get(i))) {
                                    catList.add(post);
                                }
                            }
                            copy.removeAll(catList);
                        }
                    } else {
                        catList = new ArrayList<>();
                        catList.addAll(list);
                    }
                    filter(queryy);
                } catch (Exception e ) {e.printStackTrace();}

            }
        }).start();
    }

    /**
     * The View that displays one post in the RecyclerView.
     * @author Quentin DE MUYNCK
     * @see RecyclerViewAdapterPosts#onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView name;
        final TextView content;
        final TextView date;
        final TextView categories;
        final ImageView pic;
        final ImageView expand;
        final LinearLayout lay;
        final Button button;
        private final Context context;

        ViewHolder(View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            name = itemView.findViewById(R.id.post_title);
            content = itemView.findViewById(R.id.post_desc);
            date = itemView.findViewById(R.id.post_date);
            categories = itemView.findViewById(R.id.post_categories);
            pic = itemView.findViewById(R.id.post_pic);
            lay = itemView.findViewById(R.id.post_lay);
            button = itemView.findViewById(R.id.viewed_button);
            expand = itemView.findViewById(R.id.content_button);
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lay.getVisibility() == View.VISIBLE) {
                        lay.setVisibility(View.GONE);
                        expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand_more_black_24dp));
                    } else {
                        lay.setVisibility(View.VISIBLE);
                        expand.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_expand_less_black_24dp));
                    }
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Post post = filteredList.get(pos);
                        newArt.remove(Integer.valueOf(post.getId()));
                        Database.updatePrefValue("posts", newArt, context);
                        view.setVisibility(View.GONE);
                        name.setAnimation(null);
                        name.setTypeface(null, Typeface.NORMAL);
                        Intent restart = new Intent(context, MainActivity.class);
                        restart.putExtra("fragment", "nav");
                        context.startActivity(restart);
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        /**
         * Gets the full article, starts an intent to display it.
         * @param view  the clicked view
         * @see PostViewerActivity
         */
        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Post post = filteredList.get(pos);
                Intent intent = new Intent(context, PostViewerActivity.class);
                if (post.getContent().startsWith("http")) {
                    post.setLink(post.getContent());
                    intent.putExtra("link", post.getLink());
                }
                intent.putExtra("id", post.getId());
                if (post.getId().equals("0")) {
                    intent.putExtra("link", post.getLink());
                }
                Log.i("POSTS", "ABOUT TO EXECUTE");
                context.startActivity(intent);

                Log.i("POSTS", "EXECUTED");
            }
        }
    }
}
