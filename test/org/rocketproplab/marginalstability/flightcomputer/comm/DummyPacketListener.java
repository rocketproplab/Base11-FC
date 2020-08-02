package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/**
 * A class to test packet listener effects. Holds the last received
 * variables.
 * @author Max Apodaca
 */
public class DummyPacketListener<E> implements PacketListener<E> {

  /**
   * The last packet received
   */
  public E               lastPacket;
  
  /**
   * The last packet's direction
   */
  public PacketDirection lastDirection;

  @Override
  public void onPacket(PacketDirection direction, E packet) {
    this.lastPacket    = packet;
    this.lastDirection = direction;
  }

}