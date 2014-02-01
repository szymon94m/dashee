package org.dashee.remote.test.models;

import junit.framework.TestCase;
import org.dashee.remote.models.Config;
import org.dashee.remote.exception.OutOfRange;

public class ConfigTest
    extends TestCase
{
    /**
     * The created instance of our vehicle.
     */
    protected Config config;

    /**
     * New instance of our Car variable
     */
    public void setUp() throws Exception
    {
        super.setUp();
        this.config = new Config("127.0.0.1");
    }

    /**
     * Test setting and getting of the port value
     */
    public void testSetAndGetPort()
    {
        assertEquals(this.config.getPort(), 2047);
        
        for (int x = 1500; x <= 3000; ++x)
        {
            this.config.setPort(x);
            assertEquals(this.config.getPort(), x);
        }

        try
        {
            this.config.setPort(1499);
            this.fail("Port less than 1500 is invalid");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Port value must be between 1500-3000");
            assertEquals(this.config.getPort(), 3000);
        }
        
        try
        {
            this.config.setPort(3001);
            this.fail("Port more than 3001 is invalid");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Port value must be between 1500-3000");
            assertEquals(this.config.getPort(), 3000);
        }
    }

    /**
     * Test set and get the IP address
     */
    public void testSetAndGetIPAddress()
        throws java.net.UnknownHostException
    {
        assertEquals(this.config.getIp().getHostAddress(), "127.0.0.1");
        this.config.setIp("192.168.1.10");
        assertEquals(this.config.getIp().getHostAddress(), "192.168.1.10");

        try
        {
            this.config.setIp("1111.1.1.1.1");
            this.fail("Invalid IP");
        }
        catch (java.net.UnknownHostException e)
        {
            assertEquals(e.getMessage(), "Unable to resolve host \"1111.1.1.1.1\": No address associated with hostname");
            assertEquals(this.config.getIp().getHostAddress(), "192.168.1.10");
        }
    }

    /**
     * Clean up our class
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
