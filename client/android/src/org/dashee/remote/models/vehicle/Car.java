package org.dashee.remote.models.vehicle;

import org.dashee.remote.models.Vehicle;

public class Car extends Vehicle
{
    public Car()
    {
        super();
        this.setPitch(128);
        this.setRoll(128);
        this.setYaw(128);
    }
}
