#include "Container.h"

/**
 * Construct our object
 */
Container::Container(int argc, char ** argv)
{
    this->argc = argc;
    this->argv = argv;

    this->config = NULL;

    this->loadConfig();
    this->loadServer();
    this->loadServoController();
    this->loadVehicle();

    this->lockConfig = NULL;
    this->lockServer = NULL;
    this->lockServoController = NULL;
    this->lockVehicle = NULL;
}

/**
 * Set the lockConfig variable
 *
 * @param lockConfig The pointer to the lock
 *
 * @throws dashee::Exception if the pointer is NULL
 */
void Container::setLockConfig(dashee::Threads::Lock * lockConfig)
{
    if (lockConfig == NULL)
        throw dashee::Exception("Lock param cannot be NULL");

    this->lockConfig = lockConfig;
}

/**
 * Set the lockServer variable
 *
 * @param lockServer The pointer to the lock
 *
 * @throws dashee::Exception if the pointer is NULL
 */
void Container::setLockServer(dashee::Threads::Lock * lockServer)
{
    if (lockServer == NULL)
        throw dashee::Exception("Lock param cannot be NULL");

    this->lockServer = lockServer;
}

/**
 * Set the lockServoController variable
 *
 * @param lockServoController The pointer to the lock
 *
 * @throws dashee::Exception if the pointer is NULL
 */
void Container::setLockServoController(
        dashee::Threads::Lock * lockServoController
    )
{
    if (lockServoController == NULL)
        throw dashee::Exception("Lock param cannot be NULL");

    this->lockServoController = lockServoController;
}

/**
 * Set the lockVehicle variable
 *
 * @param lockVehicle The pointer to the lock
 *
 * @throws dashee::Exception if the pointer is NULL
 */
void Container::setLockVehicle(dashee::Threads::Lock * lockVehicle)
{
    if (lockVehicle == NULL)
        throw dashee::Exception("Lock param cannot be NULL");

    this->lockVehicle = lockVehicle;
}

/**
 * Set the pointer to the config variable
 *
 * @param config The pointer which points to the value of the config object
 */
void Container::setConfig(dashee::Config * config)
{
    if (config == NULL)
        throw new dashee::Exception("Cannot set config to NULL");

    this->config = config;
}

/**
 * Get the pointer to the config variable
 *
 * @return the pointer to the config variable
 */
dashee::Config * Container::getConfig()
{
    return this->config;
}

/**
 * Set the pointer to the server variable
 *
 * @param server The pointer which points to the value of the server object
 */
void Container::setServer(dashee::Server * server)
{
    if (server == NULL)
        throw new dashee::Exception("Cannot set server to NULL");

    this->server = server;
}

/**
 * Get the pointer to the server variable
 *
 * @return the pointer to the server variable
 */
dashee::Server * Container::getServer()
{
    return this->server;
}

/**
 * Set the pointer to the servoController variable
 *
 * @param servoController The pointer which points to the value of the 
 * servoController object
 */
void Container::setServoController(dashee::ServoController * servoController)
{
    if (servoController == NULL)
        throw new dashee::Exception("Cannot set servoController to NULL");

    this->servoController = servoController;
}

/**
 * Get the pointer to the servoController variable
 *
 * @return the pointer to the servoController variable
 */
dashee::ServoController * Container::getServoController()
{
    return this->servoController;
}

/**
 * Set the pointer to the vehicle variable
 *
 * @param vehicle The pointer which points to the value of the vehicle object
 */
void Container::setVehicle(dashee::Vehicle * vehicle)
{
    if (vehicle == NULL)
        throw new dashee::Exception("Cannot set vehicle to NULL");

    this->vehicle = vehicle;
}

/**
 * Get the pointer to the vehicle variable
 *
 * @return the pointer to the vehicle variable
 */
dashee::Vehicle * Container::getVehicle()
{
    return this->vehicle;
}

/**
 * Load our configuration file
 *
 * @throw Exception If an invalid type is detected
 */
