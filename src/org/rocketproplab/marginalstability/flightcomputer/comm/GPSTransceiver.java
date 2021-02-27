package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.ErrorReporter;
import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

/**
 * A class to handle the sending and receiving information from the GPS. It will
 * Listener to a serial port and every time a valid full NMEA packet is received
 * it will parse the packet and send it to the packet router.
 * 
 * @author Max Apodaca
 *
 */
public class GPSTransceiver implements SerialListener {
  private PacketRouter router;

  /**
   * Create a new GPS Transceiver that
   * 
   * @param router the router to use to route packets
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
      ErrorReporter errorReporter = ErrorReporter.getInstance();
      String errorMsg = "Got invalid packet " + data + "!\"";
      errorReporter.reportError(errorMsg);
    }

  }

}
