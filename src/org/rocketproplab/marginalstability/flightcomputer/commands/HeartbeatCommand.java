package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

/**
 * This is the Heartbeat Command class that implements the Command interface.
 * The main capabilities of this class includes checking the start time and
 * sending a periodic "heartbeat" signal every second.
 * 
 * @author Hemanth Battu
 *
 */
public class HeartbeatCommand implements Command {
  private static final Subsystem[] EMPTY_ARRAY = {};
  private int                      HBcounter;
  private double                   startTime;
  private double                   currentTime;
  private Time                     time;
  private Telemetry                telemetry;

  /**
   * Creates a new HeartbeatCommand object using Time and Telemetry objects.
   * 
   * @param time      the Time object to use for checking time
   * @param telemetry the Telemetry object used to send heartbeat
   */
  public HeartbeatCommand(Time time, Telemetry telemetry) {
    this.time      = time;
    this.telemetry = telemetry;
  }

  /**
   * Getter method to return start time.
   * 
   * @return startTime
   */
  public double getStartTime() {
    return startTime;
  }

  /**
   * Setter method to set the start time.
   * 
   * @param startTime input to set start time with
   */
  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  /**
   * Getter method to return current time.
   * 
   * @return currentTime
   */
  public double getcurrentTime() {
    return currentTime;
  }

  /**
   * Setter method to set the current time.
   * 
   * @param currentTime input to set the current time with
   */
  public void setCurrentTime(double currentTime) {
    this.currentTime = currentTime;
  }

  /**
   * Returns whether the command has finished execution.
   */
  @Override
  public boolean isDone() {
    return false;
  }

  /**
   * Called by the scheduler every xx ms while command is not done. Checks to
   * see if one second has passed to send heartbeat.
   */
  @Override
  public void execute() {
    this.setCurrentTime(time.getSystemTime());
    if (HBcounter == 0) {
      if (currentTime - startTime >= Settings.HEARTBEAT_THRESHOLD) {
        telemetry.sendHeartbeat();
        HBcounter += 1;
      }
    } else {
      if (currentTime - startTime >= (HBcounter * (currentTime - startTime)
          / (HBcounter + 1) + 1)) {
        telemetry.sendHeartbeat();
        HBcounter += 1;
      }
    }
  }

  /**
   * Sets the start time.
   */
  @Override
  public void start() {
    this.setStartTime(time.getSystemTime());
  }

  /**
   * Stops command.
   */
  @Override
  public void end() {
    return;
  }

  /**
   * Returns list of dependencies.
   */
  @Override
  public Subsystem[] getDependencies() {
    return EMPTY_ARRAY;
  }

}
