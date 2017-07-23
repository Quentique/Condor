package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private Toolbar bar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* final Button bouton = (Button) findViewById(R.id.button);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent service = new Intent(getApplicationContext(), ProfsActivity.class);
                startActivity(service);
            }
        });
        Intent servicee = new Intent(getApplicationContext(), Sync.class);
        startService(servicee);*/
        bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(navigationView);
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new PostsActivity());
        ft.commit();*/

        Log.i("Test2", getApplicationContext().getFilesDir().toString());
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
        Class fragmentClass = null;
        switch(item.getItemId()) {
            case R.id.nav_posts:
                fragmentClass = PostsActivity.class;
                break;
            case R.id.nav_teachers:
                fragmentClass = ProfsActivity.class;
                break;
            case R.id.nav_canteen:
                fragmentClass = CanteenActivity.class;
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
}
