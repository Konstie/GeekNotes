package com.gnotes.app;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

// oh, this preference activity needs to be done as soon as possible
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
