package com.cvlcondorcet.condor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

/**
 * Implementation of {@link PreferenceFragmentCompat}
 * @author Quentin DE MUYNCK
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        getActivity().setTitle(R.string.settings);
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("firebase", false)) {
            Log.i("SHARED", "ACTIVE");

        } else { Log.i("SHARED", "INACTIVE"); }

        ((SwitchPreferenceCompat) findPreference("firebase")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!((SwitchPreferenceCompat) preference).isChecked()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                    builder2.setItems(null, null);
                    builder2.setTitle("Êtes-vous sûr·e ?");
                    builder2.setMessage(Html.fromHtml(getString(R.string.need_data)));
                    builder2.setCancelable(false);
                    builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((SwitchPreferenceCompat) findPreference("firebase")).setChecked(true);
                            dialog.dismiss();
                        }
                    });
                    builder2.setPositiveButton(R.string.continued, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAnalytics.getInstance(getContext()).resetAnalyticsData();
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder2.create();
                    dialog.show();
                    return true;
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Catégorie");
                    builder.setItems(R.array.cat, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("THIS IS A TEST", String.valueOf(which));
                            FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getContext());
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
                        }
                    });
                    builder.create().show();
                    return true;
                }
            }
        });

        findPreference("show_data").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String content;
                content = "<p>Instance ID : " + FirebaseInstanceId.getInstance().getId()+"</p>";
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setItems(null, null);
                builder2.setTitle(getString(R.string.your_data));
                builder2.setMessage(Html.fromHtml(content));
                builder2.setCancelable(true);
                AlertDialog dialog = builder2.create();
                dialog.show();
                return true;
            }
        });
        findPreference("export_data").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("condor", "{\"instance_id\":\""+FirebaseInstanceId.getInstance().getId()+"\"}");
                try {
                    clipboard.setPrimaryClip(data);
                } catch (NullPointerException e) { Toast.makeText(getContext(), R.string.copy_error, Toast.LENGTH_LONG).show(); }
                Toast.makeText(getContext(), R.string.data_copied, Toast.LENGTH_LONG).show();
                return true;
            }
        });
        findPreference("delete").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setItems(null, null);
                builder2.setTitle("Êtes-vous sûr·e ?");
                builder2.setMessage(Html.fromHtml(getString(R.string.condor_end)));
                builder2.setCancelable(false);
                builder2.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder2.setPositiveButton(R.string.continued, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAnalytics.getInstance(getContext()).resetAnalyticsData();
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException ignored) {}
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt("version", 0).apply();
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
                AlertDialog dialog = builder2.create();
                dialog.show();
                return true;
            }
        });
        findPreference("clear_notif").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
               SharedPreferences.Editor editor =  MainActivity.preferences.edit();
                editor.putInt("posts_count", 0);
                editor.putInt("events_count", 0);
                editor.putBoolean("cvl", false);
                editor.putBoolean("maps", false);
                editor.putBoolean("canteen", false);
                editor.putString("events","[]");
                editor.putString("posts", "[]");
                editor.apply();
                Toast.makeText(getActivity(), R.string.clear_notif_done, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("fragment", "nav");
                startActivity(intent);
                return true;
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("language")){
            getActivity().onConfigurationChanged(getResources().getConfiguration());
        } else if (key.equals("firebase") && !sharedPreferences.getBoolean("firebase", false)) {
            FirebaseAnalytics.getInstance(getContext()).setAnalyticsCollectionEnabled(false);
            FirebaseAnalytics.getInstance(getContext()).resetAnalyticsData();
        }
        if(sharedPreferences.getBoolean("firebase", false)) {
            Log.i("SHARED", "ACTIVE");
        } else { Log.i("SHARED", "INACTIVE"); }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.settings);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
