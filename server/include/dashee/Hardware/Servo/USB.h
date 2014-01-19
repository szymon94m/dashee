/**
 * @file include/dashee/Hardware/Servo/USB.h
 *
 * @section LICENSE
 *
 * This file is licensed under the terms and condition
 * mentioned at http://dashee.co.uk/license.
 */

#ifndef DASHEE_HARDWARE_SERVO_USB_H_
#define DASHEE_HARDWARE_SERVO_USB_H_

#include <dashee/Hardware/Servo.h>

namespace dashee
{
    namespace Hardware
    {
        class ServoUSB;
    }
}

/**
 * Servo USB.
 *
 * This is a class which handles communications with the Pololu board it is a 
 * wrapper over the read and write commands sent to usually `/dev/ttyACM0`
 *
 * This class represents one servo/channel at a time, The handle is held 
 * externally usually in the ServoController class, or its 
 * ServoController<CounterPart> version
 */
class dashee::Hardware::ServoUSB : public dashee::Hardware::Servo
{
private:

protected:
    
    /** 
     * File Handle.
     *
     * The pointer of the file descriptor held by ServoController,
     * we use this to actually read and write to the device
     */
    int * fd;
    
public:
    //Open our Servo Device
    explicit ServoUSB(int * fd, const unsigned short int channel);

    //Set the target of a given channel
    void setTarget(unsigned short int);
    unsigned short int getTarget(const bool fromcache = false);

    
    //Close the device
    ~ServoUSB();
};

#endif
