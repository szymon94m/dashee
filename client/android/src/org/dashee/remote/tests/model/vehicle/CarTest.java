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
}
