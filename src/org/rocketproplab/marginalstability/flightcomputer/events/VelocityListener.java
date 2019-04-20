package org.rocketproplab.marginalstability.flightcomputer.events;

import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

/**
 * Listens for updates in the velocity
 * @author Max Apodaca
 *
 */
public interface VelocityListener {

  /**
   * Called when a new velocity measurement is recorded
   * @param velocity the velocity which was recorded
   * @param time the time at which the measurement was recorded
   */
  public void onVelocityUpdate(Vector3 velocity, double time);
  
}
