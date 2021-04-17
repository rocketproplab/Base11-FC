package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.GPSPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightState;

public class GPSMessageSubsystem implements PacketListener {

  String message;
  double lastTime;
  double thisTime;
  GPSPacket packet;
  FlightState currentState;

  private static final double TIME_BETWEEN = 0;

  public GPSMessageSubsystem() {
    lastTime     = 0;
    thisTime     = 0;
    currentState = new FlightState();
  }

  public String createMessage() {
    double latitude  = this.packet.getLatitude();
    double longitude = this.packet.getLongitude();

    message = "The longitude is: " + longitude + "\nThe latitude"
            + " is: " + latitude;

    return message;
    // how do we get message into string(data) if the phone number
    //is passed in the method call and we don't necessarily have
    //the phone number yet?
  }

  @Override
  public void onPacket(PacketDirection direction, Object packet) {
    if (direction == PacketDirection.RECIVE) {
      //should include, but i can't test with that in
      //&& currentState.getFlightMode() == FlightMode.Landed
      thisTime = (new Time()).getSystemTime();
      if (thisTime - lastTime > TIME_BETWEEN) {
        this.packet = (GPSPacket) packet;
      }
    }
  }

}
