package org.rocketproplab.marginalstability.flightcomputer.events;

/**
 * A listener for the serial port. It is notified when the serial port receives
 * a new message.
 *
 * @author Max Apodaca, Antonio
 */
public interface SerialListener {

  /**
   * Called each time the serial port receives a string
   *
   * @param data the data that was received
   */
  public void onSerialData(String data);
}