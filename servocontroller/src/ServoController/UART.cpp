#include "UART.h"

/**
 * The constructor of servo which takes in the device to open
 * usually the device is /dev/ttyAMA0 but this could be different going from system to system
 *
 * @param (const char *)dev - The name of the device which will be open
 * @throw Exception_Servo - If device opening fails, an exception will be thrown
 */
ServoController_UART::ServoController_UART(const char * dev, const unsigned short int channels) : ServoController(dev)
{
    // Reset the board
    this->reset();

    // Open the device
    this->fd = open(this->dev, O_RDWR | O_NOCTTY);
    if (this->fd == -1)
        throw Exception_ServoController();

    Log::info(5, "Device open with handler: %d", this->fd);
    
    // Initilize our UART
    this->init();
    
    // Create a servo class for each, servo channel that exists
    for (int x = 0; x < channels; x++)
        servos.push_back(new Servo_UART(&this->fd, x));
}

/**
 * This function simpley sets the BAUD rates between the serial devices
 * 
 * @throw Exception_ServoController - If tcsetattr fails
 */
void ServoController_UART::init()
{
    struct termios options;
    tcgetattr(this->fd, &options);
    cfsetispeed(&options, B230400);
    cfsetospeed(&options, B230400);

    options.c_cflag &= ~PARENB;
    options.c_cflag &= ~CSTOPB;
    options.c_cflag &= ~CSIZE;
    options.c_cflag |= CS8;

    // no flow control
    options.c_cflag &= ~CRTSCTS;

    options.c_cflag |= CREAD | CLOCAL;  // turn on READ & ignore ctrl lines
    options.c_iflag &= ~(IXON | IXOFF | IXANY); // turn off s/w flow ctrl

    options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG); // make raw
    options.c_oflag &= ~OPOST; // make raw

    // see: http://unixwiz.net/techtips/termios-vmin-vtime.html
    options.c_cc[VMIN]  = 0;
    options.c_cc[VTIME] = 10;

    if (tcsetattr(this->fd, TCSANOW, &options) < 0)
        throw Exception_ServoController("Initilizing UART failed");
    
    Log::info(5, "Initlized UART with BAUD 9600");
}

/** 
 * The Pololu board will only run once successfully, so
 * for the second iteration either the board must be hard reset or
 * the reset pin must be driven low. Once the pin is driven low, the board is
 * back to its original state
 */
void ServoController_UART::reset()
{

}

/**
 * The Pololu board provides a error handling, This function is designed to 
 * get the last error from the Pololy Maestro USB Servo board, Note on retriving 
 * the error, the error is reset. So it is always a good idea to periodicly
 * pole the board.
 *
 * For performance reason we allow the user to worry about errors at his/hers perfernce
 *
 * The response is returned in a two byte represented by char, Only one bit is always set in
 * these two bytes, The error number is represeted by the nth bit set, For example
 * 
 *  00010000|00000000 - Will suggest Errornumber 3, as the erronumbering starts from 0
 * 
 * @reuturn short int - The integer response
 */
short int ServoController_UART::getError()
{
    unsigned char command[] = { 0xAA, 0xC, 0x21 };
    unsigned char response[2];

    if (write(this->fd, command, sizeof(command)) == -1)
        throw Exception_ServoController("ServoController_UART::getError write failed");
        
    // Go through and read each byte by byte
    for (int n = 0, total = 0; n < 2; total++)
    {
        if (total > 10)
            throw Exception_ServoController("Reading ServoController_UART::getError, ran more than 10 times");

        int ec = read(this->fd, response+n, 1);
        if(ec < 0)
            throw Exception_ServoController("read failed in ServoController_UART::getError");

        if (ec == 0)
            continue;

        n++;
    }
    
    //TODO This needs to be fixed, its wrong at the moment
    return (short int)sqrt(response[0] + 256*response[1]);
}

/**
 * Handler to close our @fd opened device, and delete all servo's
 */
ServoController_UART::~ServoController_UART()
{
    close(this->fd);
}