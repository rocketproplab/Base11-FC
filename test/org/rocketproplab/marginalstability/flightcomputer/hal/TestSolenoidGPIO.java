package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.mockPi4J.DummyGpioPinImpl;


public class TestSolenoidGPIO {
	
	private DummyGpioSolenoid solenoidTest;

	@Before
	public void init() {
		solenoidTest = new DummyGpioSolenoid();
	}
	
	@Test 
	public void testSolenoidIsActive() {
		assertTrue(solenoidTest.isActive());
	}
	
	@Test
	public void testSetSolenoidSet() {
		solenoidTest.set(false);
		assertFalse(solenoidTest.isActive());
	}
}
