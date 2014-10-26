package org.techteam.bashhappens.gui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.techteam.bashhappens.R;

public class SettingsFragment extends PreferenceFragment {
    public static final String NAME = SettingsFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}