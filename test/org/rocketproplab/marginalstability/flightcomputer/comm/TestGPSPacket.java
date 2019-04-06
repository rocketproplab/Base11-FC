package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestGPSPacket {

  private static final double EPSILON = 0.00000001;

  @Test
  public void gPSReadsValidDataFromNEMA() {
    String    nEMA   = "$GPGGA,172814.0,3723.46587704,N,12202.26957864,W,2,6,1.2,"
        + "18.893,M,-25.669,M,2.0,0031*4F";
    GPSPacket packet = new GPSPacket(nEMA);

    assertTrue(packet.isValid());
    assertEquals(172814, packet.getTime(), EPSILON);
    assertEquals(3723.46587704, packet.getLatitude(), EPSILON);
    assertEquals(12202.26957864, packet.getLongitude(), EPSILON);
    assertEquals(18.893, packet.getAltitude(), EPSILON);
    assertEquals(6, packet.getSVCount());

    nEMA = "$GPGGA,420,-32,N,7,W,2,12,1.2,100000,M,-25.669,M,2.0,0031*4F";

    packet = new GPSPacket(nEMA);

    assertTrue(packet.isValid());
    assertEquals(420, packet.getTime(), EPSILON);
    assertEquals(-32, packet.getLatitude(), EPSILON);
    assertEquals(7, packet.getLongitude(), EPSILON);
    assertEquals(100000, packet.getAltitude(), EPSILON);
    assertEquals(12, packet.getSVCount());
  }

}
