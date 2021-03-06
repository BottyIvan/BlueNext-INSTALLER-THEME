package com.botty.theme.next.blue.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.botty.theme.next.blue.R;

public class About extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TRUE_GOTHIC = "true_gothic";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.about);

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(TRUE_GOTHIC)) {
            sharedPreferences.getBoolean(key, true);
        }
    }
}
