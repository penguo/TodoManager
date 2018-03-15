package com.afordev.todomanagermini;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afordev.todomanagermini.Manager.DBManager;


/**
 * Created by pengu on 2017-11-25.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preferences);

        Preference resetDB = findPreference("pref_db_reset");
        resetDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DBManager dbManager = DBManager.getInstance(getActivity());
                dbManager.resetDB();
                return false;
            }
        });
    }
}