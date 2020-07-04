package org.rocketproplab.marginalstability.flightcomputer.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.hal.Valves;

public class TestSetValveCommand {
  private class MockValves implements Valves {
    public boolean[] mockvalvestates = new boolean[8];

    @Override
    public void setValve(int index, boolean active) {
      mockvalvestates[index] = active;
    }
  }

  @Test
  public void activateValve3() {
    MockValves      mockvalves      = new MockValves();
    SCMPacket       scmpacket       = new SCMPacket(SCMPacketType.V3, "On   ");
    SetValveCommand setvalvecommand = new SetValveCommand(scmpacket, mockvalves);
    setvalvecommand.start();
    setvalvecommand.execute();
    assertTrue(setvalvecommand.isDone());
    setvalvecommand.end();
    assertEquals(true, mockvalves.mockvalvestates[3]);
  }

  @Test
  public void deactivateValve2() {
    MockValves mockvalves = new MockValves();
    mockvalves.setValve(2, true);
    SCMPacket       scmpacket       = new SCMPacket(SCMPacketType.V2, "Off  ");
    SetValveCommand setvalvecommand = new SetValveCommand(scmpacket, mockvalves);
    setvalvecommand.start();
    setvalvecommand.execute();
    assertTrue(setvalvecommand.isDone());
    setvalvecommand.end();
    assertFalse(mockvalves.mockvalvestates[2]);
  }

  @Test
  public void activateValve3spacing() {
    MockValves      mockvalves      = new MockValves();
    SCMPacket       scmpacket       = new SCMPacket(SCMPacketType.V3, "   On");
    SetValveCommand setvalvecommand = new SetValveCommand(scmpacket, mockvalves);
    setvalvecommand.start();
    setvalvecommand.execute();
    assertTrue(setvalvecommand.isDone());
    setvalvecommand.end();
    assertEquals(true, mockvalves.mockvalvestates[3]);
  }

  @Test
  public void doesntChangeIfNotGivenOnOrOff() {
    MockValves      mockvalves      = new MockValves();
    SCMPacket       scmpacket       = new SCMPacket(SCMPacketType.V3, "astra");
    SetValveCommand setvalvecommand = new SetValveCommand(scmpacket, mockvalves);
    setvalvecommand.start();
    setvalvecommand.execute();
    assertTrue(setvalvecommand.isDone());
    setvalvecommand.end();
    assertEquals(false, mockvalves.mockvalvestates[3]);
    mockvalves.setValve(3, true);
    setvalvecommand = new SetValveCommand(scmpacket, mockvalves);
    setvalvecommand.start();
    setvalvecommand.execute();
    assertTrue(setvalvecommand.isDone());
    setvalvecommand.end();
    assertEquals(true, mockvalves.mockvalvestates[3]);
  }

}
