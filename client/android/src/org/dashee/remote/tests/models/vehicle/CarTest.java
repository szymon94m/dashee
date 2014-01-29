package org.dashee.remote.test.models.vehicle;

import junit.framework.TestCase;
import org.dashee.remote.models.Vehicle;
import org.dashee.remote.models.vehicle.Car;
import org.dashee.remote.exception.OutOfRange;
import static java.lang.System.out;

public class CarTest
    extends TestCase
{
    /**
     * The created instance of our vehicle.
     */
    private Vehicle vehicle;

    /**
     * New instance of our Car variable
     */
    public void setUp() throws Exception
    {
        super.setUp();
        this.vehicle = new Car();
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
        this.vehicle.setPitchMax(130);

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
            
            /*
            if (x < 128)
                assertEquals(this.vehicle.getPitch(), 128);
            else if (x > 130)
                assertEquals(this.vehicle.getPitch(), 130);
            else 
                assertEquals(this.vehicle.getPitch(), x);
                */

            this.vehicle.setPitchMin(0);
            this.vehicle.setPitchMax(255);
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
     * Clean up our class
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
