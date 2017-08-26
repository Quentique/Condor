package com.cvlcondorcet.condor;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Implementation of {@link PreferenceFragmentCompat}
 * @author Quentin DE MUYNCK
 */

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getActivity().setTitle(R.string.settings);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
