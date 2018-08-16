package com.cvlcondorcet.condor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Activity showing splash screen and redirecting the user to the MainActivity or to the ConsentActivity. Loads different modules (Firebase, Crashlytics, etc.)
 * Is also able to catch IntentExtras and launch directly the good Activity
 * @author Quentin DE MUYNCK
 */

public class SplashActivity extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!pref.getBoolean("firebase", false)) {
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false);
            Log.i("TEST", "FirebaseAnalytics disabled");
        } else {
            Log.i("TEST", "AUTOINITIALIZATION FirebaseAnalytics");
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
        }
        if (pref.getBoolean("crashlytics", false)) {
            Log.i("TEST", "AUTOINITIALIZATION CRASHLYTICS");
            Fabric.with(this, new Crashlytics());
        }  else {
        Crashlytics.Builder builder = new Crashlytics.Builder();
        CrashlyticsCore.Builder builder2 = new CrashlyticsCore.Builder();
        builder2.disabled(true);
        builder.core(builder2.build());
        Fabric.with(this, builder.build());
        }
        Log.i("CONSENT", String.valueOf(pref.getInt("version", 0)));
        if (pref.getInt("version",0) < BuildConfig.VERSION_CODE) {
            intent = new Intent(this, ConsentActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(this, MainActivity.class);
          //  intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_DEBUG_LOG_RESOLUTION);
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
                            if (list.size() >= 2 && !list.get(1).equals("")) {
                                intent.putExtra("fragment", "maps");
                                intent.putExtra("place", list.get(1));
                               // intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_DEBUG_LOG_RESOLUTION);
                                startActivity(intent);

                                //NavUtils.navigateUpTo(this, intent);
                            } else {
                                intent.putExtra("fragment", "maps");
                                intent.putExtra("place", "");
                                startActivity(intent);
                            }
                            break;
                        case "cgu":
                            Intent intent2 = new Intent(this, LicensesActivity.class);
                            intent2.putExtra("name", "cgu");
                            Log.i("TEST", "CGU");
                            startActivity(intent2);
                            break;
                        case "cvl":
                            intent.putExtra("fragment", "cvl");
                            startActivity(intent);
                            break;
                        case "canteen":
                            intent.putExtra("fragment", "canteen");
                            startActivity(intent);
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
                if (getIntent().getExtras() != null) {
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
    }

    private void launchPosts(String id) {
        Intent intent2 = new Intent(this, PostViewerActivity.class);
        intent2.putExtra("id", id);
        startActivity(intent);
        startActivity(intent2);
    }

    private void launchEvents(String id) {
        Intent intent2 = new Intent(this, EventViewerActivity.class);
        intent2.putExtra("id", id);
        startActivity(intent);
        startActivity(intent2);
    }
}
