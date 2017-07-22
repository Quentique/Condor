package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private Toolbar bar;
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new CanteenActivity());
        ft.commit();

        Log.i("Test2", getApplicationContext().getFilesDir().toString());
    }
}
