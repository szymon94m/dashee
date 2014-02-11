package org.dashee.remote.preference.fragment;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.dashee.remote.R;
 
/**
 * This will handle our Preference object
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class Roll
	extends Fragment
{
    public Roll()
    {

    }

    /**
     * The view of the fragment. Useful to retrieve the layout values within 
     * other methods
     */
    private View view;

    /**
     * Set our HUD. Initiate the servers IP address so our thread can talk to 
     * it, start our thread and return the view which is required by this 
     * function
     */
    public View onCreateView(
            LayoutInflater inflater, 
            ViewGroup container, 
            Bundle savedInstanceState
        ) 
    {
        this.view 
            = inflater.inflate(R.layout.preference_roll, container, false);
        assert this.view != null;

        return this.view;
    }
}
