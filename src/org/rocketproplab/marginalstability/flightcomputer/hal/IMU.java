package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * An interface for an Inertial Measurement Unit. An IMU implementing this
 * interface will give an iterable list of IMUReadings. If hasNext returns true
 * there will be at least another IMU Reading.
 * 
 * @author max
 *
 */
public interface IMU {

  /**
   * Read the next IMU reading, returns null if {@link #hasNext()} returns false.
   * 
   * @return the next IMU reading or null.
   */
  public IMUReading getNext();

  /**
   * Determines if there is another IMU reading that can be read with
   * {@link #getNext()}
   * 
   * @return true if {@link #getNext()} will return a new IMU reading, false
   *         otherwise.
   */
  public boolean hasNext();

}
