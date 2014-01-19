/**
 * @file include/dashee/Hardware/ServoController/UART.h
 *
 * @section LICENSE
 *
 * This file is licensed under the terms and condition
 * mentioned at http://dashee.co.uk/license. 
 */

#ifndef DASHEE_HARDWARE_SERVOCONTROLLER_UART_H_
#define DASHEE_HARDWARE_SERVOCONTROLLER_UART_H_

#include <termios.h>

#include <dashee/GPIO.h>
#include <dashee/Hardware/ServoController.h>
#include <dashee/Hardware/Servo/UART.h>

namespace dashee
{
    namespace Hardware
    {
        class ServoControllerUART;
    }
}

/**
 * This is a class which handles communications with the Pololu board it is a 
 * wrapper over the read and write commands sent to usually `/dev/ttyAMA0`
 * 
 * The `/dev/ttyAMA0` is OS dependant, and be use a later Numerical value if the
 * OS already has a device running with the similar name
 *
 * The `/dev/ttyAMA0` represents the UART protocol, and the Pololu board is set 
 * to `UART detect baud rate`. However this means the device baud rate is set by
 * the constructor
 */
class dashee::Hardware::ServoControllerUART 
    : public dashee::Hardware::ServoController
{
private:

protected:
        
    /**
     * File Handle.
     *
     * Given our ServoController::dev variable we open this device, 
     * which returns the appropriate file handler.
     */
    int fd;
    
    // Reset our board by driving the reset pin low
    void reset();
    
    // Initialize our board
    void init();
    
public:
    // Open our device, and set our ServoController::servos array
    explicit ServoControllerUART(
            const char * dev, 
            const unsigned short int channels = 6
        );
    
    // Get the error from the board
    virtual short int getError() const;
    
    // Close the device
    virtual ~ServoControllerUART();
};

#endif
