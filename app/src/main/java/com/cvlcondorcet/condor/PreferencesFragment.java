package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by Quentin DE MUYNCK on 23/07/2017.
 */

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getActivity().setTitle("Paramètres");
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
