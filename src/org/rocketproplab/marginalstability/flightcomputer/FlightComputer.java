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
  private Time                 time;

  /**
   * Create a new flight computer that will log to the given telemetry and use the
   * given time
   * 
   * @param telemetry the telemetry to log to
   * @param time      the time to use for the flight
   */
  public FlightComputer(Telemetry telemetry, Time time) {
    this.subsystems = new ArrayList<>();
    this.telemetry  = telemetry;
    this.time       = time;
  }

  /**
   * Add a new subsystem so that it will be ticked
   * 
   * @param subsystem the subsystem to tick
   */
  public void registerSubsystem(Subsystem subsystem) {
    this.subsystems.add(subsystem);
  }

  /**
   * Get the time of the flight computer
   * 
   * @return the time to use for all calculations
   */
  public Time getTime() {
    return this.time;
  }

  /**
   * Do one cycle of the subsystems
   */
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
