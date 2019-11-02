package org.rocketproplab.marginalstability.flightcomputer.subsystems;
import org.rocketproplab.marginalstability.flightcomputer.Settings;

import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;

import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/*
 * 
 *
 * 
 * 
 * @author Clara Chun
 *
 */
public class PTSubsystem implements PacketListener<SCMPacket> {
	
	private SCMPacket packet;
	public final static int MIN_PT = 0;
	public final static int MAX_PT = 1023;
	private double pt = 0;
	private double[] multiplepts = {Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN};	
	private PacketDirection direction;
	
	/**
	 * Packet receiver
	 */
	public void onPacket(PacketDirection direction, SCMPacket packet) {
		this.packet = packet;
		this.direction = direction;
		setPT();
	}
	
	public double getPT(int index) {
		if (this.direction == PacketDirection.SEND) {
			return Double.NaN;
		} else {
			return multiplepts[index];
		}
	}
	
	private double calibrate(int index, double p) {
		return ((Settings.A_PT_CONSTANTS[index] * p * p) + (Settings.B_PT_CONSTANTS[index]* p) + (Settings.C_PT_CONSTANTS[index]));
	}
	
	public void setPT() {
		// Checks if PT is valid
		pt = Double.parseDouble(this.packet.getData());
		if (!(pt > MIN_PT - 1 && pt < MAX_PT + 1)) {
			pt = Double.NaN;
		}
		
		int x = 0;
		switch (this.packet.getID()) {
			case P0:
				x = 0;
			  	break;
			case P1:
				x = 1;
				break;
			case P2:
				x = 2;
				break;
			case P3:
				x = 3;
				break;
			case P4:
				x = 4;
				break;
			case P5:
				x = 5;
				break;
			case P6:
				x = 6;
				break;
			case P7:
				x = 7;
				break;
			case P8:
				x = 8;
				break;
			case P9:
				x = 9;
				break;
			case PA:
				x = 10;
				break;
			case PB:
				x = 11;
				break;
			case PC:
				x = 12;
				break;
			case PD:
				x = 13;
				break;
			case PE:
				x = 14;
				break;
			case PF:
				x = 15;
				break;
			default:
				// TODO handle error, unexpected PT Type
				return;
		}
		multiplepts[x] = calibrate(x, pt);
	}
	
	
}
