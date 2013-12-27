/**
 * @file include/dashee/GPIO.h
 *
 * @section LICENSE
 *
 * This file is licensed under the terms and condition
 * mentioned at http://dashee.co.uk/license. 
 */

#ifndef DASHEE_GPIO_H_
#define DASHEE_GPIO_H_

#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include <dashee/common.h>
#include <dashee/Exception/GPIO.h>

namespace dashee
{
    class GPIO;
}

/**
 * GPIO class.
 *
 * This class is designed to run GPIO functions, each instance of the class 
 * represents each instance of used pin
 *
 * An instance will export a GPIO ping and on close unexport the value, You can 
 * change directions of the given pin and read/write to the pin
 *
 * This class implements most of its main functions using statics, the initiated
 * version of this class calls these static functions passing its pin through
 *
 * It is better to use this class as a initiated version. As it will handle
 * exporting and unexporting. But if you wish to control the handling of the 
 * export/unexport and then read or write you can do so using the static 
 * functions directly
 */
class dashee::GPIO
{

private:
    
    /**
     * This holds the pin value used by this
     * instance of the class
     */
    unsigned short pin;

protected:

    // Function to export/unexport the pin
    void exportPin();
    void unexportPin();

    // Pin setter
    void setPin(unsigned short int pinNumber);

public:

    /** 
     * Constant values used for 
     * High and Low definitions
     */
    static const unsigned short HIGH = 1;
    static const unsigned short LOW = 0;

    /**
     * Constant to represent the In or Out
     * direction of the GPIO Pin
     */
    static const char IN = 'i';
    static const char OUT = 'o';
    
    // Return the current pin we are working on to the user
    unsigned short int getPin();

    // Build and destroy
    GPIO(unsigned short int pin, char direction);
    ~GPIO();
    
    // Un/Export Pin as statics
    static void exportPin(int pin);
    static void unexportPin(int pin);

    // Direction setter
    void setDirection(char direction);
    char getDirection();
    static void setDirection(int pin, char direction);
    static char getDirection(int pin);

    // read/write the GPIO Pin
    void write(unsigned short int value);
    int read();
    static void write(int pin, unsigned short int value);
    static int read(int pin);

    // write high/low to the GPIO Pin
    void high();
    void low();
};

#endif
