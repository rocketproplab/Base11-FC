package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.Info;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.comm.DummyPacketListener;

public class TestTelemetry {

  private DummyPacketListener<SCMPacket> testListener;
  TestLog                               logger;
  PacketRouter                          router;
  Telemetry                             telemetry;

  class TestLog extends Logger {

    public String lastMessage;
    public Level  lastLevel;

    protected TestLog() {
      super("Dummy", null);
    }

    public void log(Level level, String message) {
      this.lastLevel   = level;
      this.lastMessage = message;
    }

  }

  @Before
  public void init() {
    this.testListener = new DummyPacketListener<SCMPacket>();
    this.logger       = new TestLog();
    this.router       = new PacketRouter();
    this.telemetry    = new Telemetry(logger, router);
    router.addListener(testListener, SCMPacket.class, PacketSources.CommandBox);
  }

  @Test
  public void telemetryReportsBase10IntegerAsSCMPacket() {
    this.telemetry.reportTelemetry(SCMPacketType.P0, 100);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.P0, "00100");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryReportsBase16IntegerAsSCMPacket() {
    this.telemetry.reportTelemetryHex(SCMPacketType.GX, 255);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.GX, "000FF");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryReportsDoubleAsSCMPacket() {
    this.telemetry.reportTelemetry(SCMPacketType.T0, 35.3);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.T0, "35.30");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesInfinityWhenTooLargerInt() {
    this.telemetry.reportTelemetryHex(SCMPacketType.GX, 0x1FFFFF);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.GX, "INF  ");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesNegativeInfinityWhenTooSmallInt() {
    this.telemetry.reportTelemetryHex(SCMPacketType.GX, -65537);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.GX, "-INF ");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesMaxValWhenAtMaxVal() {
    this.telemetry.reportTelemetry(SCMPacketType.GX, 99999);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.GX, "99999");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesNegativeInfinityWhenTooSmallDouble() {
    this.telemetry.reportTelemetry(SCMPacketType.T0, -10000.0);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.T0, "-INF ");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesNegativeInfinityWhenTooSmallIntDec() {
    this.telemetry.reportTelemetry(SCMPacketType.T0, -10000);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.T0, "-INF ");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesValidWithPreciseSmallDouble() {
    this.telemetry.reportTelemetry(SCMPacketType.T0, -1.23456);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.T0, "-1.23");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesInfinityWithLargeDouble() {
    this.telemetry.reportTelemetry(SCMPacketType.T0, 123456.0);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.T0, "INF  ");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryGeneratesErrorPacket() {
    this.telemetry.reportError(Errors.UNKNOWN_ERROR);
    SCMPacket testPacket = new SCMPacket(SCMPacketType.ER, "00000");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetrySendsHeartbeat() {
    this.telemetry.sendHeartbeat();
    SCMPacket testPacket = new SCMPacket(SCMPacketType.HB, "00000");
    assertEquals(testPacket, this.testListener.lastPacket);
    assertEquals(PacketDirection.SEND, this.testListener.lastDirection);
  }

  @Test
  public void telemetryLogsInfo() {
    this.telemetry.logInfo(Info.FINISH_SUBSYSTEM_START);
    assertEquals(Level.INFO, this.logger.lastLevel);
    assertEquals(Info.FINISH_SUBSYSTEM_START.getDescription(), this.logger.lastMessage);
  }

}
