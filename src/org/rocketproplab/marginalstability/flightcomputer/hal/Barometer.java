package org.rocketproplab.marginalstability.flightcomputer.hal;

public interface Barometer {
  /**
   * Get the pressure in hPa
   * 
   * @return get the pressure in hPa
   */
  public double getPressure();

  /**
   * Returns if the pressure is in a usable range. If the sensor is in too low or
   * too high air pressure the data we get might be garbage.
   * 
   * @return if the current pressure is in a usable range
   */
  public boolean inUsableRange();

  /**
   * Gets the time at which the last measurement was made
   * 
   * @return the time of the last measurements
   */
  public double getLastMeasurementTime();
}
