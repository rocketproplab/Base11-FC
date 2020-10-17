package org.rocketproplab.marginalstability.flightcomputer;

import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

import javax.annotation.processing.SupportedSourceVersion;

public class FlightComputer {
  private Telemetry telemetry;
  private Time      time;
  private Looper    looper;


  public FlightComputer(Telemetry telemetry, Time time) {
    this.telemetry = telemetry;
    this.time      = time;
    this.looper    = new Looper(time);
  }

  public void registerSubsystem(Subsystem subsystem) {
    subsystem.prepare(this.looper);
  }

  public Time getTime() {
    return time;
  }

  public void tick() {
    this.looper.tick(((tag, from) -> {
      try {
        this.telemetry.reportError(Errors.TOP_LEVEL_EXCEPTION);
      } catch (Exception e) {
        System.err.println("Unable to log errors!");
        e.printStackTrace();
      }
    }));
  }
}
