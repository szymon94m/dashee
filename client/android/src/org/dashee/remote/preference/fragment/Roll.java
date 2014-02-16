package org.dashee.remote.preference.fragment;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Switch;

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
    /**
     * The view of the fragment. Useful to retrieve the layout values within 
     * other methods
     */
    private View view;
    
    /**
     * Do Nothing but define.
     */
    public Roll()
    {
    }

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

        initMinMaxListener();
        initInvertListener();

        return this.view;
    }

    /**
     * Listener for Min and Max. Initialize all events with the click listener
     */
    private void initMinMaxListener()
    {
        LinearLayout layout 
            = (LinearLayout) this.view.findViewById(R.id.minmax);
        layout.setOnClickListener(
            new View.OnClickListener() 
            {
                @Override
                public void onClick(View v) 
                {
                    org.dashee.remote.preference.fragment.dialog.MinMax minmax
                        = new org.dashee.remote.preference.fragment.dialog
                            .MinMax();
                    minmax.setMax(90);
                    minmax.show(getActivity().getFragmentManager(), "minmax");

                }
            }
        );
    }

    /**
     * Listener for the Invert switch. Every time we click the invert layout
     * make sure that our invert switch is toggled
     */
    private void initInvertListener()
    {
        LinearLayout layout 
            = (LinearLayout) this.view.findViewById(R.id.invert);
        final Switch sw
            = (Switch) this.view.findViewById(R.id.invert_switch);

        layout.setOnClickListener(
            new View.OnClickListener() 
            {
                @Override
                public void onClick(View v) 
                {
                    sw.performClick();
                }
            }
        );
    }
}
