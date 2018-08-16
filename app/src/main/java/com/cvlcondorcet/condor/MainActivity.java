package com.cvlcondorcet.condor;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


/**
 * Main activity, frame for fragments, home for navigation drawer, etc.
 * @author Quentin DE MUYNCK
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Class fragmentClass;
    public static String locale;
    public static String TOPIC_ID = "***REMOVED***";
    private HashMap<Integer, Class> correspondance;
   // private BroadcastReceiver mRegistrationBroadcastReceiver;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static SharedPreferences preferences, default_preferences;

    /**
     * Sets up activity, loading language and locale.
     * Loads old activity state (fragment), starting syncing at app starts, etc.
     * @param savedInstanceState    Old state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();*/

// Initialize Fabric with the debug-disabled crashlytics.
        //Fabric.with(this, crashlyticsKit);
        /* Initialising Firebase and remote control */
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
      /*  FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);*/
        mFirebaseRemoteConfig.setDefaults(R.xml.defaults_remote_param);

        preferences = getSharedPreferences("notifications",0);
        default_preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(default_preferences.getBoolean("firebase", false)) {
            Log.i("STARTUP", "DONE");
        } else {
            Log.i("STARTUP", "SOMETHING WENT WRONG");
        }

        if (default_preferences.getBoolean("crashlytics", false)) {
            Log.i("TEST", "AUTOINITIALIZATION CRASHLYTICS");
            Fabric.with(this, new Crashlytics());
        }

        mFirebaseRemoteConfig.fetch(0)

                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        }
                    }
                });

        /* Setting general parameters*/
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

        /* Setting up the toolbar and the navigation drawer */
        Toolbar bar = findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        try {
            getSupportActionBar().setHomeAsUpIndicator(setBadgeCount(this, 0));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
            Crashlytics.logException(ignored);}
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nvView);
        setupDrawerContent(navigationView);

        /* Getting data from db and detect if first use or not */
        Database db = new Database(this);
        db.open();
        if (db.timestamp("name").equals("")) {
            selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_sync));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Catégorie");
            builder.setItems(R.array.cat, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getApplication());
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
                        case 5:
                            analytics.setUserProperty("category", "NSP");
                            break;
                    }
                    recreate();
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
                }
            });
            builder.create().show();
            Intent servicee = new Intent(getApplicationContext(), Sync.class);
            servicee.putExtra("from", "activity");
            startService(servicee);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setItems(null,null);
            builder2.setTitle("Merci !");
            builder2.setMessage(Html.fromHtml(getString(R.string.end_sync)));
            builder2.setCancelable(true);
            builder2.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder2.create();
            dialog.show();
        } else {
            try {
                db.open();
                Sync.rssURL = db.timestamp("website") + "feed";
                db.close();
            } catch (SQLException ignored) { Crashlytics.logException(ignored); }

            if (savedInstanceState != null) {
                try {
                    fragmentClass = (Class) savedInstanceState.getSerializable("class");
                    Fragment fg = (Fragment) fragmentClass.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fg).commit();
                } catch (NullPointerException ignored) { Crashlytics.logException(ignored);
                } catch (IllegalAccessException ignored) { Crashlytics.logException(ignored);
                } catch (InstantiationException ignored) { Crashlytics.logException(ignored);
                }
            } else {
                if (default_preferences.getBoolean("sync_app_start", false) && allowConnect(this)) {
                    Intent servicee = new Intent(getApplicationContext(), Sync.class);
                    servicee.putExtra("from", "activity");
                    startService(servicee);
                }
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("fragment")) {
                    selectFromParam(getIntent().getStringExtra("fragment"));
                } else {
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
                }
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_ID);
        Log.i("CONDOR", "\""+FirebaseInstanceId.getInstance().getId()+"\"");

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    setupDrawerContent(navigationView);
                    navigationView.setCheckedItem((Integer) getKeyFromValue(correspondance, getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size() - 1).getClass()));
                } catch(NullPointerException ignored ) {}
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
        Log.i("CONDOR", "Saving instance State");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        setupDrawerContent(navigationView);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(loadLanguage(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadLanguage(this);
        Log.i("CONFIGURATION", "CONFIGURATION CHANGED CALLED");
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
        String[] params = {"posts", "events", "maps", "train", "cvl", "canteen"};
        int[] id = {R.id.nav_posts, R.id.nav_events, R.id.nav_maps, R.id.nav_train, R.id.nav_cvl, R.id.nav_canteen};
        for (int i = 0 ; i <id.length ; i++) {
            if (!mFirebaseRemoteConfig.getBoolean(params[i])) {
                nav.getMenu().findItem(id[i]).setVisible(false);
            }
        }

        int posts_count  = preferences.getInt("posts_count", 0);
        int events_count = preferences.getInt("events_count", 0);
        Log.i("START", String.valueOf(posts_count));
        Log.i("START", String.valueOf(events_count));
        boolean cvl = preferences.getBoolean("cvl", false);
        boolean maps = preferences.getBoolean("maps", false);
        if (maps){
            Log.i("DEAT","WORKED");
        } else {
            Log.i("DEAT","NOT WORKED");
        }
        boolean canteen = preferences.getBoolean("canteen", false);
        int count = posts_count+events_count;
        Log.i("START", String.valueOf(count));
        if (posts_count > 0) {
            nav.getMenu().findItem(R.id.nav_posts).setActionView(R.layout.menu_counter);
            setMenuCounter(R.id.nav_posts, posts_count);
        } else { nav.getMenu().findItem(R.id.nav_posts).setActionView(null); }
        if (events_count > 0) {
            nav.getMenu().findItem(R.id.nav_events).setActionView(R.layout.menu_counter);
            setMenuCounter(R.id.nav_events, events_count);
        } else { nav.getMenu().findItem(R.id.nav_events).setActionView(null);  }
        if (cvl) {
            nav.getMenu().findItem(R.id.nav_cvl).setActionView(R.layout.menu_counter);
            count++;
        } else { nav.getMenu().findItem(R.id.nav_cvl).setActionView(null);  }
        if (maps) {
            nav.getMenu().findItem(R.id.nav_maps).setActionView(R.layout.menu_counter);
            count++;
        } else { nav.getMenu().findItem(R.id.nav_maps).setActionView(null);  }
        if (canteen) {
            nav.getMenu().findItem(R.id.nav_canteen).setActionView(R.layout.menu_counter);
            count++;
        } else { nav.getMenu().findItem(R.id.nav_canteen).setActionView(null);  }
        getSupportActionBar().setHomeAsUpIndicator(setBadgeCount(this, count));



        nav.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //noinspection ConstantConditions
                        if (item == null) {
                            return true;
                        } else {
                            selectDrawerItem(item);
                            return false;
                        }
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
        switch (item.getItemId()) {
            case R.id.nav_train:
                if (getPackageManager().getLaunchIntentForPackage("com.sncf.fusion") != null) {
                    value = "train_app";
                    Intent intent = new Intent();
                    intent.setAction("com.sncf.fusion.STATION");
                    try {
                        if (getPackageManager().getPackageInfo("com.sncf.fusion", 0).versionCode <= 220) {
                            intent.setComponent(new ComponentName("com.sncf.fusion", "com.sncf.fusion.ui.station.trainboard.StationBoardsActivity"));
                        } else {
                            intent.setComponent(new ComponentName("com.sncf.fusion", "com.sncf.fusion.feature.station.ui.trainboard.StationBoardsActivity"));
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                    intent.putExtra("stationUic", "87184002");
                    intent.addCategory("DEFAULT");
                    try {
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        intent = getPackageManager().getLaunchIntentForPackage("com.sncf.fusion");
                        startActivity(intent);
                    }
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.sncf.fusion")));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.sncf.fusion")));
                    }
                }
                break;
            case R.id.nav_bus:
                if (mFirebaseRemoteConfig.getBoolean("bus")) {
                    fragmentClass = BusFragment.class;
                } else {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("fr.optymo.app");
                    if (intent == null) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "fr.optymo.app")));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "fr.optymo.app")));
                        }
                    } else {
                        startActivity(intent);
                    }
                }

                break;
            default:
                fragmentClass = correspondance.get(item.getItemId());
                break;
        }
        if (fragmentClass != null) {
            try {
                value = fragmentClass.getCanonicalName();
                value = value.substring(24);
                fragment = (Fragment) fragmentClass.newInstance();

                getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).addToBackStack(String.valueOf(fragment.getId())).commitAllowingStateLoss();
                navigationView.setCheckedItem(item.getItemId());
            } catch (Exception ignored) { Crashlytics.logException(ignored);
            }
        }

        Log.i("11", value);
        if (value.equals("")) {
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, value);
            analytics.logEvent("fragment", params);
        }

        drawerLayout.closeDrawers();
    }

    /**
     * Holds back pressed event and transmits it to needed fragment(s).
     */
    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fg = manager.getFragments().get(manager.getFragments().size()-1);
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
        try {
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
            return !default_preferences.getBoolean("mobile_data_usage", true) && networkInfo != null & networkInfo.getType() == ConnectivityManager.TYPE_WIFI || default_preferences.getBoolean("mobile_data_usage", true) && networkInfo != null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private static Object getKeyFromValue(Map hm, Object value) {
        for (Object o: hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @SuppressLint("ObsoleteSdkInt")
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

    private void setMenuCounter(@IdRes int itemId, int count) {
        TextView view = navigationView.getMenu().findItem(itemId).getActionView().findViewById(R.id.menu_counter);
        view.setText(count > 0 ? String.valueOf(count) : null);
    }

    private static Drawable setBadgeCount(Context context, int badgeCount){
        LayerDrawable icon = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.ic_counter_hamburger);
        Drawable mainIcon = ContextCompat.getDrawable(context, R.drawable.ic_menu_black_24dp);
        BadgeDrawable badge = new BadgeDrawable(context);
        badge.setCount(String.valueOf(badgeCount));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
        icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon);
        return icon;
    }

    private void selectFromParam(String extra) {
        try {
            switch (extra) {
                case "sync":
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_sync));
                    break;
                case "posts":
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_posts));
                    break;
                case "events":
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_events));
                    break;
                case "canteen":
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_canteen));
                    break;
                case "cvl":
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_cvl));
                    break;
                case "maps":
                    Bundle bundle = new Bundle();
                    if (getIntent().hasExtra("place")) {
                        bundle.putString("place", getIntent().getStringExtra("place"));
                    } else { bundle.putString("place", ""); }
                    Fragment fragment = MapsFragment.class.newInstance();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).addToBackStack(String.valueOf(fragment.getId())).commitAllowingStateLoss();
            }
        } catch (Exception e) {
           // Log.i("TEST", getIntent().getExtras().toString());
            selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
        }
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        Log.i("MAIN", "New intent received");
        if (newIntent.getExtras() != null) {
            if (newIntent.getExtras().containsKey("fragment")) {
                if (newIntent.getStringExtra("fragment").equals("maps")) {
                    Bundle bundle = new Bundle();
                    if (newIntent.hasExtra("place")) {
                        bundle.putString("place", newIntent.getStringExtra("place"));
                    } else { bundle.putString("place", ""); }
                    Fragment fragment;
                    try {
                        fragment = MapsFragment.class.newInstance();
                        fragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).addToBackStack(String.valueOf(fragment.getId())).commitAllowingStateLoss();
                    } catch (InstantiationException ignored) {
                    } catch (IllegalAccessException ignored) {
                    }

                } else if (newIntent.getStringExtra("fragment").equals("nav")) {
                    setupDrawerContent(navigationView);
                } else if (newIntent.getStringExtra("fragment").equals("restart")) {
                    setupDrawerContent(navigationView);
                    selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
                } else {
                    selectFromParam(newIntent.getStringExtra("fragment"));
                }

            }/* else if (newIntent.getExtras().containsKey("name") && newIntent.getStringExtra("name").equals("cgu")) {
                //Intent intent2 = new Intent(this, LicensesActivity.class);
                // intent2.putExtra()
            }*/
        }
    }
}
