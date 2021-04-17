package org.rocketproplab.marginalstability.flightcomputer.comm;

/**
 * This interface has the ability to send a packet to a given location
 *
 * @author Max Apodaca
 */
public interface PacketRelay {

  /**
   * Sends a packet to the associated listeners.
   *
   * @param o      the packet to send (must be of packet type)
   * @param source where the packet is coming from
   */
  public void sendPacket(Object o, PacketSources source);

}
