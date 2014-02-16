package org.dashee.remote.preference;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.DialogFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Set the XML view for this activity
        setContentView(R.layout.activity_preference);
        
        ActionBar ab = getActionBar();
        ab.setSubtitle(R.string.pref_roll_subtitle);
        ab.setHomeButtonEnabled(true);
    	
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
     * The listener, for the sucessful dialog
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
        toast.show();
    }
    
    /**
     *
     */
    @Override
    public void onMinMaxNegativeClick(MinMax dialog)
    {
    }
}
