package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.hal.PollingSensor;
import org.rocketproplab.marginalstability.flightcomputer.hal.SensorTicker;

/**
 * A subsystem to handle ticking the sensors at a fixed rate.
 * 
 * @author Max Apodaca
 *
 */
public class SensorSubsystem implements Subsystem {
  private List<SensorTicker> sensorTickers;
  private Time               time;

  /**
   * Create the subsystem using the given time.
   * 
   * @param time the time to use
   */
  public SensorSubsystem(Time time) {
    this.sensorTickers = Collections.synchronizedList(new ArrayList<>());
    this.time          = time;
  }

  @Override
  public void update() {
    for (SensorTicker ticker : this.sensorTickers) {
      ticker.tick(this.time.getSystemTime());
    }
  }

  /**
   * Add the sensor to be polled at the given rate. If the same sensor is added
   * twice it will be polled twice.
   * 
   * @param sensor the sensor to poll
   * @param rate   the rate in seconds to be polled at
   */
  public void addSensor(PollingSensor sensor, double rate) {
    SensorTicker ticker = new SensorTicker(sensor, rate);
    this.sensorTickers.add(ticker);
  }

}
