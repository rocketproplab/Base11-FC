package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * A class to poll a {@link PollingSensor} at a fixed rate.
 * 
 * @author Max Apodaca
 *
 */
public class SensorPoller {

  private double        pollingRate;
  private double        nextPollTime;
  private boolean       hasPolled;
  private PollingSensor sensor;

  /**
   * Initialize a sensor poller that will poll the given sensor every rate
   * seconds.
   * 
   * @param sensor the sensor to poll
   * @param rate   the rate at which to poll in seconds
   */
  public SensorPoller(PollingSensor sensor, double rate) {
    this.pollingRate  = rate;
    this.nextPollTime = -Double.MAX_VALUE;
    this.hasPolled    = false;
    this.sensor       = sensor;
  }

  /**
   * Update the poller, if enough time has passed the sensor will be polled.
   * 
   * @param time the current time in seconds
   */
  public void update(double time) {
    if (this.shouldPollSensor(time)) {
      this.nextPollTime += this.pollingRate;
      this.sensor.poll();
    }

    if (!this.hasPolled) {
      this.nextPollTime = time + this.pollingRate;
      this.hasPolled    = true;
    }
  }

  /**
   * Determine if we should poll the sensor at this time
   * 
   * @param time the current time
   * @return if the sensor needs to be polled
   */
  protected boolean shouldPollSensor(double time) {
    return time > this.nextPollTime || this.pollingRate == 0;
  }

}
