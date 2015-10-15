package org.raleighccc.www.rcccav;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by pinwu on 10/9/2015.
 */
public class PrefsFragment extends PreferenceFragment {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resources
        addPreferencesFromResource(R.xml.preferences);
    }
}
