package org.dashee.remote.preference;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import org.dashee.remote.R;
 
/**
 * This will handle our Preference object
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class RollActivity 
    extends FragmentActivity
{
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.preference_roll, new org.dashee.remote.preference.fragment.Roll());
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
}
