package com.cvlcondorcet.condor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
                        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_DEBUG_LOG_RESOLUTION);
                        startActivity(intent);

                    }
                    break;
                case "cgu":
                    Intent intent2 = new Intent(this, LicensesActivity.class);
                    intent2.putExtra("name", "cgu");
                    startActivity(intent2);
                    break;

                default:
                    startActivity(intent);
                    break;
            }
        } catch (NullPointerException e) {
            startActivity(intent);
        }
    }
}
