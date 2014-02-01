package org.dashee.remote.threads;

import java.net.*;

import org.dashee.remote.models.ModelServerState;
import org.dashee.remote.models.Vehicle;

/**
 * Thread to communicate to the server.
 * This will send position when the position is changed and 
 * communicate with the server when the position is the same, so
 * the server can know that we are still alive
 *
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class SendCommands extends Thread 
{
    
    static final int TICK_PER_BYTE = 1000;

    /**
     * DataGram object to send commands over UDP
     */
    private DatagramSocket sockHandler;

    /**
     * Time when last value was sent.
     */
    private long resetTime = 0;
    private long currentTime = 0;
    
    /**
     * Handle to our ModelServerState, to get port and other values.
     */
    private ModelServerState modelServerState;
    
    /**
     * Current vehicle in use.
     */
    private Vehicle vehicle;

    /**
     * Initiate our thread. Set the variables from the params, and 
     * set our ipAdress object. Also create a new instance of socket
     *
     * @param modelServerState - Set our pointer to the modelServerState
     * @param modelVehicle - Set our variable reference to the modelVehicle
     */
    public SendCommands(
            ModelServerState modelServerState, 
            Vehicle vehicle
        )
    {
        super();
        try
        {
            this.vehicle = vehicle;
            this.modelServerState = modelServerState;
            this.sockHandler = new DatagramSocket();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Set the position. If a position presented is different than
     * the previous position, notify our server over UDP. If we stop sending 
     * signals to our servers the server will revert to fallback mode, to 
     * prevent this we periodically send the same signal back to the server to 
     * tell it we are still alive.
     *
     * Also if the Thread is paused, our lockPause object will wait. We know the 
     * thread is paused if the @pause variable is true
     */
    public void run()
    {   
        while(true)
        {
            try
            {
                // Skip if we haven't ticked by sleeping and continuing
                if (!haveWeTicked())
                {
                    Thread.sleep(TICK_PER_BYTE);
                    continue;
                }

                // Create a new set of bytes
                byte[] ar = new byte[3];
                ar[0] = 0;
                ar[1] = (byte)vehicle.getYaw();
                ar[2] = (byte)vehicle.getThrottle();

                // Send the commands to th server
                this.sendCommandBytes(ar);
            }
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Variable reprsenting weathre or not we have ticked. If we have, reset the
     * times and return true
     *
     * @return boolean representing weather we have ticked or not
     */
    private boolean haveWeTicked()    
    {
        // Only send a packet every Nth second
        this.currentTime = System.currentTimeMillis();
        if(this.currentTime - this.resetTime < TICK_PER_BYTE)
            return false;
        
        this.resetTime = System.currentTimeMillis();
        return true;
    }
    
    /**
     * Send the bytes to the server. Each packet is sent after Nth milisecond 
     * controlled by our static TICK_PER_BYTE
     *
     * @param command the set of commands to send
     */
    private void sendCommandBytes(byte[] command)
    {
        try
        {
            DatagramPacket packet = new DatagramPacket(
                command,
                command.length,
                this.modelServerState.getIp(),
                this.modelServerState.getControlPort()
            );

            this.sockHandler.send(packet);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
