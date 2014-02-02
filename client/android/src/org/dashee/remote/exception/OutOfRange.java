package org.dashee.remote.exception;

/**
 * Exception class indicating values are out of the given range
 *
 * @author Shahmir Javaid
 */
@SuppressWarnings("serial")
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

