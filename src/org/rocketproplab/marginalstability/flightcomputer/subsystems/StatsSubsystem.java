package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.math.StatisticReporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Subsystem that contains all of the sensors that are only used to provide
 * statistics, also listens for events to provide statistics.
 * <p>
 * Contains all the Thermocouple Sensors, the Barometer, and the Pressure
 * Transducer Subsystem. Listens for parachute deployment, state change, and
 * navigation updates.
 *
 * @author Max Apodaca
 */
public class StatsSubsystem implements Subsystem {

  private List<StatisticReporter> reporters;
  private Telemetry               telemetry;
  private Looper                  looper;

  public StatsSubsystem(Telemetry telemetry) {
    this.reporters = new ArrayList<>();
    this.telemetry = telemetry;
  }

  @Override
  public void prepare(Looper looper) {
    this.looper = looper;
  }

  public void addRepoter(StatisticReporter reporter) {
    this.looper.emitIf(null, reporter::shouldSample, reporter);
  }

}
