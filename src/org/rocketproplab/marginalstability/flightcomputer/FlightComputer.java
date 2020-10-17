package org.rocketproplab.marginalstability.flightcomputer;

import java.util.ArrayList;

import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

public class FlightComputer {
  private Telemetry telemetry;
  private Looper    looper;


  public FlightComputer(Telemetry telemetry) {
    this.telemetry = telemetry;
    this.looper    = Looper.getInstance();
  }

  public void registerSubsystem(Subsystem subsystem) {
    subsystem.prepare(this.looper);
  }

  public void tick() {
    try {
      this.looper.tick();
    } catch (Exception e) {
      this.telemetry.reportError(Errors.TOP_LEVEL_EXCEPTION);
    }
//    try {
//      for(Subsystem subsystem : this.subsystems) {
//        try {
//          subsystem.update();
//        } catch (Exception e) {
//          this.telemetry.reportError(Errors.TOP_LEVEL_EXCEPTION);
//        }
//      }
//    } catch (Exception e) {
//      System.err.println("Unable to log errors!");
//      e.printStackTrace();
//    }

  }
}
