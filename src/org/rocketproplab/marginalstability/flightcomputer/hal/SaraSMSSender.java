package org.rocketproplab.marginalstability.flightcomputer.hal;

import org.rocketproplab.marginalstability.flightcomputer.comm.GPSPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

public class SaraSMSSender implements SMSSender, SerialListener {

  SerialPort saraSerialPort;
  String     phoneNumber;
  String     at;
  String     message;
  int        messageIndex;
  private PacketRouter router;
  GPSPacket packet;

  public SaraSMSSender(SerialPort saraSerialPort) {
    this.saraSerialPort = saraSerialPort;
    messageIndex        = 0;
    router              = new PacketRouter(); //should this be set to a specific packet router?

    saraSerialPort.write("AT\n");
    saraSerialPort.write("AT+CMGF=1\n");
  }

  @Override
  public void sendTextMessage(String number, String data) {
    //TODO: Add in a listener that would allow us to check if the AT
    //is on, if it's been switched the SMS mode, and if the message
    //was sent.
    if (number.length() != 11 || data.length() > 160) {
      throw new IllegalArgumentException();
    } else {
      saraSerialPort.write("AT+CMGS=\"+" + number + "\"\n");
      saraSerialPort.write(data + "\r\n");
    }
  }

  public void getGPSInfo() {
    saraSerialPort.write("AT+UGPS=1\n"); //turns the gps on.
    saraSerialPort.write("AT+UGGGA?\r\n");
  }

  @Override //this re
  public void onSerialData(String data) {
    int    index   = data.indexOf(",");
    String subData = data.substring(index + 1);

    packet = new GPSPacket(subData);
    router.recivePacket(packet, PacketSources.AUX_GPS);
    //should this throw an error if null data is sent?
    //or should it just be thrown to the wayside?
    //
  }

  //we need to figure out a way to send a message to the
  //sara, but so taht it can differentiate between texting
  // and not texting

}
