/** 
 * This program is designed to connect to the servo controller using the COM port
 * which is usually defined by /dev/ttyACM0. It can then interface with this port 
 * by reading and writing to it. The read write protocol is defined by the POLOLU
 * board which we are using for this program
 *
 * The POLOLU Maestro board allows us to controll servo using the POLOLU protocol
 * This protocol is more clearly defined in Servo.h.
 *
 * Given the control of the device this program also handels a UDP server which allows
 * forign programs to interface with it, and control the POLOLU board using a network
 * protocol
 *
 * This server is not daemonized, as its mostly run in debug mode.
 *
 * This was developed for Linux and has never been tested in other operating systems
 *
 * @author Shahmir Javaid
 */

#include <stdlib.h>
#include <sstream>
#include <getopt.h> /* for getopts_long() */

#include "Servo/USB.h"
#include "Servo/Dummy.h"
#include "Server/UDP.h"
#include "Log.h"

#define SERVO_DEVICE "/dev/ttyACM0"
#define SERVER_PORT 2047
#define SERVER_TIMEOUT 4

/**
 * This is our main variable that controls wheather or not the program should be running
 * As soon as this variable is set to 1, all threads should start exiting gracefully, 
 * and pselect() would have by this time timed out
 */
int volatile EXIT = 0;

/**
 * A helpfull variable set by catch exceptions, which change the return value for the main
 * function
 */
int volatile RETVAL = 0;

/**
 * Our signal handler as overwritten by our main function
 */
void sighandler(int);

/**
 * This function will set our given options, See intilization for full details
 */ 
void setoptions(int, char **, unsigned int *, char **, unsigned int *);

/**
 * Our main function is designed to take in arguments from the command line
 * and run a UDP server. The UDP server provides a interface to the outside world
 * which can communicate and send commands to our Servo controller.
 *
 * @param (int)argc - The number of arguments
 * @param (char **)argv - The arguments array
 *
 * @return int - 0 on successfull shutdown and any other number for error.
 */
int main(int argc, char **argv)
{
    // Create a dummy Servo to be initiated later
    // Initialising to NULL is important otherwise you will seg fault
    Servo *s = NULL;
    
    // Set our sigaction
    struct sigaction act;
    act.sa_handler = sighandler;
    if (sigaction(SIGINT, &act, 0))
        throw new std::runtime_error("Sigaction failed");

    // Variables that will be overwritten by the user
    unsigned int servotype = 1;
    char * servo = (char *)SERVO_DEVICE;
    unsigned int port = SERVER_PORT;
    
    // Call our setoptions which will change the following functions
    setoptions(argc, argv, &servotype, &servo, &port);

    try
    {
        // Set our servo to the COM PORT
        if (servotype == 1)
        {
            // Create a Servo
            Log::info(1, "Loading device '%s'.", servo);
            s = new Servo_USB(servo);
        }

        // Set our servo to the Binary file
        else if(servotype == 2)
        {
            Log::info(1, "Loading device '%s'.", servo);
            s = new Servo_Dummy(servo);
        }

        // Fail as the options must fall within the above
        else
            Log::fatal("Servotype invalid");

        // Print and clear any Servo specific errors
        int error = s->getError();
        if (error > 0)
            Log::error("Servo failed with eccode %d", error);
        
        // Create a UDP server
        Server_UDP x(port);
        Log::info(1, "Port initialized on %d.", port);

        // Loop through read and write our server
        while (!EXIT)
        {
            // Recieave from client and timeout after 4 seconds
            if (x.read(SERVER_TIMEOUT))
            {
                // ---------
                // 000000001 &
                // 00000000-
                if ((x.getBuffer()[0] & 1) == 1)
                {   
                    // @throw Exception_Servo
                    try
                    {
                        // --------
                        // 00001110 & 14
                        // 0000---0 >> 1
                        // 00000---
                        unsigned char command = (x.getBuffer()[0] & 14) >> 1;
                        Log::info(3, "Command: %d", (unsigned short int)command);

                        // --------
                        // 11110000 & 240
                        // ----0000 >> 4
                        // 0000----
                        unsigned char channel = (x.getBuffer()[0] & 240) >> 4;
                        Log::info(3, "Channel: %d", (unsigned short int)channel);

                        switch (command)
                        {
                            case 0:
                            {
                                // -------- >> 1
                                // 0-------
                                unsigned char target = (x.getBuffer()[1] >> 1);
                                
                                // Print out to the server,
                                Log::info(2, "setTarget(%d, %d)" , (unsigned short int)channel, (unsigned short int)target);

                                // Set the target for channel 1 as requested
                                s->setTarget(channel, target);
                                
                                // Good for testing
                                // Set the target for channel 1 as requested
                                Log::info(3, "getTarget(%d): %d", (unsigned short int)channel, s->getTarget(channel));

                                break;
                            }
                            case 1:
                                Log::info(2, "setSpeed(%d)", (unsigned short int)channel);
                                break;
                            case 2:
                                Log::info(2, "setAcceleration(%d)", (unsigned short int)channel);
                                break;
                            default:
                                throw Exception_Servo("Invalid Command!");
                                break;
                                
                        }
                    }
                    // User is out of range, only then do you print the error message
                    catch (Exception_Servo e)
                    {   
                        Log::info(1, e.what());
                        if (!x.write(e.what())) 
                            throw Exception_Server("Write failed");
                    }
                }
            }
            else
            {
                Log::info(3, "read() timeout");
            }
        }
    }
    catch (Exception_Server_Signal e)
    {
        Log::info(4, "caught(Exception_Server_Signal): %s", e.what());
    }
    catch (Exception e)
    {
        Log::warning(1, "caught(Exception): %s.", e.what());
        RETVAL = -1;
    }
    catch (std::runtime_error e)
    {
        Log::warning(3, "caught(runtime_error): %s.", e.what());
        RETVAL = -2;
    }
    
    Log::info(4, "Performing cleanups.");
    delete s;

    Log::info(4, "Returning with %d.", RETVAL);
    return RETVAL;
}


