package org.dashee.remote.preference.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.List;
import java.util.Map;

import org.dashee.remote.R;
import org.dashee.remote.model.Config;


/**
 * This will handle our Preference object. And also validate the text
 * box values before setting them.
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class Main
    extends PreferenceFragment 
    implements 
        OnSharedPreferenceChangeListener,
        OnPreferenceChangeListener
{

    /**
     * Shared Preference objects used by our class
     */
    SharedPreferences sharedPref;

    /**
     * Edit text box to represent the IP address
     */
    EditTextPreference etpIp;

    /**
     * Edit text bod to represent the PORT address
     */
    EditTextPreference etpPort;

    /**
     * Create our Main fragment, by setting all the fragments. And set our 
     * preferences, and also initialize our elements
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        this.initSharedPreferences();
        this.initElements();
    }

    /**
     * Initialize our preferences.
     */
    private void initSharedPreferences()
    {
        this.sharedPref = this.getPreferenceScreen().getSharedPreferences();
        this.sharedPref.registerOnSharedPreferenceChangeListener(this);

        // Set our Preferences summary values from the shared preferences
        List<String> summaryToValue = java.util.Arrays.asList(
                "pref_server_ip", 
                "pref_server_port"
            );

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
     * Initialize our elements.
     */ 
    private void initElements()
    {
        this.etpIp = (EditTextPreference) findPreference("pref_server_ip");
        this.etpIp.setOnPreferenceChangeListener(this);
        this.etpPort = (EditTextPreference) findPreference("pref_server_port");
        this.etpPort.setOnPreferenceChangeListener(this);
    }
        
    /**
     * On tree click, go to home. This needs to be overwritten but we just
     * call parent to do nothing.
     */
    // @Override
    public boolean onPreferenceTreeClick(PreferenceScreen ps, Preference p) 
    {
        return super.onPreferenceTreeClick(ps, p);
    }

    /**
     * Every time the shared preference is changed, change the summary values
     * of the preference. This helps represent the current value well
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

    /**
     * Run all the validation when the preferences are changed. Such as ensure
     * that the IP address is valid, also ensure that the port value is valid
     *
     * @param preferences The preferences object
     * @param newValue The actual value that was set
     *
     * @return TRUE if the value is valid, other wise return false
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) 
    {
        String value = newValue.toString();

        // If the key is IP address and in invalid
        if(preference.equals(etpIp) && !this.validIpAddress(value))
        {
            toastError("The IP address '" + value + "' is invalid!");
            return false;
        }
        
        // The port KEY should also be valid
        if(preference.equals(etpPort) && !this.validPort(value))
        {
            toastError("A valid port is between 1500-3000 range and must " + 
                    "be numeric");
            return false;
        }

        return true;
    }

    /**
     * Check to see if the ip address is valid. The most easiest way to do this
     * is to parse the address through InetAddress.getByName, if this throws
     * an exception than the ip address is wrong
     *
     * @param ipAddress the address to check
     *
     * @return true for valid and false otherwise
     */
    private boolean validIpAddress(String ipAddress)
    {
        try
        {
            java.net.InetAddress.getByName(ipAddress);
            return true;
        }
        catch (java.net.UnknownHostException e) {}
        catch (android.os.NetworkOnMainThreadException e) {}

        return false;
    }

    /**
     * Valid the PORT number
     *
     * @param port The port as string
     *
     * @return True if port is valid and is within the range
     */
    private boolean validPort(String port)
    {
        try 
        { 
            int temp = Integer.parseInt(port);

            if (temp < 1500 || temp > 3000)
                return false;
        } 
        catch(NumberFormatException e) 
        { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }

    /**
     * A short helper for our Toasting when on error
     *
     * @param message The message to toast
     */
    private void toastError(String message)
    {
        Toast toast = Toast.makeText(
            this.getActivity(),
            message,
            Toast.LENGTH_LONG
        );
        toast.show();
    }
}
