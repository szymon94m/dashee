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
import android.widget.Toast;
import android.widget.TextView;

import org.dashee.remote.R;
import org.dashee.remote.preference.fragment.dialog.MinMax;
 
/**
 * This will handle our Roll preference specifics
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
     * The current value of min.
     */
    private int min;

    /**
     * The current value of max.
     */
    private int max;
    
    /**
     * Do Nothing but define.
     */
    public Roll()
    {
        this.min = 0;
        this.max = 100;
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

        this.updateMinMaxTextView();
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
                    MinMax minmax = new MinMax();
                    minmax.setMin(Roll.this.min);
                    minmax.setMax(Roll.this.max);
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

    /**
     * Update the text view.
     */ 
    public void updateMinMaxTextView()
    {
        TextView tv
            = (TextView) this.view.findViewById(R.id.minmax_text);
        tv.setText(this.min + "-" + this.max);
    }

    /**
     * Set the min value of the fragment
     */ 
    public void setMin(int min)
    {
        this.min = min;
        this.updateMinMaxTextView();
    }

    /**
     * Set the max value of the fragment
     */ 
    public void setMax(int max)
    {
        this.max = max;
        this.updateMinMaxTextView();
    }
}
