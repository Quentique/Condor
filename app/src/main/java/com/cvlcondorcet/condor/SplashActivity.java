package com.cvlcondorcet.condor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * Activity showing splash screen and starting the {@link MainActivity}
 * @author Quentin DE MUYNCK
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        try {
            List<String> list = getIntent().getData().getPathSegments();
            switch (list.get(0)) {
                case "post":
                    if (!list.get(1).equals("")) {
                        Intent intent2 = new Intent(this, PostViewerActivity.class);
                        intent2.putExtra("id", list.get(1));
                        startActivity(intent);
                        startActivity(intent2);
                    }
                    break;
                case "event":
                    if (!list.get(1).equals("")) {
                        Intent intent2 = new Intent(this, EventViewerActivity.class);
                        intent2.putExtra("id", list.get(1));
                        startActivity(intent);
                        startActivity(intent2);
                    }
                    break;
                case "maps":
                    if(!list.get(1).equals("")) {
                        intent.putExtra("fragment", "maps");
                        intent.putExtra("place", list.get(1));
                        startActivity(intent);
                    }
            }
        } catch (NullPointerException e) {
            startActivity(intent);
        }

    }
}
