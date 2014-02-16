package org.dashee.remote.preference;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import org.dashee.remote.R;
import org.dashee.remote.preference.fragment.Roll;
import org.dashee.remote.preference.fragment.dialog.MinMax;
import org.dashee.remote.preference.fragment.dialog.MinMax.MinMaxListener;
 
/**
 * This will handle our Preference object
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class RollActivity 
    extends FragmentActivity
    implements MinMaxListener
{
    /**
     * Handler to our Fragment
     */ 
    private Roll roll;

    /**
     * Preference editor.
     */
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
    	initActivity();
        initFragment();
        initPreferenceEditor();
    }

    /**
     * Initialize our Activity
     */
    public void initActivity()
    {
        // Set the XML view for this activity
        setContentView(R.layout.activity_preference);
        
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.pref_roll_subtitle);
        ab.setHomeButtonEnabled(true);
    }

    /**
     * Initialize our Fragments
     */
    public void initFragment()
    {
        //Set the initial view to our HUD
        roll = new Roll();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(
                R.id.preference_roll, 
                roll
            );
        ft.commit();
    }

    /**
     * Get the preference editor from the SharedPreference
     */
    public void initPreferenceEditor()
    {
        SharedPreferences sp = PreferenceManager
            .getDefaultSharedPreferences(this);
        editor = sp.edit();

        roll.setMin(sp.getInt("roll_min", 0));
        roll.setMax(sp.getInt("roll_max", 100));
    }

    /**
     * Handler of the Action bar selected Option
     *
     * @param item - The item clicked
     *
     * @return boolean - true if selected other wise see parent
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
            //Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The listener, for the successful dialog
     */
    @Override
    public void onMinMaxPositiveClick(MinMax dialog)
    {
        Toast toast = Toast.makeText(
                this,
                "Min and Max updated!", 
                Toast.LENGTH_SHORT
        );

        roll.setMin(dialog.getMin());
        roll.setMax(dialog.getMax());
        roll.updateMinMaxTextView();
        editor.putInt("roll_min", dialog.getMin());
        editor.putInt("roll_max", dialog.getMax());
        editor.commit();
        toast.show();
    }
    
    /**
     * The listener, for the cancel dialog button.
     */
    @Override
    public void onMinMaxNegativeClick(MinMax dialog)
    {
    }
}
