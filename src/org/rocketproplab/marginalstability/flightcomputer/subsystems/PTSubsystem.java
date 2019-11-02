package org.rocketproplab.marginalstability.flightcomputer.subsystems;
import org.rocketproplab.marginalstability.flightcomputer.Settings;

import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;

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
public class PTSubsystem implements PacketListener<SCMPacket> {
	
	private SCMPacket packet;
	public static int MIN_PT = 0;
	public static int MAX_PT = 1023;
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
	
	public double returnValidNumsOnly(double value) {
		if (value > MIN_PT - 1 && value < MAX_PT + 1) {
			return value;
		} else {
			return Double.NaN;
		}
	}
	
	public void calibrate(int index) {
		multiplepts[index] = ((Settings.A_PT_CONSTANTS[index] * pt * pt) + (Settings.B_PT_CONSTANTS[index]* pt) + (Settings.C_PT_CONSTANTS[index]));
	}
	
	public void setPT() {
		
		pt = returnValidNumsOnly(Double.parseDouble(this.packet.getData()));
		
		int x = 0;
		switch (this.packet.getID()) {
			case P0:
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
			case P10:
				x = 10;
				break;
			case P11:
				x = 11;
				break;
			case P12:
				x = 12;
				break;
			case P13:
				x = 13;
				break;
			case P14:
				x = 14;
				break;
			case P15:
				x = 15;
				break;
			default:
				break;
		}
		multiplepts[x] = pt;
	}
	
	
}
