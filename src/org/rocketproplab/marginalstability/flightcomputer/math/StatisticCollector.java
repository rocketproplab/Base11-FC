package org.rocketproplab.marginalstability.flightcomputer.math;

import java.util.LinkedList;
import java.util.Queue;

import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketHelpers;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

/**
 * Averages value over a given display rate and outputs SCM packets. While all
 * units are in seconds they are self consistent. This means as long as all
 * units are multiplied by the correct rate it will till work.
 * 
 * @author Max Apodaca
 *
 */
public class StatisticCollector {
  private Queue<SCMPacket> outgoingPackets;
  private double           nextSampleTime;

  private double statisticAccumulator;
  private int    accumulatorCount;

  private boolean       firstSample;
  private SCMPacketType outputType;
  private double        timeIncrement;

  /**
   * Create a new statistic collector that will output packets of type type and
   * sample over the given display rate.
   * 
   * @param type        the type of {@link SCMPacket} to output.
   * @param displayRate the rate in seconds of how often to output
   */
  public StatisticCollector(SCMPacketType type, double displayRate) {
    this.outgoingPackets = new LinkedList<>();
    this.firstSample     = true;
    this.outputType      = type;
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
      String data = SCMPacketHelpers.getSCMDoulbeRepresentation(this.statisticAccumulator / this.accumulatorCount);
      this.outgoingPackets.add(new SCMPacket(this.outputType, data));
      this.reset();
      this.nextSampleTime += this.timeIncrement;
      if (this.nextSampleTime < time) {
        this.nextSampleTime = time + this.timeIncrement;
        // TODO Report sample rate error
      }
    }

    this.statisticAccumulator += value;
    this.accumulatorCount++;
    this.firstSample = false;
  }

  /**
   * Reset the internal counters to zero
   */
  private void reset() {
    this.statisticAccumulator = 0;
    this.accumulatorCount     = 0;
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
   * Get the next SCM packet to send for this sampler.
   * 
   * TODO throw error if queue gets too long
   * 
   * @return the next SCM Packet to send
   */
  public SCMPacket getNext() {
    return this.outgoingPackets.poll();
  }

}
