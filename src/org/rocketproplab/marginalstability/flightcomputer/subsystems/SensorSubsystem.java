package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.hal.PollingSensor;
import org.rocketproplab.marginalstability.flightcomputer.hal.SensorPoller;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A subsystem to handle ticking the sensors at a fixed rate.
 *
 * @author Max Apodaca
 */
public class SensorSubsystem implements Subsystem {
  private List<SensorPoller> sensorPollers;
  private Time time;

  /**
   * Create the subsystem using the given time.
   *
   * @param time the time to use
   */
  public SensorSubsystem(Time time) {
    this.sensorPollers = Collections.synchronizedList(new ArrayList<>());
    this.time          = time;
  }

  @Override
  public void prepare(Looper looper) {
    looper.emitAlways(this, (tag, from) -> updateSenorPollers());
  }

  private void updateSenorPollers() {
    for (SensorPoller poller : this.sensorPollers) {
      poller.update(this.time.getSystemTime());
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
    SensorPoller poller = new SensorPoller(sensor, rate);
    this.sensorPollers.add(poller);
  }
}
