#include "I2C.h"

using namespace dashee::test;

/**
 * Create a new instance of I2C
 */
void I2C::setUp()
{
    this->i2c = new dashee::I2C(1);
}

/**
 * Test contraction of I2C values by passing only numbers
 */
void I2C::testIntConstruction()
{
    dashee::I2C * i2c0 = new dashee::I2C(0);
    CPPUNIT_ASSERT(i2c0->getSlaveAddress() == 0x00);
    dashee::I2C * i2c1 = new dashee::I2C(1);
    CPPUNIT_ASSERT(i2c1->getSlaveAddress() == 0x00);

    delete i2c0;
    delete i2c1;

    // Check the slave address post construction
    for (unsigned char x = 0; x < 128; ++x)
    {
	dashee::I2C i2c0(0, x);
	CPPUNIT_ASSERT(i2c0.getSlaveAddress() == x);
	
	dashee::I2C i2c1(1, x);
	CPPUNIT_ASSERT(i2c1.getSlaveAddress() == x);
    }	
}

/**
 * Test constructing I2C values, by passing whole strings
 */ 
void I2C::testStringConstruction()
{
    dashee::I2C * i2c0 = new dashee::I2C("/dev/i2c-0");
    dashee::I2C * i2c1 = new dashee::I2C("/dev/i2c-1");

    delete i2c0;
    delete i2c1;
    
    // Check the slave address post construction
    for (unsigned char x = 0; x < 128; ++x)
    {
	dashee::I2C i2c0("/dev/i2c-0", x);
	CPPUNIT_ASSERT(i2c0.getSlaveAddress() == x);
	
	dashee::I2C i2c1("/dev/i2c-1", x);
	CPPUNIT_ASSERT(i2c1.getSlaveAddress() == x);
    }	
}

/**
 * Test setting and getting of the addresses, Note that only 255 addresses
 * are allowed
 */
void I2C::testSetAndGetAddress()
{
    CPPUNIT_ASSERT(this->i2c->getSlaveAddress() == 0x00);

    for (int x = 0; x < 128; ++x)
    {
	this->i2c->setSlaveAddress(static_cast<unsigned char>(x));
	CPPUNIT_ASSERT(
		this->i2c->getSlaveAddress() == static_cast<unsigned char>(x)
	    );
    }
}

/**
 * Make sure this does not throw an exception
 */
void I2C::testSet10BitAddressFlag()
{
    this->i2c->set10BitAddress(true);
    this->i2c->set10BitAddress(false);
}

/**
 * Test working register functionality
 */
void I2C::testSetAndGetWorkingRegister()
{
    dashee::I2C accelerometer(1, 0x53);
    
    // Test default values
    CPPUNIT_ASSERT(accelerometer.getWorkingRegister() == 0x00);

    // Test changed values
    accelerometer.setWorkingRegister(0x01);
    CPPUNIT_ASSERT(accelerometer.getWorkingRegister() == 0x01);
}

/**
 * This test the reading the value from the register
 */
void I2C::testReadWriteRegister()
{
    std::vector<unsigned char> val;

    // Test reading and writing to the accelerometer
    dashee::I2C accelerometer(1, 0x53);
    val = accelerometer.readFromRegister(0x00, 1);
    CPPUNIT_ASSERT(val.size() == 1);
    CPPUNIT_ASSERT(val[0] == 229);
    val = accelerometer.read();
    CPPUNIT_ASSERT(val.size() == 1);
    CPPUNIT_ASSERT(val[0] == 229);
    val = accelerometer.read(1);
    CPPUNIT_ASSERT(val.size() == 1);
    CPPUNIT_ASSERT(val[0] == 229);

    // Test reading and writing to the gyro
    dashee::I2C gyro(1, 0x68);
    val = gyro.readFromRegister(0x00, 1);
    CPPUNIT_ASSERT(val.size() == 1);
    CPPUNIT_ASSERT(val[0] == 105);
    val = gyro.readFromRegister(0x00, 1);
    CPPUNIT_ASSERT(val.size() == 1);
    CPPUNIT_ASSERT(val[0] == 105);

    // TODO test reading and writing to the magnetometer
}

/**
 * Invalid addresses should throw exceptions
 */
void I2C::testInvalidAddress()
{
    this->i2c->setSlaveAddress(0xFF);
}

/**
 * Destroy the I2C interface and tear down our tests
 */
void I2C::tearDown()
{
    delete i2c;
}
