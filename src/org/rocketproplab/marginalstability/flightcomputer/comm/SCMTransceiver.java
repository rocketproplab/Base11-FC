package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.ErrorReporter;
import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.SerialPort;

/**
 * A class to handle the sending and receiving information from the any SCM
 * source. <br>
 * It listens for serial data and when a valid SCM packet is received in one
 * chunk it will notify the packet router of the new packet. It uses the
 * specified source for this as we can have multiple SCM packet sources. See
 * {@link PacketSources} for more info about packet sources.
 * 
 * @author Max Apodaca, Antonio
 *
 */
public class SCMTransceiver implements SerialListener, PacketListener<SCMPacket> {
  private SerialPort    serialPort;
  private PacketRouter  router;
  private PacketSources source;

  /**
   * Create a new SCM Transceiver that will use this serial port to send and
   * receive data.
   * 
   * @param serialPort The serial port to send data to
   * @param router     the router to use to route packets
   * @param source     where the SCM is connected to
   */
  public SCMTransceiver(SerialPort serialPort, PacketRouter router, PacketSources source) {
    this.serialPort = serialPort;
    this.router     = router;
    this.source     = source;
  }

  @Override
  public void onSerialData(String data) {
    SCMPacket packet = new SCMPacket(data);
    if (packet.isValid()) {
      router.recivePacket(packet, this.source);
    } else {
      ErrorReporter errorReporter = ErrorReporter.getInstance();
      String errorMsg = "Got invalid packet " + data + "!";
      errorReporter.reportError(null, null, errorMsg);
    }

  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {
    if (packet.isValid() && direction == PacketDirection.SEND) {
      this.serialPort.write(packet.toString());
    }
  }

}
