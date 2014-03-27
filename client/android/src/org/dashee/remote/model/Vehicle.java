package org.dashee.remote.model;

import org.dashee.remote.exception.OutOfRange;
import org.dashee.remote.RangeMapping;

/**
 * This class holds all the values used for all vehicles. A nice way to 
 * encapsulate the properties of pitch, roll, yaw and throttle. With an
 * added functionality to provide Trim, Min, Max and Invert behavior to
 * all of the flight controls.
 */
public abstract class Vehicle
{
    public static final int MAX = 255;
    public static final int MID = 128;
    public static final int MIN = 0;

    /**
     * Main flight control sets.
     */
    private int pitch = MIN;
    private int roll = MIN;
    private int yaw = MIN;
    private int throttle = MIN;
 
    /**
     * The trim values for the vehicle
     */    
    private int pitchTrim = MIN;
    private int rollTrim = MIN;
    private int yawTrim = MIN;
    private int throttleTrim = MIN;

    /**
     * The max value for flight controls
     */
    private int pitchMin = MIN;
    private int rollMin = MIN;
    private int yawMin = MIN;
    private int throttleMin = MIN;

    /**
     * The max value for flight controls
     */
    private int pitchMax = MAX;
    private int rollMax = MAX;
    private int yawMax = MAX;
    private int throttleMax = MAX;

    /**
     * Flag to determine the inversion of the flight controls.
     */
    private boolean pitchInverted = false;
    private boolean rollInverted = false;
    private boolean yawInverted = false;
    private boolean throttleInverted = false;
 
    /**
     * Set the value of pitch.
     *
     * @param value The pitch value to set
     *
     * @throws exception.OutOfRange
     */ 
    public void setPitch(int pitch)
    {
        if (pitch < MIN || pitch > MAX)
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
        int trimmed = this.pitch + this.pitchTrim;

        // Return the min/max values if the actual value is out of range
        if (trimmed < this.pitchMin)
           trimmed = this.pitchMin;
        else if (trimmed > this.pitchMax)
            trimmed = this.pitchMax;

        // Reverse the value when in invert
        if (pitchInverted)
            return MAX - trimmed;
        
        return trimmed;
    }

    /**
     * Set the pitch trim value.
     *
     * @param trim The value to set
     *
     * @throws OutOfRange if the trim value is not valid
     */
    public void setPitchTrim(int trim)
    {
        if (trim < -MID || trim > MID)
            throw new OutOfRange("Invalid range value for Pitch trim");

        this.pitchTrim = trim;
    }

    /**
     * Set the invert flag for pitch.
     *
     * @param inverted The value to set
     */
    public void setPitchInverted(boolean inverted)
    {
        this.pitchInverted = inverted;
    }

    /**
     * Get the pitch invert value.
     *
     * @return the value of the inverted flag 
     */
    public boolean getPitchInverted()
    {
        return this.pitchInverted;
    }

    /**
     * Set the min value of pitch.
     *
     * @param min The value to set it to
     * 
     * @throws OutOfRange if the min value is not valid
     */
    public void setPitchMin(int min)
    {
        if (min >= this.pitchMax)
            throw new OutOfRange("Min value must be less than max");

        this.pitchMin = min;
    }

    /**
     * Get the pitch min value
     *
     * @return the value of min pitch
     */
    public int getPitchMin()
    {
        return this.pitchMin;
    }

    /**
     * Set the max value of pitch.
     *
     * @param max The value to set it to
     * 
     * @throws OutOfRange if the max value is not valid
     */
    public void setPitchMax(int max)
    {
        if (max <= this.pitchMin)
            throw new OutOfRange("Max value must be less than min");

        this.pitchMax = max;
    }

    /**
     * Get the pitch max value
     *
     * @return the value of max pitch
     */
    public int getPitchMax()
    {
        return this.pitchMax;
    }

    /**
     * Set the value of roll.
     *
     * @param value The roll value to set
     *
     * @throws OutOfRange
     */ 
    public void setRoll(int roll)
    {
        if (roll < MIN || roll > MAX)
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
        int trimmed = this.roll + this.rollTrim;

        // Return the min/max values if the actual value is out of range
        if (trimmed < this.rollMin)
           trimmed = this.rollMin;
        else if (trimmed > this.rollMax)
            trimmed = this.rollMax;

        // Reverse the value when in invert
        if (rollInverted)
            return MAX - trimmed;
        
        return trimmed;
    }

    /**
     * Get the actual roll value
     *
     * @return roll
     */ 
    public int getActualRoll()
    {
        return this.roll;
    }

    /**
     * Set the roll trim value.
     *
     * @param trim The value to set
     *
     * @throws OutOfRange if the trim value is not valid
     */
    public void setRollTrim(int trim)
    {
        if (trim < -MID || trim > MID)
            throw new OutOfRange("Invalid range value for Roll trim");

        this.rollTrim = trim;
    }

    /**
     * Set the invert flag for roll.
     *
     * @param inverted The value to set
     */
    public void setRollInverted(boolean inverted)
    {
        this.rollInverted = inverted;
    }

    /**
     * Get the roll invert value.
     *
     * @return the value of the inverted flag 
     */
    public boolean getRollInverted()
    {
        return this.rollInverted;
    }

    /**
     * Set the min value of roll.
     *
     * @param min The value to set it to
     * 
     * @throws OutOfRange if the min value is not valid
     */
    public void setRollMin(int min)
    {
        if (min >= this.rollMax)
            throw new OutOfRange("Min value must be less than max");

        this.rollMin = min;
    }

