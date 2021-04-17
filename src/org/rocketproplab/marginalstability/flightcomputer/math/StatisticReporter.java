package org.rocketproplab.marginalstability.flightcomputer.math;

import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.hal.SamplableSensor;
import org.rocketproplab.marginalstability.flightcomputer.looper.EventCallback;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

/**
 * A Class to report statistics to the telemetry subsystem.
 *
 * @author Max
 */
public class StatisticReporter implements EventCallback {

  private Telemetry telemetry;
  private SCMPacketType meanType;
  private SCMPacketType varianceType;
  private StatisticArray array;
  private SamplableSensor<Double> sensor;
  private double lookbackTime;
  private boolean reportVaraince;

  /**
   * Create a new reporter which only reports the mean
   *
   * @param sensor   what sensor to report
   * @param telem    the telemetry to log to
   * @param meanType the packet type to use for the mean
   */
  public StatisticReporter(SamplableSensor<Double> sensor, Telemetry telem, SCMPacketType meanType) {
    this(sensor, telem, meanType, false, null);
  }

  /**
   * Create a new reporter which logs both mean and variance
   *
   * @param sensor       what sensor to report
   * @param telem        the telemetry to log to
   * @param meanType     the packet type to use for the mean
   * @param varianceType the packet type to use for the variance
   */
  public StatisticReporter(SamplableSensor<Double> sensor, Telemetry telem, SCMPacketType meanType,
                           SCMPacketType varianceType) {
    this(sensor, telem, meanType, true, varianceType);
  }

  private StatisticReporter(SamplableSensor<Double> sensor, Telemetry telem, SCMPacketType meanType,
                            boolean reportVariance, SCMPacketType varianceType) {
    this.telemetry      = telem;
    this.meanType       = meanType;
    this.array          = new StatisticArray(1);
    this.sensor         = sensor;
    this.lookbackTime   = -1;
    this.reportVaraince = reportVariance;
    this.varianceType   = varianceType;
  }

  /**
   * Generate and send a report. This should be called periodically.
   */
  public void report() {
    double mean = this.getMean();
    this.telemetry.reportTelemetry(this.meanType, mean);
    if (this.reportVaraince) {
      double variance = this.getVariance();
      this.telemetry.reportTelemetry(this.varianceType, variance);
    }
  }

  private double getMean() {
    if (this.array.getNumberOfSamples() == 0) {
      return Double.NaN;
    }
    if (this.lookbackTime < 0) {
      return this.array.getMean();
    }
    return this.array.getMean(this.lookbackTime);
  }

  private double getVariance() {
    if (this.lookbackTime < 0) {
      return this.array.getVariance();
    }
    return this.array.getVariance(this.lookbackTime);
  }

  /**
   * Sample the sensor, this should only be called if {@link #shouldSample()}
   * returns true.
   */
  public void sample() {
    double data = this.sensor.getNewData();
    double time = this.sensor.getLastSampleTime();
    this.array.addSample(data, time);
  }

  /**
   * Should we call sample, useful for looper constructions with
   * {@link org.rocketproplab.marginalstability.flightcomputer.looper.Looper#emitIf()}
   *
   * @return if data is ready to be sampled
   */
  public boolean shouldSample() {
    return this.sensor.hasNewData();
  }

  /**
   * Set the maximum number of samples to store.
   *
   * @param size the number of samples to store
   */
  public void setWindowSize(int size) {
    this.array = new StatisticArray(size);
  }

  /**
   * Set the amount of time to look back, by default all samples up to the maximum
   * count are used.
   *
   * @param time how far to look back in seconds
   */
  public void setLookbackTime(double time) {
    this.lookbackTime = time;
  }

  @Override
  public void onLooperCallback(Object tag, Looper from) {
    this.report();
  }

}
