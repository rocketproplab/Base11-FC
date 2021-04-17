package org.rocketproplab.marginalstability.flightcomputer.comm;

/**
 * The different places packets can go and come from.
 *
 * @author Max Apodaca
 */
public enum PacketSources {

  /**
   * The packet is coming from or going to the Engine Controller Unit (ECU)
   */
  EngineControllerUnit,

  /**
   * The packet is coming from or going to the command box
   */
  CommandBox,

  /**
   * The packet is coming from or going to the GPS
   */
  GPS,

  /**
   * This packet is coming from or going to the Auxiliary GPS
   */
  AUX_GPS
}
