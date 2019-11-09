package org.rocketproplab.marginalstability.flightcomputer.commands;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRelay;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

public class HeartbeatCommandTest {

  private static final double EPSILON = 0.00000001;

  public class TestTime extends Time {
    public double currentTime;

    @Override
    public double getSystemTime() {
      return currentTime;
    }

  }

  public class TestTelemetry extends Telemetry {
    public int heartbeatCounter;

    public TestTelemetry(Logger logger, PacketRelay relay) {
      super(logger, relay);
    }

    @Override
    public void sendHeartbeat() {
      heartbeatCounter += 1;
    }

  }

  @Test
  public void testIsDone() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    assertFalse(HBcommand.isDone());
  }

  @Test
  public void testHeartbeatDoesNotFire() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    newTime.currentTime = 0;
    HBcommand.start();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD - 0.01;
    HBcommand.execute();
    assertEquals(0, newTelemetry.heartbeatCounter);

  }

  @Test
  public void testHeartbeatafterOneSecond() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    newTime.currentTime = 0;
    HBcommand.start();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD + 0.01;
    HBcommand.execute();
    assertEquals(1, newTelemetry.heartbeatCounter);

  }

  @Test
  public void testOneHeartbeatsbeforeTwoSeconds() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    newTime.currentTime = 0;
    HBcommand.start();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD + 0.01;
    HBcommand.execute();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD * 2 - 0.01;
    HBcommand.execute();
    assertEquals(1, newTelemetry.heartbeatCounter);

  }

  @Test
  public void testTwoHeartbeatsafterTwoSeconds() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    newTime.currentTime = 0;
    HBcommand.start();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD + 0.01;
    HBcommand.execute();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD * 2 + 0.01;
    HBcommand.execute();
    assertEquals(2, newTelemetry.heartbeatCounter);

  }

  @Test
  public void testTwoHeartbeatsbeforeThreeSeconds() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    newTime.currentTime = 0;
    HBcommand.start();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD + 0.01;
    HBcommand.execute();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD * 2 + 0.01;
    HBcommand.execute();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD * 3 - 0.01;
    HBcommand.execute();
    assertEquals(2, newTelemetry.heartbeatCounter);

  }

  @Test
  public void testThreeHeartbeatsafterThreeSeconds() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    newTime.currentTime = 0;
    HBcommand.start();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD + 0.01;
    HBcommand.execute();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD * 2 + 0.01;
    HBcommand.execute();
    newTime.currentTime = Settings.HEARTBEAT_THRESHOLD * 3 + 0.01;
    HBcommand.execute();
    assertEquals(3, newTelemetry.heartbeatCounter);

  }

  @Test
  public void testStart() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);
    newTime.currentTime = 2;
    HBcommand.start();
    assertEquals(2, HBcommand.getStartTime(), EPSILON);
  }

  @Test
  public void testGetDependencies() {
    TestTime         newTime      = new TestTime();
    TestTelemetry    newTelemetry = new TestTelemetry(null, null);
    HeartbeatCommand HBcommand    = new HeartbeatCommand(newTime, newTelemetry);

    assertArrayEquals(new Subsystem[] {}, HBcommand.getDependencies());
  }

}
