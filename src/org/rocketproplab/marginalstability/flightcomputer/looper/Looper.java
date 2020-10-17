package org.rocketproplab.marginalstability.flightcomputer.looper;

import org.rocketproplab.marginalstability.flightcomputer.Time;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Looper that allows events to be registered and triggered
 * when conditions are met
 *
 * @author Chi Chow
 */
public class Looper {
  private static Looper mainLooper;

  public static Looper getInstance() {
    if (mainLooper == null) {
      mainLooper = new Looper(null);
    }
    return mainLooper;
  }

  private Time                   time;
  private HashMap<Object, Event> callbackMap;

  /**
   * Construct a new Looper with time object.
   *
   * @param time the time all events in this Looper will use.
   */
  public Looper(Time time) {
    this.time   = time;
    callbackMap = new HashMap<>();
  }

  /**
   * Infinite loop to update all events in this Looper.
   */
  public void loop() {
    // TODO: correct implementation of looping
    while (true) {
      tick();
    }
  }

  /**
   * Iterate through events in this Looper and checks if
   * callbacks should be emitted.
   */
  @SuppressWarnings("WhileLoopReplaceableByForEach")
  public void tick() {
    Iterator<Map.Entry<Object, Event>> entryIterator = callbackMap.entrySet().iterator();
    while (entryIterator.hasNext()) {
      Map.Entry<Object, Event> entry = entryIterator.next();
      Object                   tag   = entry.getKey();
      Event                    event = entry.getValue();
      if (event.shouldEmit()) {
        event.onLooperCallback(tag, this);
      }
    }
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
    emitScheduled(tag, 0.0, callback);
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
   * Remove an event from this Looper.
   *
   * @param tag to identify the event
   * @return the event removed
   */
  public Event removeEvent(Object tag) {
    return callbackMap.remove(tag);
  }

  /**
   *
   */
  @FunctionalInterface
  public interface Callback {
    void onLooperCallback(Object tag, Looper from);
  }

  @FunctionalInterface
  public interface CallbackCondition {
    boolean shouldEmit();

    CallbackCondition TRUE = () -> true;
  }

  public static class Event implements CallbackCondition, Callback {
    private CallbackCondition condition;
    private Callback          callback;
    private Time              time;

    public Event(CallbackCondition condition, Callback callback, Time time) {
      this.condition = condition;
      this.callback  = callback;
      this.time      = time;
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
      return time.getSystemTime();
    }
  }
}
