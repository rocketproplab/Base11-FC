package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.mockPi4J.DummyGpioPinImpl;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Spi;


public class TestIMU {
	
	private DummyPiCommandsSolenoid solenoidTest;
	private GpioPin solenoidPin;

	@Before
	public void init() {
		solenoidTest = new DummyPiCommandsSolenoid();
		solenoidPin = new DummyGpioPinImpl();
	}
	
	@Test 
	public void testSolenoidIsActive() {
		assertTrue(solenoidTest.isActive());
	}
	
	@Test
	public void testSetSolenoid() {
		solenoidTest.setPinState(solenoidPin);
		assertEquals(solenoidTest.getPinState(), ((DummyGpioPinImpl)solenoidPin).getState());
	}
}
