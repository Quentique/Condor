package com.cvlcondorcet.condor;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FilterQueryProvider;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.view.View.GONE;

/**
 * Displays a {@link PDFView} with the different plans of the high-school ; integrates a SearchView
 * @author Quentin DE MUYNCK
 */

public class MapsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private String query;
    private RelativeLayout layout;
    private SimpleCursorAdapter adapter;
    private LinkedHashMap<String, String> gl, pl, in, ge;
    private LinkedHashMap<String, List<String>> total;
    private List<String> title;
    private AlertDialog dialog;
    private PDFView pdf;
    private Database db;
    private TextView name, desc, place;
    private double initialX, initialY, x, y;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_maps, menu);
        final MenuItem item = menu.findItem(R.id.action_search_maps);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        SearchView.SearchAutoComplete searchAutoCompleteTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoCompleteTextView.setThreshold(1);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                query = "";
                return false;
            }
        });
        db = new Database(getActivity());
        db.open();
        adapter = (SimpleCursorAdapter)db.getSuggestions();
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                Cursor cursor = db.getQuery(DBOpenHelper.Maps.TABLE_NAME, new String[]{DBOpenHelper.Maps.COLUMN_ID, DBOpenHelper.Maps.COLUMN_DPNAME, DBOpenHelper.Maps.COLUMN_NAME},
                        DBOpenHelper.Maps.COLUMN_DPNAME +" LIKE '%"+query+"%'" );
               // Log.i("COUNT", String.valueOf(cursor.getCount()));
              //  Log.i("QUERY", query);
                return cursor;
            }
        });
        searchView.setSuggestionsAdapter(adapter);
        searchView.setIconifiedByDefault(true);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
               // Log.i("TESTT", String.valueOf(adapter.getItemId(position)));
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
               // Log.i("TESTT", String.valueOf(adapter.getItemId(position)));

                searchView.clearFocus();
                Cursor cursor =(Cursor)adapter.getItem(position);
               // Log.i("TESTT", "C" +cursor.getString(cursor.getColumnIndex(DBOpenHelper.Maps.COLUMN_ID)));
               // Log.i("TESTT", adapter.getCursor().getString(position));
                loadPdf(adapter.getItemId(position));
                searchView.setQuery(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Maps.COLUMN_DPNAME)), true);

                cursor.close();
                return false;
            }
        });

        menu.findItem(R.id.menu_select).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dialog.show();
                return false;
            }
        });

        String test;
        try {
            test = getArguments().getString("place");
        } catch (Exception e) { test = ""; }

        if (test.equals("")) {
            loadPdf("GEN.pdf");
            pdf.zoomWithAnimation(3.0f);
        } else {
            if (test.endsWith(".pdf")) {
                loadPdf(test);
            } else {
                Log.i("TEST", test);
                loadPdf(db.getPlaceId(test));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (db != null) {
            db.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

    public void onStop() {
        super.onStop();
        if (db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        getActivity().setTitle(R.string.maps);
        gl=new LinkedHashMap<>();
        String[] grandl_lycee = getResources().getStringArray(R.array.gl);
        for (int i = 0 ; i<grandl_lycee.length ; i++) {
            gl.put(grandl_lycee[i], String.valueOf(i)+"EGL.pdf");
           // Log.i("TEST", grandl_lycee[i]);
        }
        pl=new LinkedHashMap<>();
        String[] petti_lycee = getResources().getStringArray(R.array.pl);
        for (int i = 0 ; i<petti_lycee.length ; i++) {
            pl.put(petti_lycee[i], String.valueOf(i)+"EPL.pdf");
        }
        in=new LinkedHashMap<>();
        String[] internat = getResources().getStringArray(R.array.in);
        for (int i = 0 ; i<internat.length ; i++) {
            in.put(internat[i], String.valueOf(i)+"EIN.pdf");
        }
        ge = new LinkedHashMap<>();
        String[] general = getResources().getStringArray(R.array.maps_list);
        for (int i = 0 ; i<general.length ; i++) {
            ge.put(general[i], "GEN.pdf");
        }
        total = new LinkedHashMap<>();
        total.put("GENERAL", new ArrayList<String>(ge.keySet()));
        total.put("GRAND LYCEE", new ArrayList<String>(gl.keySet()));
        total.put("PETIT LYCEE", new ArrayList<String>(pl.keySet()));
        total.put("INTERNAT", new ArrayList<String>(in.keySet()));
        title  = new ArrayList<>(total.keySet());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.plan_choice);

        ExpandableListView list = new ExpandableListView(getActivity());
        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter(getActivity(),
                title,
                total);
        list.setAdapter(adapter);
        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                switch (title.get(i)) {
                    case "GRAND LYCEE":
                        loadPdf(gl.get(gl.keySet().toArray()[i1]));
                        break;
                    case "PETIT LYCEE":
                        loadPdf(pl.get(pl.keySet().toArray()[i1]));
                        break;
                    case "INTERNAT":
                        loadPdf(in.get(in.keySet().toArray()[i1]));
                        break;
                    case "GENERAL":
                        loadPdf(ge.get(ge.keySet().toArray()[i1]));
                        break;
                }
                dialog.dismiss();
                return false;
            }
        });

        builder.setView(list);
        dialog = builder.create();

        pdf = view.findViewById(R.id.pdfView);
        layout = view.findViewById(R.id.maps_layout);
        name = view.findViewById(R.id.text_maps_name);
        desc = view.findViewById(R.id.text_maps_desc);
        place = view.findViewById(R.id.text_maps_acces);
        layout.setVisibility(GONE);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Event listeners to search field text changes, gets query and filters the RecyclerView according to it
     * @param query the text query
     * @return  False
     * @see RecyclerViewAdapterPosts#filter(String)
     */
    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        this.query = query;
       // Log.i("e", query);
        adapter.changeCursor(adapter.runQueryOnBackgroundThread(query));
        return true;
    }

    private void loadPdf(String name) {
        layout.setVisibility(GONE);
        pdf.fromAsset(name).defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableAnnotationRendering(true)
                .load();
    }

    private void loadPdf(long id) {
        Log.i("TEST", String.valueOf(id));
        Cursor cursor = db.getPlace(id);
        if (cursor != null) {
           // Log.i("NULL", "CURSOR IS NULL");

            cursor.moveToFirst();
            name.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Maps.COLUMN_DPNAME)));
            desc.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Maps.COLUMN_DESC)));
            try {
                JSONArray array = new JSONArray(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Maps.COLUMN_MARK)));
                if (array.getString(1).equals("NU")) {
                    place.setVisibility(GONE);
                } else {
                    String toAdd;
                    switch (array.getString(1)) {
                        case "GL":
                            toAdd = "Grand Lycée";
                            break;
                        case "PL":
                            toAdd = "Petit Lycée";
                            break;
                        case "IN":
                            toAdd = "Internat";
                            break;
                        default:
                            toAdd = "Error";
                            break;
                    }
                    place.setText(getResources().getString(R.string.floor) + " " + array.getString(0) + getResources().getString(R.string.from_building) + toAdd);
                }
            } catch (JSONException e) {
                place.setVisibility(GONE);
            }
            final JSONObject pos;
            try {
                pos = new JSONObject(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Maps.COLUMN_POS)));
                initialX = pos.getDouble("x");
                initialY = pos.getDouble("y");
            } catch (JSONException e) {
                initialX = 0;
                initialY = 0;
            }
            pdf.fromAsset(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Maps.COLUMN_FILE)))
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableAnnotationRendering(true)
                    .enableDoubletap(true)
                    .onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                            if (initialX != 0) {
                                Drawable toDraw = getResources().getDrawable(R.drawable.ic_place_black_24dp);
                                if (Build.VERSION.SDK_INT >= 21) {
                                    toDraw.setTint(getResources().getColor(R.color.place));
                                }
                                toDraw.setLevel(2);
                                toDraw.setAlpha(235);
                                x = initialX * pageWidth;
                                y = initialY * pageHeight;

                                toDraw.setBounds((int) Math.round(x - pageWidth / 30), (int) Math.round(y - pageWidth / 15), (int) Math.round(x + pageWidth / 30), (int) Math.round(y));
                                toDraw.draw(canvas);
                            }
                        }
                    })
                    .load();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdf.resetZoom();
                            pdf.zoomWithAnimation((float) x, (float) y, 2.5f);
                        }
                    });
                }
            }).start();
            layout.setVisibility(View.VISIBLE);
            cursor.close();
        }
    }

    public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private LinkedHashMap<String, List<String>> expandableListDetail;

        public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, LinkedHashMap<String, List<String>> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String expandedListText = (String) getChild(listPosition, expandedListPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView expandedListTextView = (TextView) convertView
                    .findViewById(R.id.expandedListItem);
            expandedListTextView.setText(expandedListText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableListTitle.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            TextView listTitleTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }
}
