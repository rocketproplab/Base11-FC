package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.SerialPort;

public class TestSCMTransceiver {

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
  public void testSCMTransciverRedirectsRecievedPacketToRouter() {
    PacketRouter                  router   = new PacketRouter();
    TestSerialPort                port     = new TestSerialPort();
    
    SCMTransceiver                tx       = new SCMTransceiver(port, router,
        PacketSources.EngineControllerUnit);
    
    DummyPacketListener<SCMPacket> listener = new DummyPacketListener<SCMPacket>();
    
    router.addListener(listener, SCMPacket.class,
        PacketSources.EngineControllerUnit);
    tx.onSerialData(new SCMPacket(SCMPacketType.VS, "10010").toString());
    
    SCMPacket comparePacket = new SCMPacket(
        new SCMPacket(SCMPacketType.VS, "10010").toString());
    
    assertEquals(comparePacket, listener.lastPacket);
    assertEquals(0, port.lastWritten.size());
  }

  @Test
  public void testSCMTransciverRedirectsRecivedPacketToPort() {
    PacketRouter                  router   = new PacketRouter();
    TestSerialPort                port     = new TestSerialPort();
    
    SCMTransceiver                tx       = new SCMTransceiver(port, router,
        PacketSources.EngineControllerUnit);
    
    DummyPacketListener<SCMPacket> listener = new DummyPacketListener<SCMPacket>();
    
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

  @Test
  public void testSCMTransciverRedirectsPacketBasedOnSource() {
    PacketRouter                  router   = new PacketRouter();
    TestSerialPort                port     = new TestSerialPort();
    
    SCMTransceiver                tx       = new SCMTransceiver(port, router,
        PacketSources.CommandBox);
    
    DummyPacketListener<SCMPacket> listener = new DummyPacketListener<SCMPacket>();
    
    router.addListener(listener, SCMPacket.class,
        PacketSources.EngineControllerUnit);
    tx.onSerialData(new SCMPacket(SCMPacketType.VS, "10010").toString());
    
    assertEquals(null, listener.lastPacket);
    assertEquals(0, port.lastWritten.size());
  }

}
