package org.rocketproplab.marginalstability.flightcomputer.hal;

public class DummyPiCommandsImu {
	
	private byte altX;
	private byte altY;
	private byte alyZ;
	
	
	/**
	 * This is for receiving packages. Basically a dummy pi command set.
	 * I loved it.
	 */
	public void DummyPiCommands() {	
	}
}
/*
 * i2c has registers which store data generally for things like altitude it's
 * inputed to 3 registers
 * need to read, set, and specific read from registers. all of these are bytes
 * lsm9ds1 - data sheet
 */