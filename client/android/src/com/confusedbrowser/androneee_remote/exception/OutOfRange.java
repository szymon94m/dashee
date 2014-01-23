package com.confusedbrowser.androneee_remote.exception;

/**
 * Exception class indicating Invalid Values
 *
 * @author Shahmir Javaid
 */
public class OutOfRange extends RuntimeException
{
    /**
     * Call extended version
     *
     * @param message
     */
    public OutOfRange(String message)
    {
        super(message);
    }

    /**
     * Call extended version
     *
     * @param message
     * @param throwable
     */
    public OutOfRange(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}

