package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

public class TestSaraSMSSender {
	public class SerialPortSara implements SerialPort{
		List<String> data = new ArrayList<String>();
		
		@Override
		public void registerListener(SerialListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void write(String data) {
			this.data.add(data);
		}
		
		public List<String> getData() {
			return data;
		}
	}
	
	@Test
	public void sendMessageReceiveNothing() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		
		SMSSera.sendMessage("13108665454", "Hello, World!");
		
		assertEquals(serialPort.getData().get(3), "Hello, World!\r");
	}
	
	@Test
	public void sendMessageReceiveMessage() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		
		SMSSera.sendMessage("12908665454", "Wow A Message");
		assertEquals(serialPort.getData().get(2).substring(10, 21), "12908665454");
		assertEquals(serialPort.getData().get(3), "Wow A Message\r");
	}
	
	@Test
	public void sendMessageFull() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		
		SMSSera.sendMessage("12908665454", "BLOOD FOR THE BLOOD GOD");
		assertEquals(serialPort.getData().get(0), "AT");
		assertEquals(serialPort.getData().get(1), "AT+CMFG=1");
		assertEquals(serialPort.getData().get(2), "AT+CMGS=\"+12908665454\"\n");
		assertEquals(serialPort.getData().get(3), "BLOOD FOR THE BLOOD GOD\r");
	}
	
	@Test
	public void numberNotValid() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		
		try {
			SMSSera.sendMessage("4", "FOR NARNIAAAA");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}
	

}