/**
 * Our signal handler as overwritten by our main function
 *
 * @param (int)sig - The signal that was thrown
 */
void sighandler(int sig)
{
    EXIT = 1;
    Log::info(3, "Signal %d called.", sig);
}


/**
 * We need to be able to change the server behaviour using command line arguments.
 * do do that we use this function which takes in argc and argv amongst other arguments
 * other arguments are pointers so this function can modify there value
 *
 * @param (int)argc - The number of arguments
 * @param (char **)argv - The arguments array
 * @param (unsigned int *)servotype - The type of servo, 1 for USB, 2 for FILE
 * @param (char **)servo - The file name for the servo
 * @param (unsigned int *)port - The port the server will run on
 */
void setoptions(int argc, char ** argv, unsigned int * servotype, char ** servo, unsigned int * port)
{
    int c;
    static struct option long_options[] = {
        { "servotype", 1, 0, 0 },
        { "servo", 1, 0, 0 },
        { "port", 1, 0, 0 },
        { "verbosity", 1, 0, 'v' }
    };
    int long_index = 0;
    
    while((c = getopt_long(argc, argv, "sv", long_options, &long_index)) != -1)
    {
        // switch our c, if it is 0 then it uses the long options
        switch (c)
        {
            // Use our long options
            case 0:

                // Switch using the index int, Note that the number
                // of the case x: is relevent to the long_options array above
                switch (long_index)
                {
                    case 0:
                        *servotype = (unsigned int)atoi(optarg);
                        if (*servotype <= 0 && *servotype > 2) { Log::fatal("Invalid sevotype"); }
                        Log::info(3, "Option 'servotype' set to '%d'.", *servotype);
                        break;
                    case 1:
                        *servo = optarg;
                        Log::info(3, "Option 'servo' set to '%s'.", *servo);
                        break;
                    case 2:
                        *port = (unsigned int)atoi(optarg);
                        Log::info(3, "Option 'port' set to '%d'.", *port);
                        break;
                }
                break;

            // Give 'v' we see if optarg is set
            // If so we use its value, otherwise we increase verbosity
            // from its previous state
            case 'v':
                if (optarg)
                    Log::verbosity = (unsigned int)atoi(optarg) == 0 ? 1 : (unsigned int)atoi(optarg);
                else
                    Log::verbosity++;
                Log::info(1, "Option 'verbosity' set to '%d'.", Log::verbosity);
                break;

            // When something goes wrong, a '?' is returned
            case '?':
                Log::error("Option '%s' came back with '?'.", optopt);
                break;
        }
    }
}
