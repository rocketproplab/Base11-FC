package org.rocketproplab.marginalstability.flightcomputer.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

/**
 * Responsible for running commands. Determines when it should run a command
 * based on the state of required subsystems.
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
   * Hash map containing all subsystems that are being used along with the
   * command that's using them.
   */
  private HashMap<Subsystem, Command> busySubsystems;

  /**
   * Constructor.
   */
  public CommandScheduler() {
    active         = new ArrayList<Command>();
    queue          = new ArrayList<Command>();
    busySubsystems = new HashMap<Subsystem, Command>();
  }

  /**
   * Add command to command scheduler queue. It will be executed when it's
   * subsystem dependencies are available.
   * 
   * @param command Command to add.
   */
  public void scheduleCommand(Command command) {
    // Make sure command is not done and not already in scheduler.
    if (!command.isDone() && !queue.contains(command)
        && !active.contains(command)) {
      queue.add(command);
    }
  }

  /**
   * Retrieves the command that is using a particular subsystem. Returns null if
   * subsystem is not being used by any command.
   * 
   * @param subsystem Subsystem that is being used by command.
   */
  public Command getCommandUsingSubsystem(Subsystem subsystem) {
    return busySubsystems.get(subsystem);
  }

  /**
   * Called every interval.
   */
  public void tick() {
    // Process active commands.
    updateActiveCommands();

    // Process queue.
    // Stores the list of subsystems not available to be used.
    HashSet<Subsystem> unavailableSubsystems = new HashSet<Subsystem>(
        busySubsystems.keySet());

    // Loop through all commands in queue.
    Iterator<Command> queueIterator = queue.iterator();
    while (queueIterator.hasNext()) {
      // Get next command in queue and it's dependencies.
      Command         command      = queueIterator.next();
      List<Subsystem> dependencies = Arrays.asList(command.getDependencies());

      // Check if command has no dependencies that are in-use.
      if (Collections.disjoint(dependencies, unavailableSubsystems)) {
        // Start running command.
        queueIterator.remove(); // Remove command from queue.
        active.add(command); // Add command to active list.
        command.start(); // Start command execution.
        command.execute(); // Execute command.
        // Mark command's dependencies as busy.
        for (Subsystem s : dependencies) {
          busySubsystems.put(s, command);
        }
      }

      // Add all of command's dependencies to unavailableSubsystems list.
      // This prevents us from running a command that uses a dependency that
      // a command earlier in the queue needs.
      unavailableSubsystems.addAll(dependencies);
    }
  }

  /**
   * Updates the active and busy subsystems lists. Should be called every tick().
   */
  private void updateActiveCommands() {
    // Loop through all active commands.
    Iterator<Command> activeCmdsIterator = active.iterator();
    while (activeCmdsIterator.hasNext()) {
      Command command = activeCmdsIterator.next();

      if (command.isDone()) {
        // Remove the command from active list.
        activeCmdsIterator.remove();
        // Make command's dependencies available for other commands to use.
        Subsystem[] subsystemsUsed = command.getDependencies();
        for (Subsystem s : subsystemsUsed) {
          busySubsystems.remove(s, command);
        }
      } else {
        // Invoke command's execute method.
        command.execute();
      }
    }
  }

}
