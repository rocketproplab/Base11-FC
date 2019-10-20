package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

public class DummyCommand implements Command {

  /**
   * The subsystem dependencies for the command.
   */
  public Subsystem[] dependencies = new Subsystem[] {};

  /**
   * Whether the command is done.
   */
  public boolean done = false;

  /**
   * Whether the command has been started by the scheduler;
   */
  public boolean started = false;

  /**
   * Number of times execute must be called until command is done.
   */
  public int  doneAfter = 1;
  /**
   * Number of times execute has been called.
   */
  private int counter   = 0;

  @Override
  public boolean isDone() {
    return done;
  }

  @Override
  public void execute() {
    counter++;
    if (counter >= doneAfter) {
      done = true;
    }
  }

  @Override
  public void start() {
    started = true;
  }

  @Override
  public void end() {
    started = false;
  }

  @Override
  public Subsystem[] getDependencies() {
    return dependencies;
  }

}
