package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class FramedSCMTest {
  private class SendPacketTuple {
    public Object        o;
    public PacketSources source;

    public SendPacketTuple(Object o, PacketSources source) {
      this.o      = o;
      this.source = source;
    }
  }

  private class FakePacketRelay implements PacketRelay {
    public ArrayList<SendPacketTuple> sentPackets = new ArrayList<>();

    @Override
    public void sendPacket(Object o, PacketSources source) {
      this.sentPackets.add(new SendPacketTuple(o, source));
    }

  }

  @Test
  public void noCompletedMessageWithoutPackets() {
    FramedSCM framedSCM = new FramedSCM(null);
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void singleFramePacketIsImmediatlyAvaliable() {
    FramedSCM framedSCM      = new FramedSCM(null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "1|A  ");
    SCMPacket ack            = framedSCM.processNextPacket(incomingPacket);
    assertEquals(SCMPacketType.X1, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("A", framedSCM.getCompletedMessage());

  }

  @Test
  public void afterRemovingCompletedMessageNoLongerHasMessage() {
    FramedSCM framedSCM      = new FramedSCM(null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "1|A  ");
    framedSCM.processNextPacket(incomingPacket);
    framedSCM.getCompletedMessage();
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void twoFramePacketIsReconstructedAcrossFrames() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "8|ABC");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "DEFGH");
    SCMPacket ack             = framedSCM.processNextPacket(incomingPacket);
    assertEquals(SCMPacketType.XB, ack.getID());
    assertFalse(framedSCM.hasCompletedMessage());

    ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XA, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());

    assertEquals("ABCDEFGH", framedSCM.getCompletedMessage());
  }

  @Test
  public void twoFramePacketIsCutShort() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "4|ABC");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "D    ");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);

    assertEquals(SCMPacketType.XA, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABCD", framedSCM.getCompletedMessage());
  }

  @Test
  public void threeFramePacketIsReconstructed() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "11|He");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "llo W");
    SCMPacket incomingPacket3 = new SCMPacket(SCMPacketType.X1, "orld ");
    SCMPacket ack             = framedSCM.processNextPacket(incomingPacket);
    assertEquals(SCMPacketType.XB, ack.getID());
    assertFalse(framedSCM.hasCompletedMessage());

    ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XA, ack.getID());
    assertFalse(framedSCM.hasCompletedMessage());

    ack = framedSCM.processNextPacket(incomingPacket3);
    assertEquals(SCMPacketType.XB, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());

    assertEquals("Hello World", framedSCM.getCompletedMessage());
  }

  @Test
  public void repeatedPacketIsIgnored() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "4|ABC");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "D    ");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABCD", framedSCM.getCompletedMessage());
    SCMPacket ack2 = framedSCM.processNextPacket(incomingPacket2);

    assertEquals(SCMPacketType.XA, ack.getID());
    assertEquals(SCMPacketType.XA, ack2.getID());
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void restartInMiddleIsTolerated() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "4|ABC");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.XS, "2|ABC");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);

    assertEquals(SCMPacketType.XB, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("AB", framedSCM.getCompletedMessage());
  }

  @Test
  public void numberOverTwoFramesIsTolerated() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "00000");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "3|ABC");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XB, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABC", framedSCM.getCompletedMessage());
  }

  @Test
  public void badPacketIsIgnored() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "00000");
    SCMPacket incomingPacket2 = new SCMPacket("X0,3|ABC,98;");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertNull(ack);
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void canQueueMultipleMessages() {
    FramedSCM framedSCM       = new FramedSCM(null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "2|ABC");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.XS, "2|CDE");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);

    assertEquals(SCMPacketType.XB, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("AB", framedSCM.getCompletedMessage());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("CD", framedSCM.getCompletedMessage());
  }

  @Test
  public void onPacketAlsoRelaysPacketsToPacketRelay() {
    FakePacketRelay packetRelay = new FakePacketRelay();
    FramedSCM framedSCM       = new FramedSCM(packetRelay);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "5|ABC");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "2|CDE");
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket);
    assertFalse(framedSCM.hasCompletedMessage());
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket2);
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABC2|", framedSCM.getCompletedMessage());
    
    assertEquals(2, packetRelay.sentPackets.size());
    
    SCMPacket ack0 = (SCMPacket) packetRelay.sentPackets.get(0).o;
    assertEquals(SCMPacketType.X1, ack0.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(0).source);
    
    SCMPacket ack1 = (SCMPacket) packetRelay.sentPackets.get(1).o;
    assertEquals(SCMPacketType.X0, ack1.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(1).source);
  }
  
  @Test
  public void packetDirectionSendDoesNotCauseError() {
    FakePacketRelay packetRelay = new FakePacketRelay();
    FramedSCM framedSCM       = new FramedSCM(packetRelay);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "5|ABC");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "XYZWW");
    SCMPacket incomingPacket3 = new SCMPacket(SCMPacketType.X0, "2|CDE");
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket);
    assertFalse(framedSCM.hasCompletedMessage());
    framedSCM.onPacket(PacketDirection.SEND, incomingPacket2);
    assertFalse(framedSCM.hasCompletedMessage());
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket3);
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABC2|", framedSCM.getCompletedMessage());
    
    assertEquals(2, packetRelay.sentPackets.size());
    
    SCMPacket ack0 = (SCMPacket) packetRelay.sentPackets.get(0).o;
    assertEquals(SCMPacketType.X1, ack0.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(0).source);
    
    SCMPacket ack1 = (SCMPacket) packetRelay.sentPackets.get(1).o;
    assertEquals(SCMPacketType.X0, ack1.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(1).source);
  }

}
