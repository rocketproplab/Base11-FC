package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestGPSTransceiver {

  @Test
  public void testGPSTransciverRedirectsRecievedPacketToRouter() {
    PacketRouter   router = new PacketRouter();
    GPSTransceiver tx     = new GPSTransceiver(router);

    TestPacketListener<GPSPacket> listener = new TestPacketListener<GPSPacket>();
    
    router.addListener(listener, GPSPacket.class, PacketSources.GPS);
    
    String nEMA = "$GPGGA,420,-32,N,7,W,2,12,1.2,100000,M,-25.669,M,2.0,0031*4F";
    tx.onSerialData(nEMA);
    GPSPacket comparePacket = new GPSPacket(nEMA);

    assertEquals(comparePacket, listener.lastPacket);
  }

  @Test
  public void testGPSTransciverRedirectsPacketBasedOnSource() {
    PacketRouter   router = new PacketRouter();
    GPSTransceiver tx     = new GPSTransceiver(router);

    TestPacketListener<SCMPacket> listener = new TestPacketListener<SCMPacket>();

    router.addListener(listener, GPSPacket.class,
        PacketSources.EngineControllerUnit);
    tx.onSerialData("$GPGGA,420,-32,N,7,W,2,12,1.2,100000,M,-25.669,M,2.0,0031*4F");

    assertEquals(null, listener.lastPacket);
  }

}
