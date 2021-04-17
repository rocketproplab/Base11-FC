package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * Sensor that exposes a standardized samplable interface
 *
 * @param <E> The type that the sensor returns
 * @author Max
 */
public interface SamplableSensor<E> {

  /**
   * If the sensor has new data to sample
   *
   * @return if the sensor has new data
   */
  public boolean hasNewData();

  /**
   * Get the next datum from the sensor. This can either be a queue or the latest
   * measurement.
   *
   * @return The next datum
   */
  public E getNewData();

  /**
   * Return when the datum that was last returned by {@link #getNewData()} was
   * captured.
   *
   * @return the capture time of the datum returned by {@link #getNewData()}
   */
  public double getLastSampleTime();
}
