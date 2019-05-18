package org.rocketproplab.marginalstability.flightcomputer.routing;

import java.lang.*;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;


public class ECURouter {
	private static SerialPort ECUPort;

	public ECURouter(SerialPort myPort){
		ECUPort = myPort;
	}

	public void dispatchPacket (String packet){
		ECUPort.write(myPacket);
	}

	public SCMPacket inspectPacket (){
		String incomingPacket = ECUPort.read();
		SCMPacket packet = new SCMPacket(incomingPacket);

		return packet;
	}

	public void createAndSendPulse (){
		String packetID = "HB";
		Random rand = new Random();
		int data = rand.nextInt(100000);
		
		String packet = SCMPacket.encodeSCMPacket(packetID,Integer.toString(data));
		dispatch(packet);
	}
}

