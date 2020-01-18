package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.comm.TestPacketListener;

public class TestValveStateSubsystem {
	private TestPacketListener<SCMPacket> testListener;
	PacketRouter router;
	ValveStateSubsystem valveState;

	@Before
	public void init() {
		this.testListener = new TestPacketListener<SCMPacket>();
		this.router = new PacketRouter();
		router.addListener(testListener, SCMPacket.class, PacketSources.EngineControllerUnit);
	  }
	
	@Test
	public void onZeroSCMPacketReceiveValveStateReadZero() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket zeropacket = new SCMPacket(SCMPacketType.V0, "00000");
		valveState.onPacket(PacketDirection.RECIVE, zeropacket);
		assertEquals("0", this.testListener.lastPacket.getData().substring(0, 1));
	}
	
	@Test
	public void onNonValidNumReturnNaN() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket nonValid = new SCMPacket(SCMPacketType.V0, "00500");
		valveState.onPacket(PacketDirection.RECIVE, nonValid);
		assertNotEquals(nonValid, this.testListener.lastPacket);
	}
	
	@Test
	public void onSCMPacketReturnValveState() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V0, "01010");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		assertEquals("0", this.testListener.lastPacket.getData().substring(4));
	}
	
	@Test
	public void onSCMPacketReturnValveState2() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V0, "01010");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		assertEquals("1", this.testListener.lastPacket.getData().substring(3,4));
	}

	@Test
	public void onSCMPacketReturnValveState3() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V1, "11100");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		assertEquals("1", this.testListener.lastPacket.getData().substring(0,1));
	}
	
	@Test
	public void onSCMPacketReturnValveState4() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V1, "11000");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		assertEquals("0", this.testListener.lastPacket.getData().substring(2,3));
	}
	
	//test if packet get's passed on along when all values wanted are valid
	@Test
	public void onValidValuesPacketPassedisRight() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V0, "11001");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		assertEquals(packetOne, this.testListener.lastPacket);
	}
	
	//check direction of packet
	@Test
	public void onValidValuesPacketisPassedInRightDirection() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V0, "11001");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
	}
	
	//test if the packet passed along is correct when all values are valid
	@Test
	public void checkIfSentPacketHasRightValues() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V0, "00101");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		assertEquals(packetOne.getData(), this.testListener.lastPacket.getData());
	}
	
	//change a value and then test if packet passed along is valid
	@Test
	public void checkIfSentPacketIsCorrectWhenOneValueIsChanged() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetTwo = new SCMPacket(SCMPacketType.V1, "01100");
		valveState.onPacket(PacketDirection.RECIVE, packetTwo);
		valveState.setValve(8, 0);
		assertEquals("01000", this.testListener.lastPacket.getData());
	}
	//test if setting to true
	@Test
	public void checkIfSentPacketIsCorrectWhenOneValueIsChanged2() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V0, "10011");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		valveState.setValve(2, 1);
		assertEquals("11011", this.testListener.lastPacket.getData());
	}
	
	//test out of bounds set value
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void checkForErrorOutOfBounds() {
		this.valveState = new ValveStateSubsystem(router);
		valveState.setValve(9, 1);
	}
	
	// if a value isn't valid, check if packet doesn't get passed along
	@Test
	public void lastSentPacketNotMatchWhenValueInvalid() {
		this.valveState = new ValveStateSubsystem(router);
		SCMPacket packetOne = new SCMPacket(SCMPacketType.V1, "01000");
		valveState.onPacket(PacketDirection.RECIVE, packetOne);
		SCMPacket packetTwo = new SCMPacket(SCMPacketType.V1, "11200");
		valveState.onPacket(PacketDirection.RECIVE, packetTwo);
		assertEquals(packetOne.getData(), this.testListener.lastPacket.getData());
	}
	//

}
