package org.dashee.remote.threads;

import android.util.Log;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.dashee.remote.models.ModelServerState;
import org.dashee.remote.models.ModelVehicle;

/**
 * Thread to communicate to the server.
 * This will send position when the position is changed and 
 * communicate with the server when the position is the same, so
 * the server can know that we are still alive
 *
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class ThreadPassPositionControls extends Thread 
{
    /**
     * DataGram object to send commnds over UDP
     */
    private DatagramSocket sockHandler;

    private long resetTime = 0; // Time when last value was set
    private long currentTime;


    /**
     * Track packet count
     */
    public int packetCount;

    /**
     * Locking objects.
     * lockPause is used to make the run thread wait and
     * a wrapper to change the pause variable
     * lockPosition is used to lock when getting/setting position
     */
    private Object lockPause = new Object();

    /**
     *  Variable controlling the pause state of this thread.
     *  When the onPause function is called this is changed to true
     *  which in turn will call wait on lockPause.
     */
    private boolean pause = false;
    
    /**
     * Variable controlling the execution of a thread.
     * The run() function runs constantly, so exit variable
     * controls the infinite loop
     */
    private boolean exit = false;

    /**
     * Holds the last time the Bps command was reset. This will help change
     * our view to determine how many bytes have been sent over a second
     * Every time the view is set, the values of this is reset to current time
     */
    //private long timeLastBpsReset = 0;

    /**
     * Hold the value of bytes sent. It will be reset to 0 after every
     * second.
     */
    private int packetsPerSec = 0;
    
    /**
     * Handle to our ModelServerState, to get port and other values.
     */
    private ModelServerState modelServerState;
    
    /**
     * Current vehicle in use.
     */
    private ModelVehicle modelVehicle;

    /*
     * Current list of commands
     */
    List<Byte> commands;


    /**
     * Initiate our thread. Set the variables from the params, and 
     * set our ipAdress object. Also create a new instance of socket
     *
     * @param modelServerState - Set our pointer to the modelServerState
     * @param modelVehicle - Set our variable reference to the modelVehicle
     */
    public ThreadPassPositionControls(ModelServerState modelServerState, ModelVehicle modelVehicle)
    {
        super();
        try
        {
            this.modelVehicle = modelVehicle;
            this.modelServerState = modelServerState;
            //this.timeLastBpsReset = System.currentTimeMillis();
            this.sockHandler = new DatagramSocket();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    public void setBps(int bps)
    {
        if (bps < 0) bps = 0;

        this.packetsPerSec = bps;
    }

    public int getBps()
    {
        return this.packetsPerSec;
    }
    
    /**
     * Set the position. If a position presented is different than
     * the previous position, notify our server over UDP. If we stop sending 
     * signals to our servers the server will revert to fallback mode, to prevent
     * this we periodically send the same signal back to the server to tell it we
     * are still alive.
     *
     * Also if the Thread is paused, our lockPause object will wait. We know the 
     * thread is paused if the @pause variable is true
     */
    public void run() 
    {   
        while(!exit)
        {
                this.sendCommands();

            // We are in lock state, so sent the thread to wait
            // which can be then woken up by a notify
            synchronized (lockPause) 
            {
                while (pause) 
                {
                    try 
                    {
                        lockPause.wait();
                    }

                    catch (InterruptedException e) 
                    {
                    }
                }
            }
        }
    }
    
    /**
     * Send any available control updates.
     */
    private void sendCommands()
    {
    	commands =  this.modelVehicle.getCommands();
        if(commands.isEmpty()) return;

    	byte[] tempArray = new byte[commands.size()];

        for(int i = 0; i < commands.size(); i++) {
          tempArray[i] = commands.get(i);
        }

        this.sendCommandBytes(tempArray);
    }

    /**
     * Passes dashee server protocol commands to the server.
     */
    private void sendCommandBytes(byte[] command)
    {
        try
        {
            this.currentTime = System.currentTimeMillis();
            if(this.currentTime - this.resetTime > 1000){
                this.resetTime = System.currentTimeMillis();
                //Log.d("Dashee", "Packets Per Second " + this.packetCount);
                this.packetCount = 0;
            }

            DatagramPacket packet = new DatagramPacket(
                command,
                command.length,
                this.modelServerState.getIp(),
                this.modelServerState.getControlPort()
            );
            this.packetCount++;
            this.sockHandler.send(packet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Change the pause flag. When we pause
     * the flag will prevent call lockPause.wait();
     * see run for this behaviour
     */
    public void onPause()
    {
        synchronized (lockPause)
        {
            pause = true;
        }
    }

    /**
     * Thread is resumed. Change the pause flag
     * and notify all waiting Objects
     */
    public void onResume() 
    {
        synchronized (lockPause) 
        {
            pause = false;
            lockPause.notifyAll();
        }
    }
}
