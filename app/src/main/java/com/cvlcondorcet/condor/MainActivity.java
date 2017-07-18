package com.cvlcondorcet.condor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button bouton = (Button) findViewById(R.id.button);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent service = new Intent(getApplicationContext(), CanteenActivity.class);
                startActivity(service);
            }
        });
        Intent servicee = new Intent(getApplicationContext(), Sync.class);
        startService(servicee);

        Log.i("Test2", getApplicationContext().getFilesDir().toString());
    }
}
