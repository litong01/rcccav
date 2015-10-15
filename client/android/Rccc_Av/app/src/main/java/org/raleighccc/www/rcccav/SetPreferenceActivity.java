package org.raleighccc.www.rcccav;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by pinwu on 10/9/2015.
 */
public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }
}
