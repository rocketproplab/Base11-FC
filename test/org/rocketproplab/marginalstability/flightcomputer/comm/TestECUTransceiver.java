package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.SerialPort;

public class TestECUTransceiver {

  private class TestSerialPort implements SerialPort {

    public ArrayList<String> lastWritten;
    public SerialListener    listener;

    public TestSerialPort() {
      this.lastWritten = new ArrayList<String>();
    }

    public void sendMessage(String message) {
      this.listener.onSerialData(message);
    }

    @Override
    public void registerListener(SerialListener listener) {
      this.listener = listener;
    }

    @Override
    public void write(String data) {
      this.lastWritten.add(data);
    }

    public void clearLastWritten() {
      this.lastWritten.clear();
    }

  }

  @Test
  public void eCUTransciverRedirectsRecievedPacketToRouter() {
    PacketRouter                  router   = new PacketRouter();
    TestSerialPort                port     = new TestSerialPort();
    ECUTransceiver                tx       = new ECUTransceiver(port, router);
    TestPacketListener<SCMPacket> listener = new TestPacketListener<SCMPacket>();
    router.addListener(listener, SCMPacket.class,
        PacketSources.EngineControllerUnit);
    tx.onSerialData(SCMPacket.encodeSCMPacket("VS", "10010"));
    SCMPacket comparePacket = new SCMPacket(
        SCMPacket.encodeSCMPacket("VS", "10010"));
    assertEquals(comparePacket, listener.lastPacket);
  }

}
