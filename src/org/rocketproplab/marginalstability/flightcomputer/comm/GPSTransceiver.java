package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

/**
 * A class to handle the sending and receiving information from the GPS
 * 
 * @author Max Apodaca
 *
 */
public class GPSTransceiver implements SerialListener {
  private PacketRouter  router;

  /**
   * Create a new GPS Transceiver that 
   * 
   * @param router     the router to use to route packets
   */
  public GPSTransceiver(PacketRouter router) {
    this.router = router;
  }

  @Override
  public void onSerialData(String data) {
    GPSPacket packet = new GPSPacket(data);
    if (packet.isValid()) {
      router.recivePacket(packet, PacketSources.GPS);
    } else {
      System.out.println("Got invalid packet " + data + "!");
      // TODO report error
    }

  }

}
