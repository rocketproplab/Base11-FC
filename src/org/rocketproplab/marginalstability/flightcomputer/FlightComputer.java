package org.rocketproplab.marginalstability.flightcomputer;

import java.util.ArrayList;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

/**
 * The flight computer class is the main registration body for all of the
 * classes. It will hold the sensors and subsystems for access by other classes.
 *
 */
public class FlightComputer {
  private ArrayList<Subsystem> subsystems;
  private Telemetry            telemetry;

  public FlightComputer(Telemetry telemetry) {
    this.subsystems = new ArrayList<>();
    this.telemetry  = telemetry;
  }

  public void registerSubsystem(Subsystem subsystem) {
    this.subsystems.add(subsystem);
  }

  public void tick() {
    try {
      for (Subsystem subsystem : this.subsystems) {
        try {
          subsystem.update();
        } catch (Exception e) {
          this.telemetry.reportError(Errors.TOP_LEVEL_EXCEPTION);
        }
      }
    } catch (Exception e) {
      System.err.println("Unable to log errors!");
      e.printStackTrace();
    }

  }
}
