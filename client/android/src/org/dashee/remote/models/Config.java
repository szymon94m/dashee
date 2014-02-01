package org.dashee.remote.models;

import java.net.InetAddress;

import org.dashee.remote.exception.OutOfRange;

/**
 * This class holds all the values associated with our application.
 *
 * It is helpful to have a class which can also validate things such as IP 
 * addresses before they are set application wide, and as an added bonus we can 
 * pass this class around to others so they can use the values required
 *
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class Config
{
    /**
     * The server port.
     */
    private int port = 2047;
    
    /**
     * This is the server's IP Address.
     */
    private InetAddress ip;

    /**
     * Initialise our variables
     */
    public Config(String ipAddress) throws java.net.UnknownHostException
    {
        this.setIp(ipAddress);
    }
    
    /**
     * Set the Controls port value.
     *
     * @param port A port greater than 1500 and less than 3000
     */
    public void setPort(int port)
    {
        if (port < 1500 || port > 3000)
            throw new OutOfRange("Port value must be between 1500-3000");

        this.port = port;
    }
    
    /**
     * Get the port.
     *
     * @return The port number
     */ 
    public int getPort()
    {
        return this.port;
    }
    
    /**
     * Change the IP address
     *
     * @param ip The IP address value in string
     */
    public void setIp(String ip) throws java.net.UnknownHostException
    {
        this.ip = java.net.InetAddress.getByName(ip);
    }
    
    /**
     * Get the IP string
     *
     * @return The IP address
     */
    public InetAddress getIp()
    {
        return this.ip;
    }
}
