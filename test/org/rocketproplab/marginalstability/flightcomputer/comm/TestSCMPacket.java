package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestSCMPacket {

  @Test
  public void testParseValid() {
    SCMPacket packet = new SCMPacket("HB,12345,81;");
    assertEquals(SCMPacketType.HB, packet.getID());
    assertEquals("12345", packet.getData());
  }
  
  @Test
  public void testParseWrongChecksum() {
    SCMPacket packet = new SCMPacket("HB,12345,82;");
    assertFalse(packet.isValid());
  }
  
  @Test
  public void testNonintegerChecksum() {
    SCMPacket packet = new SCMPacket("HB,12345,dd;");
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
    SCMPacket packet = new SCMPacket(SCMPacketType.HB, "12345");
    assertEquals("HB,12345,81;", packet.toString());
    assertTrue(packet.isValid());
  }
  
  @Test
  public void testCreateWithInvalidLengthGivesInvalidPacket() {
    SCMPacket packet = new SCMPacket(SCMPacketType.VS, "1234");
    assertFalse(packet.isValid());
  }
  
  @Test
  public void testCreateWithNullTypeGivesInvalidPacket() {
    SCMPacket packet = new SCMPacket(null, "12345");
    assertFalse(packet.isValid());
  }
  
  @Test
  public void testCreateWithNullDataGivesInvalidPacket() {
    SCMPacket packet = new SCMPacket(SCMPacketType.HB, null);
    assertFalse(packet.isValid());
  }
  
  @Test
  public void testInvalidPacketDoesNotThrowErrorOnToString() {
    SCMPacket packet = new SCMPacket(null, null);
    packet.toString();
  }
  
  @Test
  public void testEqualPacketsAreEqual() {
    SCMPacket packetOne = new SCMPacket(SCMPacketType.HB, "11100");
    SCMPacket packetTwo = new SCMPacket(SCMPacketType.HB, "11100");
    assertEquals(packetOne, packetTwo);
    assertEquals(packetTwo, packetOne);
  }
  
  @Test
  public void testNullIDDoesNotCauseExceptionInEquals() {
    SCMPacket packetOne = new SCMPacket(null, "11100");
    SCMPacket packetTwo = new SCMPacket(SCMPacketType.HB, "11100");
    assertNotEquals(packetOne, packetTwo);
    assertNotEquals(packetTwo, packetOne);
  }
  
  @Test
  public void testNullStringDoesNotCauseExceptionInEquals() {
    SCMPacket packetOne = new SCMPacket(SCMPacketType.HB, null);
    SCMPacket packetTwo = new SCMPacket(SCMPacketType.HB, "11100");
    assertNotEquals(packetOne, packetTwo);
    assertNotEquals(packetTwo, packetOne);
  }
  
  @Test
  public void testNullEverythingDoesNotCauseExceptionInEquals() {
    SCMPacket packetOne = new SCMPacket(null, null);
    SCMPacket packetTwo = new SCMPacket(SCMPacketType.HB, "11100");
    assertNotEquals(packetOne, packetTwo);
    assertNotEquals(packetTwo, packetOne);
  }
  
  @Test
  public void testNullOtherObjectDoesNotCauseExceptionInEquals() {
    SCMPacket packetOne = new SCMPacket(null, null);
    SCMPacket packetTwo = new SCMPacket(SCMPacketType.HB, "11100");
    assertNotEquals(packetOne, null);
    assertNotEquals(packetTwo, null);
  }
  
  @Test
  public void testUnequalPacketsDueToString() {
    SCMPacket packetOne = new SCMPacket(SCMPacketType.HB, "11000");
    SCMPacket packetTwo = new SCMPacket(SCMPacketType.HB, "11100");
    assertNotEquals(packetOne, packetTwo);
    assertNotEquals(packetTwo, packetOne);
  }
  
  @Test
  public void testUnequalPacketsDueToID() {
    SCMPacket packetOne = new SCMPacket(SCMPacketType.VS, "11000");
    SCMPacket packetTwo = new SCMPacket(SCMPacketType.HB, "11000");
    assertNotEquals(packetOne, packetTwo);
    assertNotEquals(packetTwo, packetOne);
  }
  
  @Test
  public void testBadPacketIDDoesNotCauseException() {
    SCMPacket packetOne = new SCMPacket("``,10000,21;");
    assertFalse(packetOne.isValid());
  }
}
