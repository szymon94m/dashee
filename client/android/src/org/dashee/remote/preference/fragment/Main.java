package org.dashee.remote.preference.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.content.Intent;

import org.dashee.remote.R;
import org.dashee.remote.model.Config;

import java.util.List;
import java.util.Map;

/**
 * This will handle our Preference object
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class Main
    extends PreferenceFragment 
    implements OnSharedPreferenceChangeListener
{
    SharedPreferences sharedPref;
    List<String> summaryToValue = java.util.Arrays.asList(
            "pref_server_ip", 
            "pref_server_port"
        );

    /**
     * Create our Main fragment, by setting all the fragments
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
            
        this.sharedPref = getPreferenceScreen().getSharedPreferences();
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        
        this.setSharedPreferenceState(sharedPref);
    }
        
    // @Override
    public boolean onPreferenceTreeClick(PreferenceScreen ps, Preference p) 
    {
        return super.onPreferenceTreeClick(ps, p);
    }
    
    /**
     * Set the Value of shared preferences given the last state.
     *
     * @param sharedPref - The sharedPref Object
     */
    private void setSharedPreferenceState(SharedPreferences sharedPref) 
    {
        Map<String,?> values = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : values.entrySet())
        {
            if(summaryToValue.contains(entry.getKey()))
                findPreference(entry.getKey()).setSummary(
                        sharedPref.getString(entry.getKey(), "0")
                    );
        }
    }

    /**
     * Change the value of the Textbox
     *
     * @param sharedPreferences The SharedPreference object
     * @param key The preference changed
     */
    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, 
            String key
        )
    {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) 
        {
            EditTextPreference temp = (EditTextPreference) pref;
            pref.setSummary(temp.getText());
        }
    }
}
