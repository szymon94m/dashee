package org.dashee.remote.model;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;

import java.util.Observable;

/**
 * This Class allows monitoring of various user phone movements.
 * yaw, pitch and roll are calculated and updated to the observer
 *
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class PhoneSensors
    extends Observable 
    implements SensorEventListener 
{
    /**
     * required Sensors.
     */
    private SensorManager sensorManager;

    /**
     * The sensor type ROTATION_VECTOR
     */ 
    private Sensor rotationSensor;

    /**
     * Holds the rotation Matrix array of floats.
     * Used by the onSensorChanged to update our orientation
     */
    private float[] rotationMatrix = new float[16];

    /**
     * An array of orientation of the phone. Updated by the
     * onSensorChanged event, holds yaw, pitch and roll
     */
    private float[] orientation = new float[4];
    
    /**
     * Initialise our variables
     *
     * @param The context in which to set the sensor in
     */
    public PhoneSensors(Context cont)
    {
        this.sensorManager 
            = (SensorManager)cont.getSystemService(Context.SENSOR_SERVICE);
        this.rotationSensor 
            = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }
    
    /**
     * Return current Pitch value
     * 
     * @return The pitch value in radians
     */
    public float getPitch()
    {
        return this.orientation[2];
    }
    
    /**
     * Return current Roll value
     * 
     * @return The roll value in radians
     */
    public float getRoll()
    {
        return this.orientation[1];
    }
    
    /**
     * Return current Yaw value
     * 
     * @return The yaw value in Radians
     */
    public float getYaw()
    {
        return this.orientation[0];
    }
    
    /**
     * When the accuracy is changed, Perform some actions
     *
     * @param sensor The sensor in question
     * @param accuracy The accuracy value
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) 
    {
    }

    /**
     * Resume the sensor.
     */
    public void onResume()
    {
        sensorManager.registerListener(
            this,
            rotationSensor,
            //Important, use the highest value possible
            SensorManager.SENSOR_DELAY_FASTEST 
        );
    }
    
    /**
     * Pause our sensor.
     */
    public void onPause()
    {
        sensorManager.unregisterListener(this);
    }
    
    /**
     * When ever our sensor is changed notify the Observer.
     *
     * This function will notifyObservers of the change of value
     * This object can then access the get function to determine new values
     *
     * @param event The sensor values when changed
     */
    @Override
    public void onSensorChanged(SensorEvent event) 
    {
        SensorManager.getRotationMatrixFromVector(
                this.rotationMatrix, 
                event.values
            );
        SensorManager.getOrientation(
                this.rotationMatrix, 
                this.orientation
            );

        setChanged();
        notifyObservers();
    }
}
