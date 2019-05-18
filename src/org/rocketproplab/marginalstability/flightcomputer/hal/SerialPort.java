package org.rocketproplab.marginalstability.flightcomputer.hal;

import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

public interface SerialPort {
  
	/**
	 * Register a serial port as a listener	
	 * @param listener the listener to register
	 */
	public void registerListener(SerialListener listener);

	/**
	 * Send data over serial port
	 * @param data the string to send
	 */
	public void write(String data);

}