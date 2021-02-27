package org.rocketproplab.marginalstability.flightcomputer.hal;

public interface Thermocouple {
  /**
   * Get the temperature in celsius
   * 
   * @return the temperature in celsius
   */
  public double getTemperature();

  /**
   * Gets the time at which the last measurement was made
   * 
   * @return the time of the last measurements
   */
  public double getLastMeasurementTime();
  
  /**
   * Checks if temperature is within logical bounds of the sensor
   * 
   * @return if temperature is in usable range
   */
  public boolean inUsableRange();
}