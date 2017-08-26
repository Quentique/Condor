package com.cvlcondorcet.condor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import java.util.Locale;

/**
 * Main activity, frame for fragments, home for navigation drawer, etc.
 * @author Quentin DE MUYNCK
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    //private ActionBarDrawerToggle drawerToggle;
    private Class fragmentClass;
    public static String locale;

    /**
     * Sets up activity, loading language and locale.
     * Loads old activity state (fragment), starting syncing at app starts, etc.
     * @param savedInstanceState    Old state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        Locale localeChosen;
        if (PreferenceManager.getDefaultSharedPreferences(this).getString("language", "default").equals("default")) {
            if (Build.VERSION.SDK_INT >= 24) {
                localeChosen = res.getConfiguration().getLocales().get(0);
            } else {
                localeChosen = res.getConfiguration().locale;
            }
        } else {
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            localeChosen = new Locale(PreferenceManager.getDefaultSharedPreferences(this).getString("language", "fr"));
            conf.setLocale(localeChosen); // API 17+ only.
            res.updateConfiguration(conf, dm);
        }
        locale = localeChosen.getISO3Language();
        Log.i("E", locale);
        setContentView(R.layout.activity_main);




       /* final Button bouton = (Button) findViewById(R.id.button);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent service = new Intent(getApplicationContext(), TeachersFragment.class);
                startActivity(service);
            }
        });*/

        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(navigationView);
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new PostsFragment());
        ft.commit();*/

        if (savedInstanceState != null){
            try {
                fragmentClass = (Class) savedInstanceState.getSerializable("class");
                Fragment fg = (Fragment) fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fg).commit();
            } catch (NullPointerException e) {
            } catch (IllegalAccessException e) {
            } catch (InstantiationException e) {}
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync_app_start", true) && allowConnect(this)) {
                Intent servicee = new Intent(getApplicationContext(), Sync.class);
                startService(servicee);
            }
            if (getIntent().getExtras() != null) {
                try {
                    // getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, new PostsFragment()).commit();
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_posts));
                } catch (Exception e) {}
            } else {
                selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
            }
        }
        Database db = new Database(this);
        db.open();
        if (db.timestamp("name").equals("")) {
            selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_sync));
        }
        db.close();
        /*Locale locale = new Locale(PreferenceManager.getDefaultSharedPreferences(this).getString("language", "default"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());*/

        Log.i("Test2", getApplicationContext().getFilesDir().toString());
    }

    /**
     * Saves activity state and actual fragment (in order to be reloaded later).
     * @param outState  state that is being saved
     * @see MainActivity#onCreate(Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("class", fragmentClass);
    }

    /* public Toolbar getSupportBar() {
         return bar;
     }
 */

    /**
     * Handles menu click and opening navigation drawer
     * @param item  menu item clicked
     * @return  True
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the navigation drawer and handling click.
     * @param nav   navigation view that must be set up
     */
    private void setupDrawerContent(NavigationView nav) {
        nav.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    /**
     * Starts correspond intent to menu item.
     * @param item  menu item clicked
     * @see #setupDrawerContent(NavigationView)
     */
    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        fragmentClass = null;
        switch(item.getItemId()) {
            case R.id.nav_posts:
                fragmentClass = PostsFragment.class;
                break;
            case R.id.nav_teachers:
                fragmentClass = TeachersFragment.class;
                break;
            case R.id.nav_canteen:
                fragmentClass = CanteenFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = PreferencesFragment.class;
                break;
            case R.id.nav_train:
                fragmentClass = TrainFragment.class;
                break;
            case R.id.nav_bus:
                fragmentClass = BusFragment.class;
                break;
            case R.id.nav_sync:
                fragmentClass = SyncingFragment.class;
                break;
            case R.id.nav_help:
                fragmentClass = HelpFragment.class;
                break;
            case R.id.nav_home:
                fragmentClass = MainFragment.class;
                break;
            default:
                Log.i("PATATE", "CHAUDE");
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {}

        getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).commit();
        item.setChecked(true);
        drawerLayout.closeDrawers();
    }

    /**
     * Holds back pressed event and transmits it to needed fragment(s).
     */
    @Override
    public void onBackPressed() {
        Fragment fg = getSupportFragmentManager().getFragments().get(0);
        if (fg.getClass() == BusFragment.class)
        {
            ((BusFragment) fg).backPressed();
        }
    }

    /**
     * Checks whether the internet connection exists & could be used.
     * @param context   context
     * @return  Whether the connection is ready for use
     */
    public static boolean allowConnect(Context context) {
        ConnectivityManager conn =  (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            if (!settings.getBoolean("mobile_data_usage", false) && networkInfo != null & networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else return settings.getBoolean("mobile_data_usage", false) && networkInfo != null;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
