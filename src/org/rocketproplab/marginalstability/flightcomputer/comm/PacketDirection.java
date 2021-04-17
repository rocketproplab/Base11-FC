package org.rocketproplab.marginalstability.flightcomputer.comm;

/**
 * The different ways a packet can be transmitted, either sending or receiving
 *
 * @author Max Apodaca
 */
public enum PacketDirection {

  /**
   * The packet is being sent to the other component
   */
  SEND,

  /**
   * The packet is coming to the flight computer
   */
  RECIVE
}
