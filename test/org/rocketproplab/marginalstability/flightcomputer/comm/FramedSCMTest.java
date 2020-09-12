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

  private class FakeFramedPacketProcessor implements FramedPacketProcessor {
    public int               foo        = 8;
    public ArrayList<String> sentFrames = new ArrayList<>();

    @Override
    public void processFramedPacket(String framedPacket) {
      this.sentFrames.add(framedPacket);
    }

  }

  @Test
  public void noCompletedMessageWithoutPackets() {
    FramedSCM framedSCM = new FramedSCM(null, null);
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void singleFramePacketIsImmediatlyAvaliable() {
    FramedSCM framedSCM      = new FramedSCM(null, null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "1|A  ");
    SCMPacket ack            = framedSCM.processNextPacket(incomingPacket);
    assertEquals(SCMPacketType.XB, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("A", framedSCM.getCompletedMessage());

  }

  @Test
  public void singleSpacePacketIsImmediatlyAvaliable() {
    FramedSCM framedSCM      = new FramedSCM(null, null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "1|   ");
    SCMPacket ack            = framedSCM.processNextPacket(incomingPacket);
    assertEquals(SCMPacketType.XB, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals(" ", framedSCM.getCompletedMessage());

  }

  @Test
  public void afterRemovingCompletedMessageNoLongerHasMessage() {
    FramedSCM framedSCM      = new FramedSCM(null, null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "1|A  ");
    framedSCM.processNextPacket(incomingPacket);
    framedSCM.getCompletedMessage();
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void twoFramePacketIsReconstructedAcrossFrames() {
    FramedSCM framedSCM       = new FramedSCM(null, null);
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
    FramedSCM framedSCM       = new FramedSCM(null, null);
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
    FramedSCM framedSCM       = new FramedSCM(null, null);
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
    FramedSCM framedSCM       = new FramedSCM(null, null);
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
    FramedSCM framedSCM       = new FramedSCM(null, null);
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
    FramedSCM framedSCM       = new FramedSCM(null, null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "00000");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "3|ABC");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XA, ack.getID());
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABC", framedSCM.getCompletedMessage());
  }

  @Test
  public void framelengthOverTwoPackets() {
    FramedSCM framedSCM       = new FramedSCM(null, null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "00001");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "0|ABC");
    SCMPacket incomingPacket3 = new SCMPacket(SCMPacketType.X1, "DEFGH");
    SCMPacket incomingPacket4 = new SCMPacket(SCMPacketType.X0, "IJ   ");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XA, ack.getID());

    SCMPacket ack2 = framedSCM.processNextPacket(incomingPacket3);
    assertEquals(SCMPacketType.XB, ack2.getID());

    SCMPacket ack3 = framedSCM.processNextPacket(incomingPacket4);
    assertEquals(SCMPacketType.XA, ack3.getID());

    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABCDEFGHIJ", framedSCM.getCompletedMessage());
  }

  @Test
  public void framelengthOverTwoPacketsDouble() {
    FramedSCM framedSCM       = new FramedSCM(null, null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "00001");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "0|ABC");
    SCMPacket incomingPacket3 = new SCMPacket(SCMPacketType.X1, "DEFGH");
    SCMPacket incomingPacket4 = new SCMPacket(SCMPacketType.X0, "IJ   ");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XA, ack.getID());

    SCMPacket ack2 = framedSCM.processNextPacket(incomingPacket3);
    assertEquals(SCMPacketType.XB, ack2.getID());

    SCMPacket ack3 = framedSCM.processNextPacket(incomingPacket4);
    assertEquals(SCMPacketType.XA, ack3.getID());

    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABCDEFGHIJ", framedSCM.getCompletedMessage());

    incomingPacket  = new SCMPacket(SCMPacketType.XS, "00001");
    incomingPacket2 = new SCMPacket(SCMPacketType.X0, "0|ABC");
    incomingPacket3 = new SCMPacket(SCMPacketType.X1, "DEFGH");
    incomingPacket4 = new SCMPacket(SCMPacketType.X0, "IJ   ");
    framedSCM.processNextPacket(incomingPacket);
    ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XA, ack.getID());

    ack2 = framedSCM.processNextPacket(incomingPacket3);
    assertEquals(SCMPacketType.XB, ack2.getID());

    ack3 = framedSCM.processNextPacket(incomingPacket4);
    assertEquals(SCMPacketType.XA, ack3.getID());

    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("ABCDEFGHIJ", framedSCM.getCompletedMessage());
  }

  @Test
  public void invalidCharacaterInFrameLength() {
    FramedSCM framedSCM      = new FramedSCM(null, null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "z3|AB");
    SCMPacket ack            = framedSCM.processNextPacket(incomingPacket);
    assertNull(ack);
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void negFrameLengthIgnored() {
    FramedSCM framedSCM      = new FramedSCM(null, null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "-3|AB");
    SCMPacket ack            = framedSCM.processNextPacket(incomingPacket);
    assertNull(ack);
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void leadingZeroIsValid() {
    FramedSCM framedSCM      = new FramedSCM(null, null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "03|AB");
    SCMPacket ack            = framedSCM.processNextPacket(incomingPacket);
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void zeroFrameLengthWorks() {
    FramedSCM framedSCM      = new FramedSCM(null, null);
    SCMPacket incomingPacket = new SCMPacket(SCMPacketType.XS, "0|ABC");
    SCMPacket ack            = framedSCM.processNextPacket(incomingPacket);
    assertTrue(framedSCM.hasCompletedMessage());
    assertEquals("", framedSCM.getCompletedMessage());
    assertEquals(SCMPacketType.XB, ack.getID());
  }

  @Test
  public void badPacketIsIgnored() {
    FramedSCM framedSCM       = new FramedSCM(null, null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "00000");
    SCMPacket incomingPacket2 = new SCMPacket("X0,3|ABC,98;");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertNull(ack);
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void pipeAsLastChar() {
    FramedSCM framedSCM       = new FramedSCM(null, null);
    SCMPacket incomingPacket  = new SCMPacket(SCMPacketType.XS, "1354|");
    SCMPacket incomingPacket2 = new SCMPacket(SCMPacketType.X0, "3|ABC");
    framedSCM.processNextPacket(incomingPacket);
    SCMPacket ack = framedSCM.processNextPacket(incomingPacket2);
    assertEquals(SCMPacketType.XA, ack.getID());
    assertFalse(framedSCM.hasCompletedMessage());
  }

  @Test
  public void canQueueMultipleMessages() {
    FramedSCM framedSCM       = new FramedSCM(null, null);
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
    FakePacketRelay           packetRelay           = new FakePacketRelay();
    FakeFramedPacketProcessor framedPacketProcessor = new FakeFramedPacketProcessor();
    FramedSCM                 framedSCM             = new FramedSCM(packetRelay, framedPacketProcessor);
    SCMPacket                 incomingPacket        = new SCMPacket(SCMPacketType.XS, "5|ABC");
    SCMPacket                 incomingPacket2       = new SCMPacket(SCMPacketType.X0, "2|CDE");
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket);
    assertEquals(0, framedPacketProcessor.sentFrames.size());
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket2);
    assertEquals(1, framedPacketProcessor.sentFrames.size());
    assertEquals("ABC2|", framedPacketProcessor.sentFrames.get(0));

    assertEquals(2, packetRelay.sentPackets.size());

    SCMPacket ack0 = (SCMPacket) packetRelay.sentPackets.get(0).o;
    assertEquals(SCMPacketType.XB, ack0.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(0).source);

    SCMPacket ack1 = (SCMPacket) packetRelay.sentPackets.get(1).o;
    assertEquals(SCMPacketType.XA, ack1.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(1).source);
  }

  @Test
  public void packetDirectionSendDoesNotCauseError() {
    FakePacketRelay           packetRelay           = new FakePacketRelay();
    FakeFramedPacketProcessor framedPacketProcessor = new FakeFramedPacketProcessor();
    FramedSCM                 framedSCM             = new FramedSCM(packetRelay, framedPacketProcessor);
    SCMPacket                 incomingPacket        = new SCMPacket(SCMPacketType.XS, "5|ABC");
    SCMPacket                 incomingPacket2       = new SCMPacket(SCMPacketType.X0, "XYZWW");
    SCMPacket                 incomingPacket3       = new SCMPacket(SCMPacketType.X0, "2|CDE");
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket);
    assertEquals(0, framedPacketProcessor.sentFrames.size());
    framedSCM.onPacket(PacketDirection.SEND, incomingPacket2);
    assertEquals(0, framedPacketProcessor.sentFrames.size());
    framedSCM.onPacket(PacketDirection.RECIVE, incomingPacket3);
    assertEquals(1, framedPacketProcessor.sentFrames.size());
    assertEquals("ABC2|", framedPacketProcessor.sentFrames.get(0));

    assertEquals(2, packetRelay.sentPackets.size());

    SCMPacket ack0 = (SCMPacket) packetRelay.sentPackets.get(0).o;
    assertEquals(SCMPacketType.XB, ack0.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(0).source);

    SCMPacket ack1 = (SCMPacket) packetRelay.sentPackets.get(1).o;
    assertEquals(SCMPacketType.XA, ack1.getID());
    assertEquals(PacketSources.CommandBox, packetRelay.sentPackets.get(1).source);
  }

}