void Container::loadConfig()
{
    dashee::Threads::Scope scope(this->lockConfig);

    this->config = new dashee::Config();
    this->config->set("config", CONFIG);

    int c;
    static struct option long_options[] = {
        { "servo-type", 1, 0, 0 },
        { "servo-name", 1, 0, 0 },
        { "port", 1, 0, 'p' },
        { "config", 1, 0, 'c' },
        { "verbosity", 1, 0, 'v' },
        { "logfile", 1, 0, 'l' },
        { "workingdir", 1, 0, 'w' },
        { "pidfile", 1, 0, 0 }
    };
    int long_index = 0;
    
    while(
            (c = getopt_long(
                             this->argc, 
                             this->argv, 
                             "c:p:v", 
                             long_options, 
                             &long_index
                            )
            ) 
            != -1
        )
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
                    // Type of Servo
                    case 0:
                        this->config->set(
                                "servo-type", 
                                static_cast<int>(dashee::strtol(optarg))
                            );
                        break;
                    // Servo file path
                    case 1:
                        this->config->set("servo-name", optarg);
                        break;
                    // PID file
                    case 7:
                        this->config->set("pidfile", optarg);
                        break;
                }
                break;
            // Set the logfile location
            case 'l':
                this->config->set("logfile", optarg);
                break;
            // Set working directory
            case 'w':
                this->config->set("workingdir", optarg);
                break;
            // Give 'v' we see if optarg is set
            // If so we use its value, otherwise we increase verbosity
            // from its previous state
            case 'v':
                if (optarg)
                    dashee::Log::verbosity 
                        = dashee::strtol(optarg) == 0 
                            ? 1 : dashee::strtol(optarg);
                else
                    dashee::Log::verbosity++;
                break;
            // Represents the config file which will be read later
            case 'c':
                this->config->set("config", optarg);
                break;
            // Represents the port
            case 'p':
                this->config->set(
                        "port", 
                        static_cast<int>(dashee::strtol(optarg))
                    );
                break;
            // When something goes wrong, a '?' is returned
            case '?':
                this->lockConfig->unlock();
                throw dashee::Exception(
                        "Option '" + dashee::ctostr(optopt) + 
                        "' requires a value"
                    );
                break;
        }
    }

    // Read the config from file
    this->config->read(this->config->get("config"));
}

/**
 * Simple function which loads the server.
 *
 * At the moment only UDP is supported
 */
void Container::loadServer()
{
    dashee::Threads::Scope scope(this->lockServer);

    this->server 
        = new dashee::ServerUDP(config->getUInt("port", SERVER_PORT));

    this->server->setTimeout(
            config->getUInt("server-timeout", SERVER_TIMEOUT), 
            config->getUInt("server-timeoutM", 0)
        );
}

/**
 * Load our ServoController
 *
 * @throws dashee::ExceptionServoController If error came back with a value
 * Or the servotype is invalid
 */
void Container::loadServoController()
{
    const char * servoName = config->get("servo-name", SERVOCONTROLLER_DEVICE);
    unsigned int servoChannels 
        = config->getUInt("servo-channels", SERVOCONTROLLER_CHANNELS);

    // Create a different servo-type depending on the variable
    switch (config->getUInt("servo-type", SERVOCONTROLLER_TYPE))
    {
        case 1:
            dashee::Log::info(1, "Loading UART device '%s'.", servoName);
            this->servoController 
                = new dashee::ServoControllerUART(servoName, servoChannels);
            break;
        case 2:
            dashee::Log::info(1, "Loading USB device '%s'.", servoName);
            this->servoController 
                = new dashee::ServoControllerUSB(servoName, servoChannels);
            break;
        case 3:
            dashee::Log::info(1, "Loading Dummy device '%s'.", servoName);
            this->servoController = new dashee::ServoControllerDummy(
                    servoName, 
                    SERVOCONTROLLER_CHANNELS
                );
            break;
        default:
            throw dashee::ExceptionServoController(
                    "Invalid servo-type '" + 
                    dashee::itostr(
                        this->config->getUInt(
                            "servo-type", 
                            SERVOCONTROLLER_TYPE
                        )
                        ) + 
                    "'"
                );
            break;
    }

    // Print and clear errors
    int error = this->servoController->getError();
    if (error > 0)
        throw dashee::ExceptionServoController(
                "ServoController failed with eccode " + dashee::itostr(error)
            );
}

/**
 * Load the vehicle
 *
 * @throws ExceptionVehicle incase something goes wrong
 */
