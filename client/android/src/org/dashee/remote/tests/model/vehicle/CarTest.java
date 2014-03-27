package org.dashee.remote.test.model.vehicle;

import junit.framework.TestCase;
import org.dashee.remote.model.vehicle.Car;
import org.dashee.remote.exception.OutOfRange;

import org.dashee.remote.test.model.VehicleTest;

public class CarTest
    extends VehicleTest
{
    /**
     * New instance of our Car variable
     */
    public void setUp() throws Exception
    {
        super.vehicle = new Car();
        super.setUp();
    }
    
    /**
     * Test the set and get of throttle
     */
    @Override
    public void testSetAndGetThrottle()
    {
        assertEquals(this.vehicle.getThrottle(), 128);
        this.vehicle.setThrottle(0);
        super.testSetAndGetThrottle();
    }

    public void testSetThrottleMaxPerc()
    {
        this.vehicle.setThrottleMaxPerc(50);

        for (int x = 128; x < 255; x++)
        {
            this.vehicle.setThrottle(x);

            if (x < 192) { assertEquals(this.vehicle.getThrottle(), x); }
            else { assertEquals(this.vehicle.getThrottle(), 191); }
        }
    }
}
