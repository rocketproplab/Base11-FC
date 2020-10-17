package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * An interface for an accelerometer and gyroscope sensor. A sensor implementing
 * this interface will give an iterable list of AccelGyroReadings. If hasNext
 * returns true there will be at least another accelerometer and gyroscope
 * reading.
 * 
 * @author max
 *
 */
public interface AccelerometerGyroscope {

  /**
   * Read the next accelerometer and gyroscope reading, returns null if
   * {@link #hasNext()} returns false.
   * 
   * @return the next accelerometer and gyroscope reading or null.
   */
  public AccelGyroReading getNext();

  /**
   * Determines if there is another reading from the accelerometer/gyroscope
   * that can be read with {@link #getNext()}
   * 
   * @return true if {@link #getNext()} will return a new accelerometer and
   * gyroscope reading, false otherwise.
   */
  public boolean hasNext();

}
