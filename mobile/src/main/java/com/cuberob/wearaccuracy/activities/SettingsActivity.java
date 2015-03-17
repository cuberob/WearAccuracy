package com.cuberob.wearaccuracy.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import com.cuberob.wearaccuracy.R;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'data' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_data);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_data_section);
    }
}
