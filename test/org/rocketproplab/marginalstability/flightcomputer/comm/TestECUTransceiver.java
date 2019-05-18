package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.SerialPort;

public class TestECUTransceiver {

  private class TestSerialPort implements SerialPort {

    public ArrayList<String> lastWritten;

    public TestSerialPort() {
      this.lastWritten = new ArrayList<String>();
    }

    @Override
    public void registerListener(SerialListener listener) {
    }

    @Override
    public void write(String data) {
      this.lastWritten.add(data);
    }

  }

  @Test
  public void testECUTransciverRedirectsRecievedPacketToRouter() {
    PacketRouter                  router   = new PacketRouter();
    TestSerialPort                port     = new TestSerialPort();
    ECUTransceiver                tx       = new ECUTransceiver(port, router);
    TestPacketListener<SCMPacket> listener = new TestPacketListener<SCMPacket>();
    router.addListener(listener, SCMPacket.class,
        PacketSources.EngineControllerUnit);
    tx.onSerialData(new SCMPacket(SCMPacketType.VS, "10010").toString());
    SCMPacket comparePacket = new SCMPacket(
        new SCMPacket(SCMPacketType.VS, "10010").toString());
    assertEquals(comparePacket, listener.lastPacket);
    assertEquals(0, port.lastWritten.size());
  }

  @Test
  public void testECUTransciverRedirectsRecivedPacketToPort() {
    PacketRouter                  router   = new PacketRouter();
    TestSerialPort                port     = new TestSerialPort();
    ECUTransceiver                tx       = new ECUTransceiver(port, router);
    TestPacketListener<SCMPacket> listener = new TestPacketListener<SCMPacket>();
    router.addListener(listener, SCMPacket.class,
        PacketSources.EngineControllerUnit);
    router.addListener(tx, SCMPacket.class, PacketSources.EngineControllerUnit);
    SCMPacket sourcePacket = new SCMPacket(
        new SCMPacket(SCMPacketType.VS, "10010").toString());
    router.sendPacket(sourcePacket, PacketSources.EngineControllerUnit);
    assertEquals(sourcePacket.toString(), port.lastWritten.get(0));
    assertEquals(1, port.lastWritten.size());
    assertEquals(listener.lastDirection, PacketDirection.SEND);
  }

}
