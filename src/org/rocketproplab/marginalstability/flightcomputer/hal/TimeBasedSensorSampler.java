package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * Provides checks for the given samplable sensor to allow sampling every time
 * new data exists.
 * 
 * @author Max
 *
 * @param <E> The type which the sensor returns
 */
public class TimeBasedSensorSampler<E> implements SamplableSensor<E> {

  private static final double EPSILON = 1E-10;

  /**
   * A free function, see {@link #getLastMeasurementTime()} for details.
   * 
   * @author Max
   *
   */
  public interface LastReadTimeFunction {

    /**
     * Get the last time a sample was captured with the guarantees from
     * {@link SamplableSensor#getLastSampleTime()}.
     * 
     * @return The time the last measurement was taken.
     */
    public double getLastMeasurementTime();
  }

  /**
   * A free function, see {@link #getNewData()} for details.
   * 
   * @author Max
   *
   * @param <E> The type of data returned
   */
  public interface GetNewDataFunction<E> {

    /**
     * Get the new datum, this can either be a queue or the most recent one but
     * should be the same as the time returned in
     * {@link LastReadTimeFunction#getLastMeasurementTime()}.
     * 
     * @return The new datum
     */
    public E getNewData();
  }

  private GetNewDataFunction<E> newDataFunction;
  private LastReadTimeFunction  lastReadTimeFunction;
  private double                lastTime;
  private boolean               firstRequest;

  /**
   * Create a new sampler the samples the given data function
   * 
   * @param newDataFunction      used to get the latest datum
   * @param lastReadTimeFunction used to get the time of the latest datum
   */
  public TimeBasedSensorSampler(GetNewDataFunction<E> newDataFunction, LastReadTimeFunction lastReadTimeFunction) {
    this.newDataFunction      = newDataFunction;
    this.lastReadTimeFunction = lastReadTimeFunction;
    this.lastTime             = 0;
    this.firstRequest         = true;
  }

  @Override
  public boolean hasNewData() {
    double  lastRead       = this.lastReadTimeFunction.getLastMeasurementTime() - lastTime;
    boolean hasReachedTime = lastRead > EPSILON;
    return this.firstRequest || hasReachedTime;
  }

  @Override
  public E getNewData() {
    this.firstRequest = false;
    this.lastTime     = this.lastReadTimeFunction.getLastMeasurementTime();
    return this.newDataFunction.getNewData();
  }

  @Override
  public double getLastSampleTime() {
    return this.lastTime;
  }

}
