package org.rocketproplab.marginalstability.flightcomputer.hal;

public interface SMSSender {
	void sendTextMessage(String number, String message);
}
