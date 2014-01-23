package org.dashee.remote.models.vehicle;

import org.dashee.remote.exception.*;

/**
 * This class holds all the values used for all vehicles.
 */
public abstract class Vehicle
{
    /**
     * Constants used for Vehicle.
     */
    public static final float START_RANGE = 0.0f;
    public static final float MID_RANGE = 128.0f;
    public static final float END_RANGE = 255.0f;
    
    /**
     * Main flight control sets.
     */
    private int pitch;
    private int roll;
    private int yaw;
    private int throttle;
 
    /**
     * Set the value of pitch.
     *
     * @param value The pitch value to set
     *
     * @throws exception.OutOfRange
     */ 
    public void setPitch(int pitch)
    {
        if (pitch < 0 || pitch > 255)
            throw new OutOfRange("Invalid range of Pitch");

        this.pitch = pitch;
    }

    /**
     * Set the value of roll.
     *
     * @param value The roll value to set
     *
     * @throws exception.OutOfRange
     */ 
    public void setRoll(int roll)
    {
        if (roll < 0 || roll > 255)
            throw new OutOfRange("Invalid range of Roll");

        this.roll = roll;
    }
 
    /**
     * Set the value of yaw.
     *
     * @param value The pitch value to set
     *
     * @throws exception.OutOfRange
     */ 
    public void setYaw(int yaw)
    {
        if (yaw < 0 || yaw > 255)
            throw new OutOfRange("Invalid range of Yaw");

        this.yaw = yaw;
    }
 
    /**
     * Set the value of throttle.
     *
     * @param value The throttle value to set
     *
     * @throws exception.OutOfRange
     */ 
    public void setThrottle(int throttle)
    {
        if (throttle < 0 || throttle > 255)
            throw new OutOfRange("Invalid range of Throttle");

        this.throttle = throttle;
    }
}
