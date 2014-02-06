package org.dashee.remote.preference.fragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.dashee.remote.R;
 
/**
 * This will handle our Preference object
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class Roll
	extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.channels);

        ActionBar ab = getActivity().getActionBar();
        ab.setSubtitle(R.string.pref_roll_subtitle);
    }
}
