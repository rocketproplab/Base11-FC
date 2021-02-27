package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * Group a set of sensors to always poll at the same time. This could be useful
 * to poll a gyroscope and magnetometer on the same chip. <br>
 * <br>
 * Each time the group is polled each sensor gets polled in series.
 * 
 * @author Max Apodaca
 *
 */
public class PollingGroup implements PollingSensor {

  private PollingSensor[] sensors;

  /**
   * Create a new polling group for the given set of sensors.
   * 
   * @param pollingSensors the sensors to poll simultaneously
   */
  public PollingGroup(PollingSensor... pollingSensors) {
    this.sensors = pollingSensors;
  }

  @Override
  public void poll() {
    for (PollingSensor sensor : this.sensors) {
      sensor.poll();
    }
  }

}
