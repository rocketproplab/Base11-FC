package org.rocketproplab.marginalstability.flightcomputer.hal;

import org.rocketproplab.marginalstability.flightcomputer.hal.*;

public class SaraSMSSender implements SMSSender {
	
	SerialPort saraSerialPort;
	String phoneNumber;
	String at;
	int messageIndex;
	String result;
	
	public SaraSMSSender(SerialPort saraSerialPort) {
		this.saraSerialPort = saraSerialPort;
		messageIndex = 0;
	}
	
	@Override
	public void sendMessage(String number, String data) {
		//TODO: Add in a listener that would allow us to check if the AT
		//is on, if it's been switched the SMS mode, and if the message
		//was sent.
		if (number == null) {
			throw new NullPointerException();
		} else if (number.length() != 11) {
			throw new IllegalArgumentException();
		} else	{
			saraSerialPort.write("AT");
			saraSerialPort.write("AT+CMFG=1");
			saraSerialPort.write("AT+CMGS=\"+" + number + "\"");
			saraSerialPort.write(data);
		}
	}
}
