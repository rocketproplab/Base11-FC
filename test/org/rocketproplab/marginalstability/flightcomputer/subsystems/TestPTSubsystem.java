package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

public class TestPTSubsystem {
	
	@Test
	public void onZeroSCMPacketReceivePTreadZero() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		SCMPacket zeropacket = new SCMPacket(SCMPacketType.P0, "00000");
		ptsubsystem.onPacket(PacketDirection.RECIVE, zeropacket);
		assertEquals(0, ptsubsystem.getPT(0), 0.0000001);
	}
	
	@Test
	public void returnNonNumberIfNotInitialised() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		assertTrue(Double.isNaN(ptsubsystem.getPT(0)));
	}
	
	@Test
	public void onValueSCMPacketReceivePTReadValue() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		SCMPacket fourpacket = new SCMPacket(SCMPacketType.P0, "00005");
		ptsubsystem.onPacket(PacketDirection.RECIVE, fourpacket);
		assertEquals(5, ptsubsystem.getPT(0), 0.0000001);
	}
	
	@Test
	public void onValueSCMPacketReceivePTReadValue2() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		SCMPacket fourpacket = new SCMPacket(SCMPacketType.P0, "00001");
		ptsubsystem.onPacket(PacketDirection.RECIVE, fourpacket);
		assertEquals(1, ptsubsystem.getPT(0), 0.0000001);
	}
	
	@Test
	public void returnNaNifValueNotValid() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		SCMPacket nonValidPacket = new SCMPacket(SCMPacketType.P0, "01234");
		ptsubsystem.onPacket(PacketDirection.RECIVE, nonValidPacket);
		assertEquals(Double.NaN, ptsubsystem.getPT(0), 0.0000001);
	}
	
	@Test
	public void ifCorrectValue() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		SCMPacket notUsefulPacket = new SCMPacket(SCMPacketType.P2, "00005");
		ptsubsystem.onPacket(PacketDirection.RECIVE, notUsefulPacket);
		assertNotEquals(0, ptsubsystem.getPT(2), 0.000001);
	}
	
	@Test
	public void returnNaNIfPacketNotRecive() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		SCMPacket sendedPacket = new SCMPacket(SCMPacketType.P2, "00005");
		ptsubsystem.onPacket(PacketDirection.SEND, sendedPacket);
		assertEquals(Double.NaN, ptsubsystem.getPT(2), 0.000001);
	}
	
	@Test
	public void returnZeroIfPTIsCalibratedAndZero() {
		PTSubsystem ptsubsystem = new PTSubsystem();
		SCMPacket packetyPacket = new SCMPacket(SCMPacketType.P4, "00000");
		ptsubsystem.onPacket(PacketDirection.RECIVE, packetyPacket);
		ptsubsystem.calibrate(4);
		assertEquals(1, ptsubsystem.getPT(4), 0.0000001);
	}
	

}