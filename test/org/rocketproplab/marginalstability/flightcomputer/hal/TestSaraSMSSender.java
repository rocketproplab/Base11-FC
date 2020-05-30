package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.GPSPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.GPSMessageSubsystem;

public class TestSaraSMSSender {
	public class SeraGPS {
		//when the rocket lands we want it to send us it's location
		//i think i should pass this as an object, then we can set a listener
		//on it for when it lands
		//we have to talk to this through the serial port?
	}
	public class SerialPortSara implements SerialPort{
		List<String> data = new ArrayList<String>();
		PacketRouter router = new PacketRouter();
		
		@Override
		public void registerListener(SerialListener listener) {			
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
		
		SMSSera.sendTextMessage("13108665454", "Hello, World!");
		
		assertEquals(serialPort.getData().get(3), "Hello, World!\r\n");
	}
	
	@Test
	public void sendMessageReceiveMessage() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		
		SMSSera.sendTextMessage("12908665454", "Wow A Message");
		assertEquals(serialPort.getData().get(2).substring(10, 21), "12908665454");
		assertEquals(serialPort.getData().get(3), "Wow A Message\r\n");
	}
	
	@Test
	public void sendMessageFull() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		
		SMSSera.sendTextMessage("12908665454", "BLOOD FOR THE BLOOD GOD");
		assertEquals(serialPort.getData().get(0), "AT\n");
		assertEquals(serialPort.getData().get(1), "AT+CMGF=1\n");
		assertEquals(serialPort.getData().get(2), "AT+CMGS=\"+12908665454\"\n");
		assertEquals(serialPort.getData().get(3), "BLOOD FOR THE BLOOD GOD\r\n");
	}
	
	@Test
	public void numberNotValid() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		
		try {
			SMSSera.sendTextMessage("4", "FOR NARNIAAAA");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		String longMessage = "FOR NARNIAAAA AND NO ONE ELSE OR MAYBE FOR THE BLOOD"
				+ "GOD OR SOMEONE ELSE. I HAVE NO IDEA, THIS IS ME YELLING TO TAKE"
				+ "UP SPACE SO THAT I CAN TEST A THINGAMAJING MWAHAHAH MEWTWO I WIN";
		
		try {
			SMSSera.sendTextMessage("12348192019", longMessage);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void testGetGPSInfo() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		SMSSera.getGPSInfo();
		assertEquals(serialPort.getData().get(2), "AT+UGPS=1\n");
		assertEquals(serialPort.getData().get(3), "AT+UGGGA?\r\n");
	}
	
	@Test
	public void testGPSCreateMessage() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		GPSMessageSubsystem gpsMessage = new GPSMessageSubsystem();
		GPSPacket packet = new GPSPacket("$GPGGA,420,-32,N,7,W,2,12,1.2,100000,M,-25.669,M,2.0,0031*4F");
		
		SMSSera.onSerialData("+UGGGA: 1,$GPGGA,420,-32,N,7,W,2,12,1.2,100000,M,-25.669,M,2.0,0031*4F");
		
		gpsMessage.onPacket(PacketDirection.RECIVE, packet);
		String message = gpsMessage.createMessage();
		SMSSera.sendTextMessage("12908665454", message);
		
		assertEquals(serialPort.getData().get(3), "The longitude is: 7.0\n" + 
				"The latitude is: -32.0\r\n");
	}
	
	@Test
	public void testGPSCreateError() {
		SerialPortSara serialPort = new SerialPortSara();
		SaraSMSSender SMSSera = new SaraSMSSender(serialPort);
		try {
			SMSSera.onSerialData(null);
		} catch (NullPointerException e) {
		}
	}
	

}

