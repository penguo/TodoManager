package com.afordev.todomanagermini;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afordev.todomanagermini.Manager.DBManager;
import com.afordev.todomanagermini.Manager.Manager;


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
                return true;
            }
        });
        Preference lockScreen = findPreference(Manager.PREF_LOCK_SCREEN);
        lockScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setData();
                return true;
            }
        });
    }

    private void setData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isLockScreen = prefs.getBoolean(Manager.PREF_LOCK_SCREEN, true);

        Preference viewChecked = findPreference(Manager.PREF_VIEW_CHECKED);
        if (isLockScreen) {
            viewChecked.setEnabled(true);
        } else {
            viewChecked.setEnabled(false);
        }
    }
}