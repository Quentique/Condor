package com.cvlcondorcet.condor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;
import java.util.UUID;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;*/

/**
 * Main activity, frame for fragments, home for navigation drawer, etc.
 * @author Quentin DE MUYNCK
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    //private ActionBarDrawerToggle drawerToggle;
    private Class fragmentClass;
    public static String locale;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

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
       // Log.i("E", locale);
        setContentView(R.layout.activity_main);
        Event.format = getString(R.string.date_format);
        Event.format2 = getString(R.string.hour_format);

        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(navigationView);

        if (savedInstanceState != null){
            try {
                fragmentClass = (Class) savedInstanceState.getSerializable("class");
                Fragment fg = (Fragment) fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fg).commit();
              //  Log.i("INSTANCE", "Old instance exists");
            } catch (NullPointerException e) {
            } catch (IllegalAccessException e) {
            } catch (InstantiationException e) {}
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync_app_start", true) && allowConnect(this)) {
                Intent servicee = new Intent(getApplicationContext(), Sync.class);
                startService(servicee);
               // Log.i("INSTANCE", "New start of app SYNCING");
            }
            if (getIntent().getExtras() != null) {
                try {
                    switch (getIntent().getStringExtra("fragment")) {
                        case "sync":
                            selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_sync));
                            break;
                        case "posts":
                            selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_posts));
                            break;
                    }
                //    Log.i("INSTANCE", "NEW -- Starting Posts");
                } catch (Exception e) {}
            } else {
                selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
              //  Log.i("INSTANCE", "ERROR -- Starting Home");
            }
        }
        Database db = new Database(this);
        db.open();
        if (db.timestamp("name").equals("")) {
            selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_sync));
            if (PreferenceManager.getDefaultSharedPreferences(this).getString("uniqueid", "0").equals("0")){
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("uniqueid", UUID.randomUUID().toString()).commit();
            }
        }
        try {
            db.open();
            Sync.rssURL = db.timestamp("website") + "feed";
          //  Log.i("TEST", Sync.rssURL);
        } catch( SQLException e) { }
        db.close();
       // Log.i("Test2", getApplicationContext().getFilesDir().toString());
        FirebaseMessaging.getInstance().subscribeToTopic("***REMOVED***");
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
            /*case R.id.nav_teachers:
                fragmentClass = TeachersFragment.class;
                break;*/
            case R.id.nav_canteen:
                fragmentClass = CanteenFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = PreferencesFragment.class;
                break;
            case R.id.nav_train:
                if (getPackageManager().getLaunchIntentForPackage("com.sncf.fusion") == null) {
                    fragmentClass = TrainFragment.class;
                } else {
                    Intent intent = new Intent();
                    intent.setAction("com.sncf.fusion.STATION");
                    intent.setComponent(new ComponentName("com.sncf.fusion", "com.sncf.fusion.ui.station.trainboard.StationBoardsActivity"));
                    intent.putExtra("stationUic", "87184002");
                    intent.addCategory("DEFAULT");
                    startActivity(intent);
                }
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
            case R.id.nav_maps:
                fragmentClass = MapsFragment.class;
                break;
            case R.id.nav_events:
                fragmentClass = EventsFragment.class;
                break;
            case R.id.nav_cvl:
                fragmentClass = CVLFragment.class;
                break;
            default:
               // Log.i("PATATE", "CHAUDE");
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
            navigationView.setCheckedItem(item.getItemId());
        } catch (Exception e) {}
        drawerLayout.closeDrawers();
    }

    /**
     * Holds back pressed event and transmits it to needed fragment(s).
     */
    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
       // Log.i("DATA", String.valueOf(manager.getBackStackEntryCount()));
        Fragment fg = manager.getFragments().get(manager.getFragments().size()-1);
        //Log.i("DATA", fg.getClass().toString());
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else if (fg != null && fg.getClass() == BusFragment.class) {
            if (!((BusFragment) fg).backPressed()) {
                manager.popBackStack();
            }
        } else if (manager.getBackStackEntryCount() > 1 ){
            manager.popBackStack();
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
