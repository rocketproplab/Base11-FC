package org.rocketproplab.marginalstability.flightcomputer.subsystems;
import org.rocketproplab.marginalstability.flightcomputer.Settings;

import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRelay;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/*
 * 
 *
 * 
 * 
 * @author Clara Chun
 *
 */
public class ValveStateSubsystem implements PacketListener<SCMPacket> {
	
	private SCMPacket packet;
	//change to Integer or Byte
	private Boolean[] valveStates = {null, null, null, null, null, null, null, null};
	private String data;
	private SCMPacketType id;
	private PacketRelay relay;
	
	
	public ValveStateSubsystem(PacketRelay relay) {
		this.relay = relay;
	}
	
	public void onPacket(PacketDirection direction, SCMPacket packet) {
		this.packet = packet;
		this.data = packet.getData();
		this.id = packet.getID();
		setStates();
	}
	
	private void setStates() {
		//change I, J, and K to better values
		int trackArray = 0;
		int maxNum = 5;
		int trackString = 0;
		if (id == SCMPacketType.V1) {
			trackArray = 5;
			maxNum = 8;
		}
		
		while (trackArray < maxNum) {
			if (data.substring(trackString, trackString + 1).equals("0")) {
				valveStates[trackArray] = false;
			} else if (data.substring(trackString, trackString + 1).equals("1")) {
				valveStates[trackArray] = true;
			} else {
				valveStates[trackArray] = null;
			}
			trackString++;
			trackArray++;
		}
		sendPacket(id);
	}
	
	public void setValve(int valve, boolean state) {
		
		valveStates[valve - 1] = state;
		sendPacket(valve);
	}
	
	private void sendPacket(int valve) {
		if (valve < 5) {
			sendPacket(SCMPacketType.V0);
		} else {
			sendPacket(SCMPacketType.V1);
		}
	}
	
	private void sendPacket(SCMPacketType type) {
		// change i and j
		SCMPacketType id = type;
		String data = "";
		SCMPacket packet;
		
		int endValue = 5;
		int beginValue = 0;
		if (id == SCMPacketType.V1) {
			endValue = 8;
			beginValue = 5;
		}
		for (; beginValue < endValue; beginValue++) {
			if (valveStates[beginValue] == null) {
				return;
			}
			data += "" + ((valveStates[beginValue])? 1 : 0);
		}
		if (id == SCMPacketType.V1) {
			data += "00";
		}
		
		packet = new SCMPacket(id, data);
		System.out.println(packet);
		this.relay.sendPacket(packet, PacketSources.EngineControllerUnit);
	}
	
	
}
