package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

/**
 * This is the super class for all commands. All commands will implement this
 * class.
 * 
 * Basic capabilities of this class include executing, starting, and stopping
 * commands. Additionally, it can check whether the command is finished or not.
 * 
 * @author Hemanth Battu, Enlil Odisho
 *
 */
public interface Command {
  /**
   * Returns whether the command has finished execution.
   */
  public void isDone();

  /**
   * Called by the scheduler every xx ms while the command is not done
   */
  public void execute();

  /**
   * Called when the scheduler requests the command to start
   */
  public void start();

  /**
   * Called when the scheduler requests the command to stop
   */
  public void end();

  /**
   * Register a subsystem as a dependency for the command.
   * 
   * @param dependency The subsystem dependency
   */
  public void registerDependency(Subsystem dependency);

  /**
   * Retrieves a list of dependencies
   * 
   * @return array of subsystems
   */
  public Subsystem[] getDependencies();

}
