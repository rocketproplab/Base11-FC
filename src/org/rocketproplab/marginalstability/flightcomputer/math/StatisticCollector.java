package org.rocketproplab.marginalstability.flightcomputer.math;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Averages value over a given display rate and outputs averaged results. While
 * all units are in seconds they are self consistent. This means as long as all
 * units are multiplied by the correct rate it will till work.
 * 
 * @author Max Apodaca
 *
 */
public class StatisticCollector {
  private Queue<StatisticArray> outgoingPackets;
  private double        nextSampleTime;

  private StatisticArray currentArray;

  private boolean firstSample;
  private double  timeIncrement;

  /**
   * Create a new statistic collector that will output samples averaged over the
   * given display rate.
   * 
   * @param displayRate the rate in seconds of how often to output
   */
  public StatisticCollector(double displayRate) {
    this.outgoingPackets = new LinkedList<>();
    this.firstSample     = true;
    this.timeIncrement   = displayRate;
  }

  /**
   * Samples one run of the statistic, after this updates hasNext and getNext
   * 
   * @param time  current time in seconds
   * @param value the sampled value
   */
  public void sampleStatistic(double time, double value) {
    if (this.firstSample) {
      this.reset();
      this.nextSampleTime = time + this.timeIncrement;
    }

    if (this.nextSampleTime < time) {
      this.outgoingPackets.add(this.currentArray);
      this.reset();
      this.nextSampleTime += this.timeIncrement;
      if (this.nextSampleTime < time) {
        this.nextSampleTime = time + this.timeIncrement;
        // TODO Report sample rate error
      }
    }

    this.currentArray.addSample(value);
    this.firstSample = false;
  }

  /**
   * Reset the internal counters to zero
   */
  private void reset() {
    this.currentArray = new StatisticArray();
  }

  /**
   * Returns whether or not there is another SCM packet to send
   * 
   * @return whether there is an SCM packet to send via {@link #getNext()}
   */
  public boolean hasNext() {
    return !this.outgoingPackets.isEmpty();
  }

  /**
   * Get the next value of the sample
   * 
   * TODO throw error if queue gets too long
   * 
   * @return the next averaged value
   */
  public StatisticArray getNext() {
    return this.outgoingPackets.poll();
  }

}