    /**
     * Get the roll min value
     *
     * @return the value of min roll
     */
    public int getRollMin()
    {
        return this.rollMin;
    }

    /**
     * Set the max value of roll.
     *
     * @param max The value to set it to
     * 
     * @throws OutOfRange if the max value is not valid
     */
    public void setRollMax(int max)
    {
        if (max <= this.rollMin)
            throw new OutOfRange("Max value must be less than min");

        this.rollMax = max;
    }

    /**
     * Get the roll max value
     *
     * @return the value of max roll
     */
    public int getRollMax()
    {
        return this.rollMax;
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
        if (yaw < MIN || yaw > MAX)
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
        int trimmed = this.yaw + this.yawTrim;

        // Return the min/max values if the actual value is out of range
        if (trimmed < this.yawMin)
            trimmed = this.yawMin;
        else if (trimmed > this.yawMax)
            trimmed = this.yawMax;
        
        // Reverse the value when in invert
        if (yawInverted)
            return MAX - trimmed;
        
        return trimmed;
    }

    /**
     * Set the yaw trim value.
     *
     * @param trim The value to set
     *
     * @throws OutOfRange if the trim value is not valid
     */
    public void setYawTrim(int trim)
    {
        if (trim < -MID || trim > MID)
            throw new OutOfRange("Invalid range value for Yaw trim");

        this.yawTrim = trim;
    }
    
    /**
     * Set the invert flag for yaw.
     *
     * @param inverted The value to set
     */
    public void setYawInverted(boolean inverted)
    {
        this.yawInverted = inverted;
    }

    /**
     * Get the yaw invert value.
     *
     * @return the value of the inverted flag 
     */
    public boolean getYawInverted()
    {
        return this.yawInverted;
    }

    /**
     * Set the min value of yaw.
     *
     * @param min The value to set it to
     *
     * @throws OutOfRange if the min value is not valid
     */
    public void setYawMin(int min)
    {
        if (min >= this.yawMax)
            throw new OutOfRange("Min value must be less than max");

        this.yawMin = min;
    }

    /**
     * Get the yaw min value
     *
     * @return the value of min yaw
     */
    public int getYawMin()
    {
        return this.yawMin;
    }

    /**
     * Set the max value of yaw.
     *
     * @param max The value to set it to
     *
     * @throws OutOfRange if the max value is not valid
     */
    public void setYawMax(int max)
    {
        if (max <= this.yawMin)
            throw new OutOfRange("Max value must be less than min");

        this.yawMax = max;
    }

    /**
     * Get the yaw max value
     *
     * @return the value of max yaw
     */
    public int getYawMax()
    {
        return this.yawMax;
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
        if (throttle < MIN || throttle > MAX)
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
        int trimmed = this.throttle + this.throttleTrim;

        // Return the min/max values if the actual value is out of range
        if (trimmed < this.throttleMin)
            trimmed = this.throttleMin;
        else if (trimmed > this.throttleMax)
            trimmed = this.throttleMax;

        // Reverse the value when in invert
        if (throttleInverted)
            return MAX - trimmed;
        
        return trimmed;
    }

    /**
     * Return the value of throttle as set by user.
     *
     * @returns the actual value of throttle
     */ 
    public int getActualThrottle()
    {
        return this.throttle;
    }

    /**
     * Set the throttle trim value.
     *
     * @param trim The value to set
     *
     * @throws OutOfRange if the trim value is not valid
     */
    public void setThrottleTrim(int trim)
    {
        if (trim < -MID || trim > MID)
            throw new OutOfRange("Invalid range value for Throttle trim");

        this.throttleTrim = trim;
    }
    
    /**
     * Set the invert flag for throttle.
     *
     * @param inverted The value to set
     */
    public void setThrottleInverted(boolean inverted)
    {
        this.throttleInverted = inverted;
    }

    /**
     * Get the throttle invert value.
     *
     * @return the value of the inverted flag 
     */
    public boolean getThrottleInverted()
    {
        return this.throttleInverted;
    }

    /**
     * Set the min value of throttle.
     *
     * @param min The value to set it to
     *
     * @throws OutOfRange if the min value is not valid
     */
    public void setThrottleMin(int min)
    {
        if (min >= this.throttleMax)
            throw new OutOfRange("Min value must be less than max");

        this.throttleMin = min;
    }

    /**
     * Get the throttle min value
     *
     * @return the value of min throttle
     */
    public int getThrottleMin()
    {
        return this.throttleMin;
    }

    /**
     * Set the max value of throttle.
     *
     * @param max The value to set it to
     *
     * @throws OutOfRange if the max value is not valid
     */
    public void setThrottleMax(int max)
    {
        if (max <= this.throttleMin)
            throw new OutOfRange("Max value must be less than min");

        this.throttleMax = max;
    }

    /**
     * Set the throttle max as a percentage.
     *
     * @param value The value from 0 to 100%
     */
    public void setThrottleMaxPerc(int value)
    {
      //  if (value < 0.0)
      //      throw new OutOfR

        this.setThrottleMax(
            Math.round(RangeMapping.mapValue(value, 1, 100, MIN+1, MAX))
        );
    }

    /**
     * Get the throttle max value
     *
     * @return the value of max throttle
     */
    public int getThrottleMax()
    {
        return this.throttleMax;
    }
}
