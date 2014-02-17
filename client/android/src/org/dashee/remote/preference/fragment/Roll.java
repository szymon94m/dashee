package org.dashee.remote.preference.fragment;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
        implements OnClickListener
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
     * Preference editor.
     */
    private Editor editor;

    /**
     * List of our Switches
     */
    Switch swInvert;
    Switch swVibrate;
    
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

        this.initSwitches();
        this.initPreferences();
        this.initClickListeners();

        return this.view;
    }
    
    /**
     * Initialize our switches.
     */
    private void initSwitches()
    {
        // Set the value of invert as it was known previously
        this.swInvert = (Switch) this.view.findViewById(R.id.invert_switch);
        this.swVibrate = (Switch) this.view.findViewById(R.id.vibrate_switch);
    }

    /**
     * Get the preference editor from the SharedPreference. This function will
     * also set the state of our last known state of all the known values.
     */
    private void initPreferences()
    {
        SharedPreferences sp = PreferenceManager
            .getDefaultSharedPreferences(this.getActivity());
        editor = sp.edit();

        // Set the value of min and max as it was known previously
        this.setMin(sp.getInt("roll_min", 0));
        this.setMax(sp.getInt("roll_max", 100));
        this.updateMinMaxTextView();
        
        swInvert.setChecked(sp.getBoolean("roll_invert", false));
        swVibrate.setChecked(sp.getBoolean("roll_vibrate", false));
    }

    /**
     * Set all of our click listeners
     */
    private void initClickListeners()
    {
        LinearLayout layout_invert
            = (LinearLayout) this.view.findViewById(R.id.invert);
        layout_invert.setOnClickListener(this);

        LinearLayout layout_vibrate
            = (LinearLayout) this.view.findViewById(R.id.vibrate);
        layout_vibrate.setOnClickListener(this);
        
        LinearLayout layout_minmax
            = (LinearLayout) this.view.findViewById(R.id.minmax);
        layout_minmax.setOnClickListener(this);
        
        swInvert.setOnClickListener(this);
        swVibrate.setOnClickListener(this);
    }

    /**
     * Our click handlers
     *
     * @param v The view clicked
     */ 
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.invert:
                onClickSwitchLayout(swInvert);
                break;
            case R.id.vibrate:
                onClickSwitchLayout(swVibrate);
                break;
            case R.id.invert_switch:
                onClickInvertSwitch();
                break;
            case R.id.vibrate_switch:
                onClickVibrateSwitch();
                break;
            case R.id.minmax:
                onClickMinMax();
                break;
            default:
                break;
        }
    }

    /**
     * Handle the clicking of the invert value.
     *
     * @param sw The switch which this layout belongs to
     */
    public void onClickSwitchLayout(Switch sw)
    {
        sw.performClick();
    }

    /**
     * Handle the inverting of roll.
     */
    public void onClickInvertSwitch()
    {
        if (swInvert.isChecked())
            this.editor.putBoolean("roll_invert", true);
        else
            this.editor.putBoolean("roll_invert", false);

        this.editor.commit();
    }

    /**
     * Handle our vibrate switch.
     */
    public void onClickVibrateSwitch()
    {
        if (swVibrate.isChecked())
            this.editor.putBoolean("roll_vibrate", true);
        else
            this.editor.putBoolean("roll_vibrate", false);

        this.editor.commit();
    }

    /**
     * Handle clicking of the Min and Max value.
     */ 
    public void onClickMinMax()
    {
        MinMax minmax = new MinMax("minmax");
        minmax.setMin(Roll.this.min);
        minmax.setMax(Roll.this.max);
        minmax.show(getActivity().getFragmentManager(), "minmax");
    }

    /**
     * Handler for Positive click for MinMax dialog
     *
     * @param MinMax dialog
     */
    public void onMinMaxPositiveClick(MinMax dialog)
    {
        Toast toast = Toast.makeText(
                this.getActivity(),
                "Min and Max updated!", 
                Toast.LENGTH_SHORT
        );

        this.setMin(dialog.getMin());
        this.setMax(dialog.getMax());
        this.updateMinMaxTextView();
        this.editor.putInt("roll_min", dialog.getMin());
        this.editor.putInt("roll_max", dialog.getMax());
        this.editor.commit();
        toast.show();
    }

    /**
     * Handler for Positive click for MinMax dialog
     *
     * @param MinMax dialog
     */
    public void onMinMaxNegativeClick(MinMax dialog)
    {
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
    }

    /**
     * Set the max value of the fragment
     */ 
    public void setMax(int max)
    {
        this.max = max;
    }
}
