package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TestSCMPacket {

  @Test
  public void testParseValid() {
    SCMPacket packet = new SCMPacket("HB,12345,81;");
    assertEquals("HB", packet.getID());
    assertEquals("12345", packet.getData());
  }
  
  @Test
  public void testParseWrongChecksum() {
    SCMPacket packet = new SCMPacket("HB,12345,82;");
    assertFalse(packet.isValid());
  }
  
  @Test
  public void testShortenedPacket() {
    SCMPacket packet = new SCMPacket("HB,12345,d");
    assertFalse(packet.isValid());
  }
  
  @Test
  public void testLongenedPacket() {
    SCMPacket packet = new SCMPacket("HB,12345,82;23452");
    assertFalse(packet.isValid());
  }
  
  @Test
  public void testEncodePacket() {
    String packet = SCMPacket.encodeSCMPacket("HB", "12345");
    assertEquals("HB,12345,81;", packet);
  }
}
