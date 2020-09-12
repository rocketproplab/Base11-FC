package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * A class to poll a {@link PollingSensor} at a fixed rate.
 * 
 * @author Max Apodaca
 *
 */
public class SensorTicker {

  private double        tickingRate;
  private double        nextTickTime;
  private boolean       hasTicked;
  private PollingSensor sensor;

  /**
   * Initialize a sensor ticker that will tick the given sensor every rate
   * seconds.
   * 
   * @param sensor the sensor to tick
   * @param rate   the rate at which to tick in seconds
   */
  public SensorTicker(PollingSensor sensor, double rate) {
    this.tickingRate  = rate;
    this.nextTickTime = -Double.MAX_VALUE;
    this.hasTicked    = false;
    this.sensor       = sensor;
  }

  /**
   * Tick the ticker, if enough time has passed the sensor will be polled.
   * 
   * @param time the current time in seconds
   */
  public void tick(double time) {
    if (this.shouldTickSensor(time)) {
      this.nextTickTime += this.tickingRate;
      this.sensor.poll();
    }

    if (!this.hasTicked) {
      this.nextTickTime = time + this.tickingRate;
      this.hasTicked    = true;
    }
  }

  /**
   * Determine if the sensor should tick at this time
   * 
   * @param time the current time
   * @return if the sensor should tick
   */
  protected boolean shouldTickSensor(double time) {
    return time > this.nextTickTime || this.tickingRate == 0;
  }

}
