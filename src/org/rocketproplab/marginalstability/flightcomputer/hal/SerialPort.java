package org.rocketproplab.marginalstability.flightcomputer.hal;

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
	public void read(String data);
}