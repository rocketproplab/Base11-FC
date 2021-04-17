package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * An on off style solenoid that can be used for a variety of purposes
 *
 * @author Max Apodaca
 */
public interface Solenoid {
  /**
   * Gets if the solenoid is currently in an active state
   *
   * @return if the solenoid is active
   */
  public boolean isActive();

  /**
   * Sets the state of the solenoid, either active or not
   *
   * @param active whether or not the solenoid is active
   */
  public void set(boolean active);
}