void Container::loadVehicle()
{
    if (this->servoController == NULL)
        throw dashee::ExceptionVehicle(
                "servoController needs to be a valid pointer, "
                "Cannot load vehicle otherwise."
            );

    const char * modelType = config->get("vehicle-type", VEHICLE_TYPE);

    if (strcmp(modelType, "Car") == 0)
    {
        this->vehicle 
            = new dashee::VehicleCar(this->servoController, this->config);
    }
    else if (strcmp(modelType, "MultirotorQuadX") == 0)
        this->vehicle 
            = new dashee::VehicleMultirotorQuadX(
                    this->servoController, 
                    this->config
                );
    else
        throw dashee::ExceptionVehicle(
                "Invalid vehicle-type '" + 
                std::string(modelType) + 
                "'"
            );

    dashee::Log::info(3, "Model set to '%s'.", modelType);
}

/**
 * Reload configuration by re-reading the config file. Note the configuration 
 * is limited to only change a few variables during runtime.
 *
 * In some cases new variables are initilized and in others only reread 
 */
void Container::reloadConfiguration()
{
    if (this->lockConfig == NULL)
        throw dashee::Exception("lockConfig should be not NULL");
    
    dashee::Threads::Scope scopeConfig(this->lockConfig);
    dashee::Threads::Scope scopeServer(this->lockServer);
    dashee::Threads::Scope scopeServoController(this->lockServoController);
    dashee::Threads::Scope scopeVehicle(this->lockVehicle);
    
    this->reloadConfig();
    this->reloadServer();
    this->reloadServoController();
    this->reloadVehicle();
}

/**
 * load our config from file, note the key 'config' must already be set, 
 * otherwise and exception is thrown
 *
 * @param dashee::ExceptionConfig();
 */ 
void Container::reloadConfig()
{
    //Ensure we can operate
    if (this->getConfig() == NULL)
        throw dashee::ExceptionConfig(
                "The config pointer is null in loadConfig"
            );

    // If the config key is not set, no point continuing. This should not really
    // happen so its appropriate to warn with exception.
    if (!this->getConfig()->isKeySet("config"))
    {
        throw new dashee::ExceptionConfig(
                "config 'key' is not set, for loading config value. "
                "This should atleast be defaulted to something by the main "
                "program. Technically this should be set by loadConfig(), "
                "something seems horibally wrong!"
            );
    }

    // Read from config and update the value's
    this->getConfig()->read(this->getConfig()->get("config"));
}

/**
 * Reload our server, this function does not destroy the object, but just resets
 * the timeout values.
 *
 * @throws Exception
 */
void Container::reloadServer()
{
    //Ensure we can operate
    if (this->config == NULL)
        throw dashee::ExceptionConfig(
                "The config pointer is null in loadConfig"
            );

    // Set only the values that are allowed by a reload
    this->server->setTimeout(
            this->config->getUInt("server-timeout"), 
            this->config->getUInt("server-timeoutM")
        );
}

/**
 * Call the load for servoController because the servocontroller needs to be
 * destroyed and reinitiated
 */ 
void Container::reloadServoController()
{
    delete this->servoController;
    this->loadServoController();
}

/**
 * Call the load function for vehicle, because the object can be destructed and
 * rebuilt again
 */
void Container::reloadVehicle()
{
    delete this->vehicle;
    this->loadVehicle();
}

/**
 * Destroy gracefully
 */
Container::~Container()
{
    delete this->config;
    delete this->server;
    delete this->servoController;
    delete this->vehicle;
}

// Initilize our constants for SERVOCONTROLLER
const char * Container::SERVOCONTROLLER_DEVICE = "/dev/ttyAMA0";
const unsigned int Container::SERVOCONTROLLER_TYPE = 1u;
const unsigned int Container::SERVOCONTROLLER_CHANNELS = 6u;

// Initilize our constants for CONFIG
const char * Container::CONFIG = "./etc/dashee/servod.conf";

// Initilize our constants for SERVER
const unsigned int Container::SERVER_PORT = 2047u;
const unsigned int Container::SERVER_TIMEOUT = 2u;

// Initilize our constants for VEHICLE
const char * Container::VEHICLE_TYPE = "Car";
