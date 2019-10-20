package org.rocketproplab.marginalstability.flightcomputer.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

/**
 * Responsible for running commands. Determines when it should run a command
 * based on the states of the required subsystems.
 * 
 * @author Enlil Odisho
 *
 */
public class CommandScheduler {

  /**
   * Static variable containing an instance of CommandScheduler.
   */
  private static CommandScheduler instance = null;

  /**
   * Gets the singleton instance of CommandScheduler.
   * @return Instance of CommandScheduler.
   */
  public static CommandScheduler getInstance() {
    if (instance == null) {
      instance = new CommandScheduler();
    }
    return instance;
  }
  
  /**
   * List storing all commands that are running.
   */
  private ArrayList<Command> active;
  
  /**
   * List storing commands awaiting execution.
   */
  private ArrayList<Command> queue;  

  /**
   * Constructor for Command Scheduler.
   */
  public CommandScheduler() {
    active = new ArrayList<Command>();
    queue = new ArrayList<Command>();
  }
  
  /**
   * Add command waiting for execution to queue.
   * @param command
   */
  public void scheduleCommand(Command command) {
    if (!queue.contains(command) && !command.isDone()) {
      queue.add(command);
    }
  }
  
  /**
   * Called every interval. Processes the queue.
   */
  public void tick() {
    // TODO implement method
  }
  
  /* old code
  /**
   * Called every xx interval.
   *
  public void tick() {
    ArrayList<Subsystem> busySubsystems = new ArrayList<Subsystem>();
    
    // Iterate over started commands and check if completed, otherwise execute.
    Iterator<Command> startedcmdsIterator = started.iterator();
    while (startedcmdsIterator.hasNext()) {
      Command command = startedcmdsIterator.next();
      if (command.isDone()) {
        // Command is done, remove from started list.
        startedcmdsIterator.remove();
      } else {
        // Command is not done, add all dependencies to busySubsystems.
        busySubsystems.addAll(Arrays.asList(command.getDependencies()));
      }
    }
  }*/

}
