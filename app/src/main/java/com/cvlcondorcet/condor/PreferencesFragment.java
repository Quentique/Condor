package com.cvlcondorcet.condor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

/**
 * Implementation of {@link PreferenceFragmentCompat}
 * @author Quentin DE MUYNCK
 */

public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getActivity().setTitle(R.string.settings);
        setPreferencesFromResource(R.xml.preferences, rootKey);
        findPreference("uniqueid").setVisible(false);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i("HELLO", "IM HERE");
        if(key.equals("language")){
            getActivity().onConfigurationChanged(getResources().getConfiguration());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("HELLO", "IM3 HERE");
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i("HELLO", "IM2 HERE");
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
