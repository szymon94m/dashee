/**
 * @file tests/I2C.h
 *
 * @section LICENSE
 *
 * This file is licensed under the terms and condition
 * mentioned at http://dashee.co.uk/license. 
 */

#ifndef DASHEE_TEST_I2C_H_
#define DASHEE_TEST_I2C_H_

#include <cppunit/extensions/HelperMacros.h>
#include <dashee/I2C.h>

/*
 * Set the namespace
 */
namespace dashee
{
    namespace test
    {
        class I2C;
    }
}   

/**
 * Buffer test class for
 * unit testing known components
 */
class dashee::test::I2C
    : public CppUnit::TestFixture
{
    CPPUNIT_TEST_SUITE(dashee::test::I2C);
    
    CPPUNIT_TEST(testIntConstruction);
    CPPUNIT_TEST(testStringConstruction);

    CPPUNIT_TEST(testSetAndGetAddress);
    CPPUNIT_TEST(testSet10BitAddressFlag);
    
    CPPUNIT_TEST(testReadRegister);
    CPPUNIT_TEST(testWriteRegister);
    
    // Exceptions
    CPPUNIT_TEST_EXCEPTION(
	    testInvalidByteSizeWhenReading, 
	    dashee::ExceptionI2C
	);
    CPPUNIT_TEST_EXCEPTION(
	    testInvalidByteSizeWhenWriting, 
	    dashee::ExceptionI2C
	);
    CPPUNIT_TEST_EXCEPTION(
	    testInvalidOutOfBoundsWhenWriting, 
	    dashee::ExceptionOutOfBounds
	);
    CPPUNIT_TEST_EXCEPTION(
	    testInvalidAddress, 
	    dashee::ExceptionI2C
	);
    CPPUNIT_TEST_EXCEPTION(
	    testInvalidNullPointerRead, 
	    dashee::ExceptionNullPointer
	);
    CPPUNIT_TEST_EXCEPTION(
	    testInvalidNullPointerWrite, 
	    dashee::ExceptionNullPointer
	);
    
    CPPUNIT_TEST_SUITE_END();

private:
    dashee::I2C * i2c;

protected:
    void testIntConstruction();
    void testStringConstruction();

    void testSetAndGetAddress();
    void testSet10BitAddressFlag();

    void testReadRegister();
    void testWriteRegister();
    
    void testInvalidAddress();
    void testInvalidByteSizeWhenWriting();
    void testInvalidByteSizeWhenReading();
    void testInvalidOutOfBoundsWhenWriting();
    void testInvalidNullPointerRead();
    void testInvalidNullPointerWrite();

public:
    void setUp();
    void tearDown();
};

#endif

