package org.dashee.remote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;
import java.net.UnknownHostException;
import java.util.Map;

import org.dashee.remote.exception.InvalidValue;
import org.dashee.remote.exception.OutOfRange;

import org.dashee.remote.model.Config;
import org.dashee.remote.model.Vehicle;
import org.dashee.remote.model.vehicle.Car;

/**
 * The main activity that the program will run.
 * This will set our fragments, handle our preferences changing, start the 
 * threads which communicate to our servers set and listen  to the Observers 
 * so actions can be taken when things are changed
 *
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class MainActivity 
    extends FragmentActivity 
    implements SeekBar.OnSeekBarChangeListener, 
               OnSharedPreferenceChangeListener
{
    /**
     * Create instances of our fragments in memory.
     * So they don't have to be initialised every time, and hold
     * there previous state.
     */
    private org.dashee.remote.fragment.Hud hud;
    
    /**
     * A list of running threads. Easy to contain them in a list as we
     * start them and leave them running.
     */
    private java.util.List<Thread> threads;
    
    /**
     * Hold the state of our Server. This will notify our
     * Observer, any time server values are changed
     */
    public Config config;

    /**
     * Current vehicle to control
     */
    private Vehicle vehicle;
    
    /**
     * SharedPrefrences object for registering
     */
    private SharedPreferences sharedPreferences;

    /**
     * The on create method to get the activity started.
     *
     * @param savedInstanceState The bundle from the main
     */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Keep our screen constantly on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Set the XML view for this activity
        setContentView(R.layout.activity_main);

        this.initModels();
        this.initFragments();
        this.initSettings();
        this.initThreads();
    }

    /**
     * Initialize our models. 
     *
     * Set the vehicle, position and the state models. 
     */
    private void initModels() 
    {
        try
        {
            //Create our ServerState model
            this.config = new Config("127.0.0.1");
            
            // Create our vehicle model
            this.vehicle = new Car();
        }
        catch (java.net.UnknownHostException e)
        {
            android.util.Log.e("dashee", "Failed to set IP Address");
            e.printStackTrace();
        }
    }

    /**
     * Initialize our fragments
     */
    private void initFragments()
    {
    	// Create our fragment views
        this.hud = new org.dashee.remote.fragment.hud.Car();
        this.hud.setVehicle(this.vehicle);
    	
        //Set the initial view to our HUD
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_content, this.hud);
        ft.commit();
    }

    /**
     * Initialize our threads. 
     *
     * Add them to our thread list which is used to hold
     * all threads so operations such as onPause and onResume can be performed
     * on all threads.
     */
    private void initThreads()
    {
        threads = new java.util.ArrayList<Thread>();

        // Initialise our thread
        threads.add(
            new org.dashee.remote.thread.SendCommands(
                this.config, 
                this.vehicle
            )
        );
        
        for (Thread t : threads) { t.start(); }
    }

    /**
     * Iterate through the settings and apply to our configuration.
     */
    private void initSettings()
    {
        this.sharedPreferences 
            = PreferenceManager.getDefaultSharedPreferences(this);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        for (
            Map.Entry<String, ?> entry : 
            this.sharedPreferences.getAll().entrySet()
        )
        {
            onSharedPreferenceChanged(this.sharedPreferences, entry.getKey());
        }
    }

    /**
     * Create a listener to activate when SharedPreferences are changed.
     *
     * @param prefs The SharedPreferences
     * @param key The key value changed
     */
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) 
    {
        try
        {
            if(key.equals("pref_server_ip"))
                this.config.setIp(
                    prefs.getString("pref_server_ip", "192.168.115")
                 );
            else if (key.equals("pref_server_port"))
                this.config.setPort(
                    Integer.parseInt(
                        prefs.getString("pref_server_port", "2047")
                    )
                );
            else if (key.equals("roll_min"))
                this.vehicle.setRollMin(
                        Math.round(
                            RangeMapping.mapValue(
                                prefs.getInt("roll_min", 0),
                                0,
                                100,
                                0,
                                255
                            )
                        )
                    );
            else if (key.equals("roll_max"))
                this.vehicle.setRollMax(
                        Math.round(
                            RangeMapping.mapValue(
                                prefs.getInt("roll_max", 0),
                                0,
                                100,
                                0,
                                255
                            )
                        )
                    );

            this.hud.setIp(this.config.getIp().toString().substring(1));
            this.hud.setRollMin(prefs.getInt("roll_min", 0));
            this.hud.setRollMax(prefs.getInt("roll_max", 100));
        }
        catch (UnknownHostException e)
        {
            Toast toast = Toast.makeText(
                getApplicationContext(), 
                e.getMessage(), 
                Toast.LENGTH_SHORT
            );
            toast.show();
        }
        catch(InvalidValue e)
        {
            Toast toast = Toast.makeText(
                getApplicationContext(), 
                e.getMessage(), 
                Toast.LENGTH_SHORT
            );
            toast.show();
        }
    }

    /**
     * Set our menu on the top bar. This will add the HUD, LOG and the ... icon
     * to the top of our menu bar
     *
     * @param menu - The menu object to load
     * @return boolean - Always true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Handle our menu button clicks. Given the menu item, 
     * either start an activity or change the fragment view
     *
     * @param item - The handler to the item selected
     *
     * @return boolean - true if clicked, or super.onOptionsItemSelected(item);
     */
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// Handle item selection
        switch (item.getItemId())
        {
            /*
            case R.id.action_dot_settings:
            {
                Intent preferencesActivity 
                    = new Intent(
                            getBaseContext(), 
                            org.dashee.remote.preference.PreferencesActivity
                                .class
                        );
                startActivity(preferencesActivity);
                return true;
            }
            */
            default:
                return super.onOptionsItemSelected(item);
        }        
    }

    /**
     * App is Resumed from a pause state.
     */
    @Override
    protected void onResume() 
    {
        super.onResume();
        //for (Thread t : this.threads) { t.onResume(); }
    }
    
    /**
     * App is paused, handle pause systems.
     */
    @Override
    protected void onPause() 
    {
        super.onPause();
        //for (Thread t : this.threads) { t.onPause(); }
    }

    /**
     * Stop everything. Time to go to bed
     */
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    

    @Override
    public void onStartTrackingTouch(SeekBar arg0) 
    {
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) 
    {
    }

    @Override
    public void onProgressChanged(
            SeekBar seekBar, 
            int progress, 
            boolean fromUser
        ) 
    {
    }
}
