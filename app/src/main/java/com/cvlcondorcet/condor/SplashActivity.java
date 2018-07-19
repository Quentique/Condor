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
    protected Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, MainActivity.class);
        Log.i("OPENING", "SPLASH ACITIVTY");
        try {
            List<String> list = getIntent().getData().getPathSegments();
            if (!list.isEmpty()) {
                switch (list.get(0)) {
                    case "posts":
                        if (!list.get(1).equals("")) {
                           launchPosts(list.get(1));
                        }
                        break;
                    case "events":
                        if (!list.get(1).equals("")) {
                           launchEvents(list.get(1));
                        }
                        break;
                    case "maps":
                        if (!list.get(1).equals("")) {
                            intent.putExtra("fragment", "maps");
                            intent.putExtra("place", list.get(1));
                            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_DEBUG_LOG_RESOLUTION);
                            startActivity(intent);

                            //NavUtils.navigateUpTo(this, intent);
                        }
                        break;
                    case "cgu":
                        Intent intent2 = new Intent(this, LicensesActivity.class);
                        intent2.putExtra("name", "cgu");
                        Log.i("TEST", "CGU");
                        startActivity(intent2);
                        break;

                    default:
                        Log.i("TEST", "DEFAULT CASE");
                        startActivity(intent);
                        break;
                }
            } else {
                startActivity(intent);
            }
        } catch (NullPointerException e) {
            Log.i("ERROR", "CATCH EXCEPTION");
            if (getIntent().getExtras() != null ) {
                if (getIntent().getExtras().containsKey("posts")) {
                    launchPosts(getIntent().getExtras().getString("posts"));
                } else if (getIntent().getExtras().containsKey("events")) {
                    launchEvents(getIntent().getExtras().getString("events"));
                } else {
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                }
            } else {
                startActivity(intent);
            }
        }
    }

    protected void launchPosts(String id) {
        Intent intent2 = new Intent(this, PostViewerActivity.class);
        intent2.putExtra("id", id);
        startActivity(intent);
        startActivity(intent2);
    }

    protected void launchEvents(String id) {
        Intent intent2 = new Intent(this, EventViewerActivity.class);
        intent2.putExtra("id", id);
        startActivity(intent);
        startActivity(intent2);
    }
}
