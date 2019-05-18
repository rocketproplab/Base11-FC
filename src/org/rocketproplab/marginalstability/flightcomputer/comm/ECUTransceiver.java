package org.rocketproplab.marginalstability.flightcomputer.comm;

import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.SerialPort;

/**
 * A class to handle the sending and receiving information from the ECU serial
 * port.
 * 
 * @author Max Apodaca, Antonio
 *
 */
public class ECUTransceiver
    implements SerialListener, PacketListener<SCMPacket> {
  private SerialPort   serialPort;
  private PacketRouter router;

  /**
   * Create a new ECU Transceiver that will use this serial port to send and
   * receive data.
   * 
   * @param serialPort The serial port to send data to
   * @param router     the router to use to route packets
   */
  public ECUTransceiver(SerialPort serialPort, PacketRouter router) {
    this.serialPort = serialPort;
    this.router     = router;
  }

  @Override
  public void onSerialData(String data) {
    SCMPacket packet = new SCMPacket(data);
    if (packet.isValid()) {
      router.recivePacket(packet, PacketSources.EngineControllerUnit);
    } else {
      System.out.println("Got invalid packet " + data + "!");
      // TODO report error
    }

  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {
    if (packet.isValid() && direction == PacketDirection.SEND) {
      this.serialPort.write(packet.toString());
    }
  }

}
