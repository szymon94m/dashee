package org.dashee.remote.preference;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import org.dashee.remote.R;
 
/**
 * Roll Preference Activity.
 *
 * Used to set the properties specific to our throttle
 */
public class RollActivity 
    extends PreferenceActivity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        ActionBar ab = getActionBar();
        ab.setHomeButtonEnabled(true);

        getFragmentManager()
            .beginTransaction()
            .replace(
                    android.R.id.content, 
                    new org.dashee.remote.preference.fragment.Roll()
                )
            .commit();
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
