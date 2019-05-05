package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

public class TestPacketRouter {

  private PacketRouter            router;
  private TestListener<SCMPacket> scmListener;
  private TestListener<GPSPacket> gpsListener;

  private class TestListener<E> implements PacketListener<E> {

    private E               lastPacket;
    private PacketDirection lastDirection;

    @Override
    public void onPacket(PacketDirection direction, E packet) {
      this.lastPacket    = packet;
      this.lastDirection = direction;
    }

  }

  @Before
  public void before() {
    router      = new PacketRouter();
    scmListener = new TestListener<SCMPacket>();
    gpsListener = new TestListener<GPSPacket>();
  }

  @Test
  public void testPacketRouterForwardsCorrectPacket() {
    router.addListener(gpsListener, GPSPacket.class, PacketSources.GPS);
    GPSPacket gpsPacket = new GPSPacket("");
    router.recivePacket(gpsPacket, PacketSources.GPS);
    assertEquals(gpsPacket, gpsListener.lastPacket);
    assertEquals(PacketDirection.RECIVE, gpsListener.lastDirection);
  }

  @Test
  public void testPacketRouterDoesNotForwardBadPacket() {
    router.addListener(gpsListener, GPSPacket.class, PacketSources.GPS);
    SCMPacket scmPacket = new SCMPacket("");
    router.recivePacket(scmPacket, PacketSources.CommandBox);
    assertEquals(null, gpsListener.lastPacket);
    assertEquals(null, gpsListener.lastDirection);
  }

  @Test
  public void testPacketRouterFowardsCorrectDirection() {
    router.addListener(scmListener, SCMPacket.class, PacketSources.CommandBox);
    SCMPacket scmPacket = new SCMPacket("");
    router.recivePacket(scmPacket, PacketSources.CommandBox);
    assertEquals(scmPacket, scmListener.lastPacket);
    assertEquals(PacketDirection.RECIVE, scmListener.lastDirection);
  }
  
  @Test
  public void testPacketRouterFowardsTwoPacketsIndependently() {
    router.addListener(scmListener, SCMPacket.class, PacketSources.CommandBox);
    router.addListener(gpsListener, GPSPacket.class, PacketSources.GPS);
    SCMPacket scmPacket = new SCMPacket("");
    GPSPacket gpsPacket = new GPSPacket("");
    router.sendPacket(scmPacket, PacketSources.CommandBox);
    router.recivePacket(gpsPacket, PacketSources.GPS);
    assertEquals(scmPacket, scmListener.lastPacket);
    assertEquals(PacketDirection.SEND, scmListener.lastDirection);
    
    assertEquals(gpsPacket, gpsListener.lastPacket);
    assertEquals(PacketDirection.RECIVE, gpsListener.lastDirection);
  }
  
  @Test
  public void testPacketRouterDiscernsTwoSources() {
    router.addListener(scmListener, SCMPacket.class, PacketSources.CommandBox);
    SCMPacket scmPacket = new SCMPacket("");
    SCMPacket scmPacket2 = new SCMPacket("");
    router.sendPacket(scmPacket, PacketSources.CommandBox);
    router.sendPacket(scmPacket2, PacketSources.EngineControllerUnit);
    assertEquals(scmPacket, scmListener.lastPacket);
    assertEquals(PacketDirection.SEND, scmListener.lastDirection);
  }

}
