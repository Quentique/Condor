package com.cvlcondorcet.condor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;*/

/**
 * Main activity, frame for fragments, home for navigation drawer, etc.
 * @author Quentin DE MUYNCK
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Class fragmentClass;
    public static String locale;
    public Map<Integer, Class> correspondance;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    /**
     * Sets up activity, loading language and locale.
     * Loads old activity state (fragment), starting syncing at app starts, etc.
     * @param savedInstanceState    Old state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        correspondance = new HashMap<>();
        correspondance.put(R.id.nav_posts, PostsFragment.class);
        correspondance.put(R.id.nav_bus, BusFragment.class);
        correspondance.put(R.id.nav_canteen, CanteenFragment.class);
        correspondance.put(R.id.nav_cvl, CVLFragment.class);
        correspondance.put(R.id.nav_events, EventsFragment.class);
        correspondance.put(R.id.nav_help, HelpFragment.class);
        correspondance.put(R.id.nav_home, MainFragment.class);
        correspondance.put(R.id.nav_maps, MapsFragment.class);
        correspondance.put(R.id.nav_settings, PreferencesFragment.class);
        correspondance.put(R.id.nav_sync, SyncingFragment.class);
        correspondance.put(R.id.nav_train, TrainFragment.class);

        setContentView(R.layout.activity_main);
        Event.format = getString(R.string.date_format);
        Event.format2 = getString(R.string.hour_format);

        Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nvView);
        setupDrawerContent(navigationView);

        Database db = new Database(this);
        db.open();
        if (db.timestamp("name").equals("")) {
            selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_sync));
            final FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Catégorie");
            builder.setItems(R.array.cat, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:
                            analytics.setUserProperty("category", "Secondes");
                            break;
                        case 1:
                            analytics.setUserProperty("category", "Premières");
                            break;
                        case 2:
                            analytics.setUserProperty("category", "Terminales");
                            break;
                        case 3:
                            analytics.setUserProperty("category", "BTS");
                            break;
                        case 4:
                            analytics.setUserProperty("category", "Personnels");
                            break;
                    }

                }
            });
            builder.create().show();
            Intent servicee = new Intent(getApplicationContext(), Sync.class);
            startService(servicee);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setItems(null,null);
            builder2.setTitle("Félicitations !");
            builder2.setMessage(Html.fromHtml("<) style=\"text-align: justify;\">Vous avez installé Condor avec succès et nous vous en remercions. Nous téléchargeons actuellement les derniers éléments nécessaires. Merci !</p><br/><br/><strong>En utilisant Condor, vous acceptez les CGU présentes dans la rubrique \"Aide\" de Condor.</strong>"));
            builder2.setCancelable(true);
            builder2.create().show();
        } else {
            try {
                db.open();
                Sync.rssURL = db.timestamp("website") + "feed";
                db.close();
            } catch (SQLException e) {
            }

            if (savedInstanceState != null) {
                try {
                    fragmentClass = (Class) savedInstanceState.getSerializable("class");
                    Fragment fg = (Fragment) fragmentClass.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fg).commit();
                } catch (NullPointerException e) {
                } catch (IllegalAccessException e) {
                } catch (InstantiationException e) {
                }
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("sync_app_start", true) && allowConnect(this)) {
                    Intent servicee = new Intent(getApplicationContext(), Sync.class);
                    startService(servicee);
                }
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("fragment")) {
                    try {
                        switch (getIntent().getStringExtra("fragment")) {
                            case "sync":
                                selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_sync));
                                break;
                            case "posts":
                                selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_posts));
                                break;
                            case "events":
                                selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_events));
                                break;
                            case "maps":
                                Bundle bundle = new Bundle();
                                bundle.putString("place", getIntent().getStringExtra("place"));
                                Fragment fragment = MapsFragment.class.newInstance();
                                fragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
                        }
                    } catch (Exception e) {
                        Log.i("TEST", getIntent().getExtras().toString());
                        selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
                    }
                } else {
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
                }
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("***REMOVED***");

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                navigationView.setCheckedItem((Integer) getKeyFromValue(correspondance,getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1).getClass()));
            }
        });
        db.close();
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(loadLanguage(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadLanguage(this);
        recreate();
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
                        return false;
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
        Fragment fragment;
        fragmentClass = null;
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle params = new Bundle();

        String value = "";
        if (item.getItemId() == R.id.nav_train) {
            if (getPackageManager().getLaunchIntentForPackage("com.sncf.fusion") == null) {
                fragmentClass = TrainFragment.class;
                value="train_web";
            } else {
                value="train_app";
                Intent intent = new Intent();
                intent.setAction("com.sncf.fusion.STATION");
                intent.setComponent(new ComponentName("com.sncf.fusion", "com.sncf.fusion.ui.station.trainboard.StationBoardsActivity"));
                intent.putExtra("stationUic", "87184002");
                intent.addCategory("DEFAULT");
                startActivity(intent);
            }
        } else {
            fragmentClass = correspondance.get(item.getItemId());
        }
        if (fragmentClass != null) {
            Log.i("ID", fragmentClass.toString());
            try {
                value = fragmentClass.getCanonicalName();
                value = value.substring(24);
                fragment = (Fragment) fragmentClass.newInstance();

                getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).addToBackStack(String.valueOf(fragment.getId())).commit();
                navigationView.setCheckedItem(item.getItemId());

            } catch (Exception e) {
            }
        }

        Log.i("11", value);
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
        analytics.logEvent("page", params);
        drawerLayout.closeDrawers();
    }

    /**
     * Holds back pressed event and transmits it to needed fragment(s).
     */
    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fg = manager.getFragments().get(manager.getFragments().size()-1);
        Log.i("TEST", "Back pressed");
        Log.i("TEST2", String.valueOf(manager.getBackStackEntryCount()));
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

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o: hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
            Log.i("TEST", String.valueOf(o));
        }
        return null;
    }

    private Context loadLanguage(Context c) {
        if (PreferenceManager.getDefaultSharedPreferences(c).getString("language", "default").equals("default")) {
            return c;
        } else {
            Locale locale = new Locale(PreferenceManager.getDefaultSharedPreferences(c).getString("language", "en"));
            Locale.setDefault(locale);

            Resources res = c.getResources();
            Configuration conf = new Configuration(res.getConfiguration());
            if (Build.VERSION.SDK_INT >= 17) {
                conf.setLocale(locale);
                c = c.createConfigurationContext(conf);
            } else {
                conf.locale = locale;
                res.updateConfiguration(conf, res.getDisplayMetrics());
            }
            return c;
        }
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        if (newIntent.getExtras().containsKey("fragment") && newIntent.getStringExtra("fragment").equals("maps")) {
            Bundle bundle = new Bundle();
            bundle.putString("place", newIntent.getStringExtra("place"));
            Fragment fragment = null;
            try {
                fragment = MapsFragment.class.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).addToBackStack(String.valueOf(fragment.getId())).commitAllowingStateLoss();
        }
    }
}
