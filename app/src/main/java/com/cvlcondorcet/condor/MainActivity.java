package com.cvlcondorcet.condor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private Toolbar bar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Class fragmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       /* final Button bouton = (Button) findViewById(R.id.button);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent service = new Intent(getApplicationContext(), TeachersFragment.class);
                startActivity(service);
            }
        });*/

        bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(navigationView);
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new PostsFragment());
        ft.commit();*/

        if (savedInstanceState != null){
            try {
                fragmentClass = (Class) savedInstanceState.getSerializable("class");
                Fragment fg = (Fragment) fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fg).commit();
            } catch (Exception e) {}
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
            }
        }
        Log.i("Test2", getApplicationContext().getFilesDir().toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("class", (Serializable) fragmentClass);
    }

    public Toolbar getSupportBar() {
        return bar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupDrawerContent(NavigationView nav) {
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

    public void selectDrawerItem(MenuItem item) {
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
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {}

        getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).commit();
        item.setChecked(true);
        drawerLayout.closeDrawers();
    }

    public static boolean allowConnect(Context context) {
        ConnectivityManager conn =  (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (!settings.getBoolean("mobile_data_usage", false) && networkInfo != null & networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (settings.getBoolean("mobile_data_usage", false) && networkInfo != null ) {
            return true;
        } else{
            return false;
        }
    }
}
