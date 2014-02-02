package org.dashee.remote.fragment;

import android.support.v4.app.Fragment;

/**
 * FragmentHud abstract class. Create the functions
 * that mainActivity can refer to without using this class
 *
 * @author David Buttar
 * @author Shahmir Javaid
 */
public abstract class Hud 
    extends Fragment
{
    /**
     * Instance which holds our vehicle.
     */
    protected org.dashee.remote.model.Vehicle vehicle;
    
    /**
     *
     * @param vehicle The vehicle value to initiate
     */
    public Hud()
    {
    }

    /**
     * Pause our thread
     */
    public void onPause()
    {
        super.onPause();
    }
    
    /**
     * Resume our thread
     */
    public void onResume()
    {
        super.onResume();
    }

    /**
     * Set the IP of HUD
     *
     * @param ip - The IP Address
     */
    public abstract void setHudIp(String ip);

    /**
     * Set the Bytes per second.
     *
     * @param bps - The Bytes per seconds
     */
    public abstract void setHudBps(int bps);

    /**
     * Set the Connection Status
     *
     * @param connection - The Connection string
     */
    public abstract void setHudConnection(String connection);

    /**
     * Assign a reference of vehicle model to the hud
     *
     * @param vehicle The vehicle object
     */
    public void setVehicle(org.dashee.remote.model.Vehicle vehicle)
    {
        this.vehicle = vehicle;
    }
}
