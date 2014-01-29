package org.dashee.remote.models;
import org.dashee.remote.exception.*;

/**
 * This class holds all the values used for all vehicles.
 */
public abstract class Vehicle
{
    /**
     * Main flight control sets.
     */
    private int pitch = 0;
    private int roll = 0;
    private int yaw = 0;
    private int throttle = 0;
    
    /**
     * The max value for flight controls
     */
    private int pitchMin = 0;
    private int rollMin = 0;
    private int yawMin = 0;
    private int throttleMin = 0;

    /**
     * The max value for flight controls
     */
    private int pitchMax = 255;
    private int rollMax = 255;
    private int yawMax = 255;
    private int throttleMax = 255;
 
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
     * Return the value of Pitch
     *
     * @return the value of pitch
     */
    public int getPitch()
    {
        // Return the min/max values if the actual value is out of range
        if (this.pitch <= this.pitchMin)
           return this.pitchMin;
        else if (this.pitch >= this.pitchMax)
            return this.pitchMax;

        return this.pitch;
    }

    /**
     * Set the min value of pitch.
     *
     * @param value The value to set it to
     */
    public void setPitchMin(int value)
    {
        if (value >= this.pitchMax)
            throw new OutOfRange("Min value must be less than max");

        this.pitchMin = value;
    }

    /**
     * Set the max value of pitch.
     *
     * @param value The value to set it to
     */
    public void setPitchMax(int value)
    {
        if (value <= this.pitchMin)
            throw new OutOfRange("Max value must be less than min");

        this.pitchMax = value;
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
     * Return the value of Roll
     *
     * @return the value of roll
     */
    public int getRoll()
    {
        return this.roll;
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
     * Return the value of Yaw
     *
     * @return the value of yaw
     */
    public int getYaw()
    {
        return this.yaw;
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

    /**
     * Return the value of Throttle
     *
     * @return the value of throttle
     */
    public int getThrottle()
    {
        return this.throttle;
    }
}
