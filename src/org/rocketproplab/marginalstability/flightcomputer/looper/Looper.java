package org.rocketproplab.marginalstability.flightcomputer.looper;

import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.commands.Command;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

import java.util.*;

/**
 * The Looper class handles 2 types of activities:
 * <p>
 * [Events]
 * Called immediately when conditions are met, and does not depend on the state
 * of other subsystems. Can be scheduled based on time and states of other subsystems.
 * <p>
 * [Commands]
 * Called as soon as possible after being queued, but may not be called immediately
 * if dependent subsystems are unavailable. Each subsystem can have only one command running
 * at the same time.
 *
 * @author Chi Chow, Enlil Odisho
 */
public class Looper {
  private static Looper mainLooper;

  public static Looper getInstance() {
    if (mainLooper == null) {
      mainLooper = new Looper(new Time());
    }
    return mainLooper;
  }

  private final Time                   time;
  private final HashMap<Object, Event> callbackMap;
  /**
   * List storing all commands that are running.
   */
  private final ArrayList<Command>     active;

  /**
   * List storing all commands awaiting execution.
   */
  private final ArrayList<Command> queue;

  /**
   * Hash map containing all subsystems that are being used along with the
   * command that's using them.
   */
  private final HashMap<Subsystem, Command> busySubsystems;

  /**
   * Construct a new Looper with time object.
   *
   * @param time the time all events in this Looper will use.
   */
  public Looper(Time time) {
    this.time = time;
    callbackMap = new HashMap<>();
    active = new ArrayList<>();
    queue = new ArrayList<>();
    busySubsystems = new HashMap<>();
  }

  /**
   * Events and commands are checked whether they should be executed every tick.
   *
   * @param errorListener to report errors
   */
  public void tick(LooperErrorListener errorListener) {
    handleEvents(errorListener);
    handleCommands();
  }

  /**
   * Iterate through events in this Looper and checks if callbacks should be emitted.
   *
   * @param errorListener to report errors
   */
  @SuppressWarnings("WhileLoopReplaceableByForEach")
  private void handleEvents(LooperErrorListener errorListener) {
    Iterator<Map.Entry<Object, Event>> entryIterator = callbackMap.entrySet().iterator();
    while (entryIterator.hasNext()) {
      Map.Entry<Object, Event> entry = entryIterator.next();
      Object tag = entry.getKey();
      Event event = entry.getValue();
      try {
        if (event.shouldEmit()) {
          event.onLooperCallback(tag, this);
        }
      } catch (Exception e) {
        if (errorListener != null) {
          errorListener.onError(tag, this);
        }
      }
    }
  }

  /**
   * Commands are queued to run once it's Subsystem dependencies are not busy.
   * Each subsystem can only have one command running at the same time.
   */
  private void handleCommands() {
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
      Command command = queueIterator.next();
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
   * Simple tick() method for ticks that do not require error reporting
   */
  public void tick() {
    tick(null);
  }

  /**
   * Create an event that will always emit callbacks.
   * Event will not be removed unless removeEvent is called.
   *
   * @param tag      to identify the event
   * @param callback to be invoked by Looper
   */
  public void emitAlways(Object tag, Callback callback) {
    registerEvent(tag, new Event(CallbackCondition.TRUE, callback, time));
  }

  /**
   * Create an event that will emit once every specified interval.
   * Event will not be removed unless removeEvent is called.
   *
   * @param tag      to identify the event
   * @param interval at which callbacks should be emitted
   * @param callback to be invoked by Looper
   */
  public void emitScheduled(Object tag, double interval, Callback callback) {
    emitScheduledIf(tag, interval, CallbackCondition.TRUE, callback);
  }

  /**
   * Create an event that will emit every time the specified
   * condition returns true.
   * Event will not be removed unless removeEvent is called.
   *
   * @param tag       to identify the event
   * @param condition required for callbacks to be emitted
   * @param callback  to be invoked by Looper
   */
  public void emitIf(Object tag, CallbackCondition condition, Callback callback) {
    emitScheduledIf(tag, 0.0, condition, callback);
  }

  /**
   * Create an event that will emit once every specified interval,
   * if the specified condition returns true.
   * Event will not be removed unless removeEvent is called.
   *
   * @param tag       to identify the event
   * @param interval  at which callbacks should be emitted
   * @param condition required for callbacks to be emitted
   * @param callback  to be invoked by Looper
   */
  public void emitScheduledIf(Object tag, double interval,
                              CallbackCondition condition, Callback callback) {
    registerEvent(tag, new ScheduledConditionEvent(interval, condition, callback, time));
  }

  /**
   * Create an event that will emit once when the specified
   * condition returns true.
   * Event will be removed automatically once the callback has been invoked.
   *
   * @param tag       to identify the event
   * @param condition required for callback to be emitted
   * @param callback  to be invoked by Looper
   */
  public void emitOnceIf(Object tag, CallbackCondition condition, Callback callback) {
    emitOnceIf(tag, 0.0, condition, callback);
  }

  /**
   * Create an event that will emit once when the specified
   * condition returns true for the specified interval.
   * Event will be removed automatically once the callback has been invoked.
   *
   * @param tag                  to identify the event
   * @param durationTrueRequired time needed for condition to return true
   * @param condition            required for callback to be emitted
   * @param callback             to be invoked by Looper
   */
  public void emitOnceIf(Object tag, double durationTrueRequired,
                         CallbackCondition condition, Callback callback) {
    registerEvent(tag, new DurationRequiredEvent(
            durationTrueRequired, condition, callback, time));
  }

  /**
   * Register an event to this Looper.
   *
   * @param tag      to identify the event
   * @param newEvent to be registered
   */
  public void registerEvent(Object tag, Event newEvent) {
    if (tag == null) {
      throw new IllegalArgumentException("Tag of registered event cannot be null");
    } else if (callbackMap.containsKey(tag)) {
      throw new IllegalArgumentException("Tag of registered event cannot be duplicated");
    }
    callbackMap.put(tag, newEvent);
  }

  /**
   * Retrieve a registered event from this looper.
   *
   * @param tag to identify the event
   * @return event registered with the given tag
   */
  public Event getEvent(Object tag) {
    return callbackMap.get(tag);
  }

  /**
   * Remove an event from this Looper.
   *
   * @param tag to identify the event
   * @return the event removed
   */
  public Event removeEvent(Object tag) {
    return callbackMap.remove(tag);
  }

  @FunctionalInterface
  public interface LooperErrorListener {
    void onError(Object tag, Looper from);
  }

  /**
   * Callback when condition to invoke an event is true.
   */
  @FunctionalInterface
  public interface Callback {
    void onLooperCallback(Object tag, Looper from);
  }

  /**
   * Condition to determine whether a callback should be invoked.
   */
  @FunctionalInterface
  public interface CallbackCondition {
    boolean shouldEmit();

    CallbackCondition TRUE = () -> true;
  }

  /**
   * Event that contains all the information needed to determine
   * when and why a callback is invoked.
   */
  public static class Event implements CallbackCondition, Callback {
    private final CallbackCondition condition;
    private final Callback callback;
    private final Time     time;

    public Event(CallbackCondition condition, Callback callback, Time time) {
      this.condition = condition;
      this.callback = callback;
      this.time = time;
    }

    @Override
    public boolean shouldEmit() {
      return condition.shouldEmit();
    }

    @Override
    public void onLooperCallback(Object tag, Looper from) {
      callback.onLooperCallback(tag, from);
    }

    protected double getCurrentTime() {
      return time != null ? time.getSystemTime() : 0.0;
    }
  }
}
