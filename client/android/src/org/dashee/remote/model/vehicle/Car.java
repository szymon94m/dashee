package org.dashee.remote.model.vehicle;

import org.dashee.remote.RangeMapping;
import org.dashee.remote.model.Vehicle;

public class Car extends Vehicle
{
    public Car()
    {
        super();
        this.setPitch(128);
        this.setRoll(128);
        this.setYaw(128);
        this.setThrottle(128);
    }

    /**
     * Set the maximum throttle percentage. Note for car the acceleration goes 
     * from 128 to 255
     *
     * @param value The value of the percentage
     */
    public void setThrottleMaxPerc(int value)
    {
        this.setThrottleMax(
            Math.round(RangeMapping.mapValue(value, 1, 100, 128, 255))
        );
    }
}
