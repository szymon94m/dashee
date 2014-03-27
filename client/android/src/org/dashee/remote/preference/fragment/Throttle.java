package org.dashee.remote.preference.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.dashee.remote.R;
 
/**
 * The fragment for our throttle.
 */
public class Throttle 
    extends PreferenceFragment
{
    /**
     * The things to fix when on create is run.
     */ 
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.throttle);
    }
}
