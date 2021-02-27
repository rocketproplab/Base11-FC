package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import java.util.ArrayList;
import java.util.List;

import org.rocketproplab.marginalstability.flightcomputer.events.EngineEventListener;
import org.rocketproplab.marginalstability.flightcomputer.events.FlightStateListener;
import org.rocketproplab.marginalstability.flightcomputer.events.ParachuteListener;
import org.rocketproplab.marginalstability.flightcomputer.events.PositionListener;
import org.rocketproplab.marginalstability.flightcomputer.events.VelocityListener;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;
import org.rocketproplab.marginalstability.flightcomputer.math.StatisticCollector;
import org.rocketproplab.marginalstability.flightcomputer.math.StatisticReporter;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

/**
 * Subsystem that contains all of the sensors that are only used to provide
 * statistics, also listens for events to provide statistics.
 * 
 * Contains all the Thermocouple Sensors, the Barometer, and the Pressure
 * Transducer Subsystem. Listens for parachute deployment, state change, and
 * navigation updates.
 * 
 * @author Max Apodaca
 *
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
