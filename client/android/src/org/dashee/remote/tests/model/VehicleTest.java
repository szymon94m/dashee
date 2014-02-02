package org.dashee.remote.test.model;

import junit.framework.TestCase;
import org.dashee.remote.model.Vehicle;
import org.dashee.remote.exception.OutOfRange;

abstract public class VehicleTest
    extends TestCase
{
    /**
     * The created instance of our vehicle.
     */
    protected Vehicle vehicle;

    /**
     * New instance of our Car variable
     */
    public void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Test the set and get values of the pitch
     */
    public void testSetAndGetPitch()
    {
        assertEquals(this.vehicle.getPitch(), 128);

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setPitch(x);
            assertEquals(this.vehicle.getPitch(), x);
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setPitch(300);
            fail("Pitch should be less than 255");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Pitch");
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setPitch(-300);
            fail("Pitch should be greater or equal to 0");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Pitch");
        }
    }

    /**
     * Test the min and max values from pitch
     */
    public void testSetAndGetPitchWithMinAndMax()
    {
        this.vehicle.setPitchMin(128);
        assertEquals(this.vehicle.getPitchMin(), 128);
        this.vehicle.setPitchMax(130);
        assertEquals(this.vehicle.getPitchMax(), 130);

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setPitchMin(140);
            fail("Pitch min should not be more than max");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Min value must be less than max");
        }

        for (int x = 0; x < 255; x++)
        {
            this.vehicle.setPitch(x);

            if (x < 128)
                assertEquals(this.vehicle.getPitch(), 128);
            else if (x > 130)
                assertEquals(this.vehicle.getPitch(), 130);
            else 
                assertEquals(this.vehicle.getPitch(), x);
        }
    }

    /**
     * Test the value when the trim is set
     */
    public void testSetAndGetPitchWhenTrimmed()
    {
        this.vehicle.setPitchTrim(10);
        this.vehicle.setPitch(10);
        assertEquals(this.vehicle.getPitch(), 20);
        
        // Make sure any value greater than 255 after trim is calculated
        // as the max value
        this.vehicle.setPitch(250);
        assertEquals(this.vehicle.getPitch(), 255);

        // Test the negative value
        this.vehicle.setPitchTrim(-10);
        this.vehicle.setPitch(10);
        assertEquals(this.vehicle.getPitch(), 0);

        try
        {
            this.vehicle.setPitchTrim(-129);
            fail("Setting values less than -128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Pitch trim");
        }

        try
        {
            this.vehicle.setPitchTrim(129);
            fail("Setting values greater than 128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Pitch trim");
        }
    }

    /**
     * Test the pitch values when the world is inverted
     */ 
    public void testSetAndGetPitchWhenInverted()
    {
        assertFalse(this.vehicle.getPitchInverted());
        this.vehicle.setPitchInverted(true);
        assertTrue(this.vehicle.getPitchInverted());

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setPitch(x);
            assertEquals(this.vehicle.getPitch(), 255-x);
        }
    }

    /**
     * Test the set and get of roll
     */
    public void testSetAndGetRoll()
    {
        assertEquals(this.vehicle.getRoll(), 128);

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setRoll(x);
            assertEquals(this.vehicle.getRoll(), x);
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setRoll(300);
            fail("Roll should be less than 255");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Roll");
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setRoll(-300);
            fail("Roll should be greater or equal to 0");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Roll");
        }
    }

    /**
     * Test the min and max values from pitch
     */
    public void testSetAndGetRollWithMinAndMax()
    {
        this.vehicle.setRollMin(128);
        assertEquals(this.vehicle.getRollMin(), 128);
        this.vehicle.setRollMax(130);
        assertEquals(this.vehicle.getRollMax(), 130);

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setRollMin(140);
            fail("Roll min should not be more than max");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Min value must be less than max");
        }

        for (int x = 0; x < 255; x++)
        {
            this.vehicle.setRoll(x);

            if (x < 128)
                assertEquals(this.vehicle.getRoll(), 128);
            else if (x > 130)
                assertEquals(this.vehicle.getRoll(), 130);
            else 
                assertEquals(this.vehicle.getRoll(), x);
        }
    }

    /**
     * Test the value when the trim is set
     */
    public void testSetAndGetRollWhenTrimmed()
    {
        this.vehicle.setRollTrim(10);
        this.vehicle.setRoll(10);
        assertEquals(this.vehicle.getRoll(), 20);
        
        // Make sure any value greater than 255 after trim is calculated
        // as the max value
        this.vehicle.setRoll(250);
        assertEquals(this.vehicle.getRoll(), 255);

        // Test the negative value
        this.vehicle.setRollTrim(-10);
        this.vehicle.setRoll(10);
        assertEquals(this.vehicle.getRoll(), 0);

        try
        {
            this.vehicle.setRollTrim(-129);
            fail("Setting values less than -128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Roll trim");
        }

        try
        {
            this.vehicle.setRollTrim(129);
            fail("Setting values greater than 128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Roll trim");
        }
    }

    /**
     * Test the pitch values when the world is inverted
     */ 
    public void testSetAndGetRollWhenInverted()
    {
        assertFalse(this.vehicle.getRollInverted());
        this.vehicle.setRollInverted(true);
        assertTrue(this.vehicle.getRollInverted());

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setRoll(x);
            assertEquals(this.vehicle.getRoll(), 255-x);
        }
    }
    
    /**
     * Test the set and get of yaw
     */
    public void testSetAndGetYaw()
    {
        assertEquals(this.vehicle.getYaw(), 128);

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setYaw(x);
            assertEquals(this.vehicle.getYaw(), x);
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setYaw(300);
            fail("Yaw should be less than 255");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Yaw");
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setYaw(-300);
            fail("Yaw should be greater or equal to 0");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Yaw");
        }
    }

    /**
     * Test the min and max values from pitch
     */
    public void testSetAndGetYawWithMinAndMax()
    {
        this.vehicle.setYawMin(128);
        assertEquals(this.vehicle.getYawMin(), 128);
        this.vehicle.setYawMax(130);
        assertEquals(this.vehicle.getYawMax(), 130);

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setYawMin(140);
            fail("Yaw min should not be more than max");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Min value must be less than max");
        }

        for (int x = 0; x < 255; x++)
        {
            this.vehicle.setYaw(x);

            if (x < 128)
                assertEquals(this.vehicle.getYaw(), 128);
            else if (x > 130)
                assertEquals(this.vehicle.getYaw(), 130);
            else 
                assertEquals(this.vehicle.getYaw(), x);
        }
    }

    /**
     * Test the value when the trim is set
     */
    public void testSetAndGetYawWhenTrimmed()
    {
        this.vehicle.setYawTrim(10);
        this.vehicle.setYaw(10);
        assertEquals(this.vehicle.getYaw(), 20);
        
        // Make sure any value greater than 255 after trim is calculated
        // as the max value
        this.vehicle.setYaw(250);
        assertEquals(this.vehicle.getYaw(), 255);

        // Test the negative value
        this.vehicle.setYawTrim(-10);
        this.vehicle.setYaw(10);
        assertEquals(this.vehicle.getYaw(), 0);

        try
        {
            this.vehicle.setYawTrim(-129);
            fail("Setting values less than -128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Yaw trim");
        }

        try
        {
            this.vehicle.setYawTrim(129);
            fail("Setting values greater than 128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Yaw trim");
        }
    }

    /**
     * Test the pitch values when the world is inverted
     */ 
    public void testSetAndGetYawWhenInverted()
    {
        assertFalse(this.vehicle.getYawInverted());
        this.vehicle.setYawInverted(true);
        assertTrue(this.vehicle.getYawInverted());

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setYaw(x);
            assertEquals(this.vehicle.getYaw(), 255-x);
        }
    }
    
    /**
     * Test the set and get of throttle
     */
    public void testSetAndGetThrottle()
    {
        assertEquals(this.vehicle.getThrottle(), 0);

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setThrottle(x);
            assertEquals(this.vehicle.getThrottle(), x);
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setThrottle(300);
            fail("Throttle should be less than 255");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Throttle");
        }

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setThrottle(-300);
            fail("Throttle should be greater or equal to 0");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Invalid range of Throttle");
        }
    }

    /**
     * Test the min and max values from pitch
     */
    public void testSetAndGetThrottleWithMinAndMax()
    {
        this.vehicle.setThrottleMin(128);
        assertEquals(this.vehicle.getThrottleMin(), 128);
        this.vehicle.setThrottleMax(130);
        assertEquals(this.vehicle.getThrottleMax(), 130);

        // Try setting the min to more than max
        try 
        {
            this.vehicle.setThrottleMin(140);
            fail("Throttle min should not be more than max");
        }
        catch (OutOfRange e)
        {
            assertSame(e.getMessage(), "Min value must be less than max");
        }

        for (int x = 0; x < 255; x++)
        {
            this.vehicle.setThrottle(x);

            if (x < 128)
                assertEquals(this.vehicle.getThrottle(), 128);
            else if (x > 130)
                assertEquals(this.vehicle.getThrottle(), 130);
            else 
                assertEquals(this.vehicle.getThrottle(), x);
        }
    }

    /**
     * Test the value when the trim is set
     */
    public void testSetAndGetThrottleWhenTrimmed()
    {
        this.vehicle.setThrottleTrim(10);
        this.vehicle.setThrottle(10);
        assertEquals(this.vehicle.getThrottle(), 20);
        
        // Make sure any value greater than 255 after trim is calculated
        // as the max value
        this.vehicle.setThrottle(250);
        assertEquals(this.vehicle.getThrottle(), 255);

        // Test the negative value
        this.vehicle.setThrottleTrim(-10);
        this.vehicle.setThrottle(10);
        assertEquals(this.vehicle.getThrottle(), 0);

        try
        {
            this.vehicle.setThrottleTrim(-129);
            fail("Setting values less than -128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Throttle trim");
        }

        try
        {
            this.vehicle.setThrottleTrim(129);
            fail("Setting values greater than 128 is invalid");
        }
        catch (OutOfRange ex)
        {
            assertSame(ex.getMessage(), "Invalid range value for Throttle trim");
        }
    }

    /**
     * Test the pitch values when the world is inverted
     */ 
    public void testSetAndGetThrottleWhenInverted()
    {
        assertFalse(this.vehicle.getThrottleInverted());
        this.vehicle.setThrottleInverted(true);
        assertTrue(this.vehicle.getThrottleInverted());

        for (int x = 0; x < 255; ++x)
        {
            this.vehicle.setThrottle(x);
            assertEquals(this.vehicle.getThrottle(), 255-x);
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
