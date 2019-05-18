package org.rocketproplab.marginalstability.flightcomputer.hal;

import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

public interface SerialPort {
	/*
	 * Register a serial port as a listener	
	 */
	public void registerListener(SerialListener listener);

	/*
	 * Send data over serial port
	 */
	public void write(String data);

	/*
	 * Read data from serial port
	 */
	public String read(String data);
}