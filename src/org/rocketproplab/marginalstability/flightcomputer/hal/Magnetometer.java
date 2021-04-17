package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * An interface for a magnetometer sensor. A sensor implementing this interface
 * will give an iterable list of MagReadings. If hasNext returns true there will
 * be at least another magnetometer reading.
 *
 * @author max
 */
public interface Magnetometer {

  /**
   * Read the next magnetometer reading, returns null if {@link #hasNext()}
   * returns false.
   *
   * @return the next magnetometer reading or null.
   */
  public MagReading getNext();

  /**
   * Determines if there is another reading from the magnetometer that can be
   * read with {@link #getNext()}
   *
   * @return true if {@link #getNext()} will return a new magnetometer reading,
   * false otherwise.
   */
  public boolean hasNext();

}
