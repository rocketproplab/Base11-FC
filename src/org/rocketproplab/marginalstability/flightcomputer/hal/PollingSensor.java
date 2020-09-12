package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * A sensor that can be polled.
 * 
 * @author Max Apodaca
 *
 */
public interface PollingSensor {
  /**
   * Polls the sensor
   */
  public void poll();
}
