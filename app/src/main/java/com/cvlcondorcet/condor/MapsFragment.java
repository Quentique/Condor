package com.cvlcondorcet.condor;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
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
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quentin DE MUYNCK on 16/09/2017.
 */

public class MapsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private String query;
    private ArrayMap<String, String> gl, pl, in, ge;
    private ArrayMap<String, List<String>> total;
    private List<String> title;
    private AlertDialog dialog;
    private PDFView pdf;

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
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // item = menu.findItem(R.id.action_search);
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//        search.setOnQueryTextListener(this);
/*        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                query = "";
                return false;
            }
        });*/

        menu.findItem(R.id.menu_select).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dialog.show();
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        getActivity().setTitle("TEST");
        gl=new ArrayMap<>();
        String[] grandl_lycee = getResources().getStringArray(R.array.gl);
        for (int i = 0 ; i<grandl_lycee.length ; i++) {
            gl.put(grandl_lycee[i], String.valueOf(i)+"EGL.pdf");
        }
        pl=new ArrayMap<>();
        String[] petti_lycee = getResources().getStringArray(R.array.pl);
        for (int i = 0 ; i<petti_lycee.length ; i++) {
            pl.put(petti_lycee[i], String.valueOf(i)+"EPL.pdf");
        }
        in=new ArrayMap<>();
        String[] internat = getResources().getStringArray(R.array.in);
        for (int i = 0 ; i<internat.length ; i++) {
            in.put(internat[i], String.valueOf(i)+"EIN.pdf");
        }
        ge = new ArrayMap<>();
        String[] general = getResources().getStringArray(R.array.maps_list);
        for (int i = 0 ; i<general.length ; i++) {
            ge.put(general[i], "GEN.pdf");
        }
        total = new ArrayMap<>();
        total.put("GENERAL", new ArrayList<String>(ge.keySet()));
        total.put("GRAND LYCEE", new ArrayList<String>(gl.keySet()));
        total.put("PETIT LYCEE", new ArrayList<String>(pl.keySet()));
        total.put("INTERNAT", new ArrayList<String>(in.keySet()));
        title  = new ArrayList<>(total.keySet());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select plan");

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
                        loadPdf(gl.valueAt(i1));
                        break;
                    case "PETIT LYCEE":
                        loadPdf(pl.valueAt(i1));
                        break;
                    case "INTERNAT":
                        loadPdf(in.valueAt(i1));
                        break;
                    case "GENERAL":
                        loadPdf(ge.valueAt(i1));
                        break;
                }
                return false;
            }
        });

        builder.setView(list);
        dialog = builder.create();

        pdf = view.findViewById(R.id.pdfView);
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
        Log.i("e", "Query");
        return false;
    }

    private void loadPdf(String name) {
        pdf.fromAsset(name).load();
    }

    public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private ArrayMap<String, List<String>> expandableListDetail;

        public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, ArrayMap<String, List<String>> expandableListDetail) {
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
