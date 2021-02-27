package org.rocketproplab.marginalstability.flightcomputer;

/**
 * A class to get the current rocket time. At the moment its implementation
 * passed through to System.currentTimeMillis but this might change.
 * 
 * @author Max Apodaca
 *
 */
public class Time {

  /**
   * Get the rocket time, this might change at some point in the future. This
   * value should be used for all interpolations.
   * 
   * @return the current rocket time
   */
  public double getSystemTime() {
    return System.currentTimeMillis() / Settings.MS_PER_SECOND;
  }
}
