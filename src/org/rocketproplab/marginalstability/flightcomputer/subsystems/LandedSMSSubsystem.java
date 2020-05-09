package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.GPSPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.events.FlightStateListener;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.SMSSender;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

/**
 * A subsystem that sends SMS messages once the rocket lands.
 *
 * @author Chi Chow
 */
public class LandedSMSSubsystem
        implements Subsystem, FlightStateListener, PacketListener<GPSPacket> {
  public static final  double SMS_INTERVAL = 15.0; // Interval to SMS, in seconds
  private static final String SMS_FORMAT   = "Landed! https://www.google.com/maps/place/%f+%f";

  private String    phoneNumber;
  private SMSSender smsSender;
  private Time      time;

  private GPSPacket  lastPacket;
  private FlightMode flightMode = null;
  private double     lastSMSTime;

  /**
   * Create a new LandedSMSSubsystem
   *
   * @param phoneNumber phone number to send SMS messages
   * @param smsSender   handles sending SMS messages
   * @param time        the rocket time
   */
  public LandedSMSSubsystem(String phoneNumber, SMSSender smsSender, Time time) {
    this.phoneNumber = phoneNumber;
    this.smsSender   = smsSender;
    this.time        = time;
    this.lastSMSTime = time.getSystemTime();
  }

  @Override
  public void onFlightModeChange(FlightMode newMode) {
    this.flightMode = newMode;
  }

  @Override
  public void update() {
    if (flightMode == FlightMode.Landed) {
      trySendSMSMessage();
    }
  }

  /**
   * Sends SMS message if the time elapsed since the last SMS message sent
   * is greater than the set time interval.
   */
  private void trySendSMSMessage() {
    double currentTime = time.getSystemTime();
    if (currentTime - lastSMSTime > SMS_INTERVAL) {
      lastSMSTime = currentTime;
      smsSender.sendMessage(phoneNumber,
              getMessage(lastPacket.getLatitude(), lastPacket.getLongitude()));
    }
  }

  /**
   * Creates the SMS message to be sent.
   * Includes a Google Maps link with the rocket's coordinates.
   *
   * @param latitude  rocket latitude
   * @param longitude rocket longitude
   * @return Complete message with Google Maps link
   */
  private static String getMessage(double latitude, double longitude) {
    return String.format(SMS_FORMAT, latitude, longitude);
  }

  @Override
  public void onPacket(PacketDirection direction, GPSPacket packet) {
    this.lastPacket = packet;
  }
}
