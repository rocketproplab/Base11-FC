package org.rocketproplab.marginalstability.flightcomputer.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
   * 
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
   * List storing all commands awaiting execution.
   */
  private ArrayList<Command> queue;

  /**
   * Constructor.
   */
  public CommandScheduler() {
    active = new ArrayList<Command>();
    queue  = new ArrayList<Command>();
  }

  /**
   * Add command to queue. It will be executed when it's subsystem dependencies
   * are available.
   * 
   * @param command Command to add.
   */
  public void scheduleCommand(Command command) {
    if (!queue.contains(command) && !active.contains(command)
        && !command.isDone()) {
      queue.add(command);
    }
  }

  /**
   * Called every interval. Processes the queue.
   */
  public void tick() {
    List<Subsystem> busySubsystems = new ArrayList<Subsystem>();

    // Process active commands.
    Iterator<Command> activeCmdsIterator = active.iterator();
    while (activeCmdsIterator.hasNext()) {
      // Get next active command in list.
      Command command = activeCmdsIterator.next();
      
      if (command.isDone()) {
        // Remove the last command from the active list.
        activeCmdsIterator.remove();
      } else {
        // Add all of the command's dependencies to busySubsystems.
        busySubsystems.addAll(Arrays.asList(command.getDependencies()));
        
        // Invoke command's execute method.
        command.execute();
      }
    }

    // Process queue.
    Iterator<Command> queueIterator = queue.iterator();
    while (queueIterator.hasNext()) {
      // Get next command in queue and it's dependencies.
      Command         command      = queueIterator.next();
      List<Subsystem> dependencies = Arrays.asList(command.getDependencies());

      // Check if command has no dependencies that are in-use.
      if (Collections.disjoint(dependencies, busySubsystems)) {
        queueIterator.remove(); // Remove command from queue.
        command.start();        // Start command execution.
        command.execute();      // Execute command.
        active.add(command);    // Add command to active list.
      }

      // Add all of command's dependencies to busySubsystems list.
      for (int d = 0; d < dependencies.size(); d++) {
        if (!busySubsystems.contains(dependencies.get(d))) {
          busySubsystems.add(dependencies.get(d));
        }
      }
    }
  }

}
