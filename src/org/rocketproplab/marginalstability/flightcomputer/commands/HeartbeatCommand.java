package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

public class HeartbeatCommand implements Command {
  private static final Subsystem[] EMPTY_ARRAY = {};
  private int                      HBcounter;
  private double                   startTime;
  private double                   currentTime;
  private Time                     time;
  private Telemetry                telemetry;

  public HeartbeatCommand(Time time, Telemetry telemetry) {
    this.time      = time;
    this.telemetry = telemetry;
  }

  public double getStartTime() {
    return startTime;
  }

  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  public double getcurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(double currentTime) {
    this.currentTime = currentTime;
  }

  @Override
  public boolean isDone() {
    return false;
  }

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

  @Override
  public void start() {
    this.setStartTime(time.getSystemTime());
  }

  @Override
  public void end() {
    return;
  }

  @Override
  public Subsystem[] getDependencies() {
    return EMPTY_ARRAY;
  }

}
