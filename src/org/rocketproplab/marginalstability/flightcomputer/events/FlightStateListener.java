package org.rocketproplab.marginalstability.flightcomputer.events;

import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

public interface FlightStateListener {

  /**
   * Called when the flight mode changes
   * @param newMode the new flight mode we are in
   */
  public void onFlightModeChange(FlightMode newMode);
  
}
