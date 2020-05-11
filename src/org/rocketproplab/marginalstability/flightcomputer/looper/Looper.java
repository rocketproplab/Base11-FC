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

  public Looper(Time time) {
    this.time   = time;
    callbackMap = new HashMap<>();
  }

  public void loop() {
    while (true) {
      tick();
    }
  }

  public void tick() {
    Iterator<Map.Entry<Object, Event>> entryIterator = callbackMap.entrySet().iterator();
    while (entryIterator.hasNext()) {
      Map.Entry<Object, Event> entry = entryIterator.next();
      entry.getValue().tick(time.getSystemTime());
    }
  }

  public void emitAlways(Object tag, Callback callback) {
    emitScheduled(tag, 0, callback);
  }

  public void emitScheduled(Object tag, double interval, Callback callback) {
    emitScheduledIf(tag, interval, CallbackCondition.TRUE, callback);
  }

  public void emitIf(Object tag, CallbackCondition condition, Callback callback) {
    emitScheduledIf(tag, 0.0, condition, callback);
  }

  public void emitScheduledIf(Object tag, double interval,
                              CallbackCondition condition, Callback callback) {
    validateTag(tag);
    callbackMap.put(tag, new ScheduledConditionEvent(tag, interval, condition, callback));
  }

  public void emitOnceIf(Object tag, CallbackCondition condition, Callback callback) {
    emitOnceIf(tag, 0.0, condition, callback);
  }

  public void emitOnceIf(Object tag, double durationTrueRequired,
                         CallbackCondition condition, Callback callback) {
    validateTag(tag);
    callbackMap.put(tag, new DurationRequiredEvent(tag, durationTrueRequired,
            this, condition, callback));
  }

  private void validateTag(Object tag) {
    if (tag == null || callbackMap.containsKey(tag)) {
      throw new IllegalArgumentException("Tag of registered event cannot be null or duplicated");
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  public boolean removeEvent(Object tag) {
    return callbackMap.remove(tag) != null;
  }

  @FunctionalInterface
  public interface Callback {
    void onCallback(Object tag);
  }

  @FunctionalInterface
  public interface CallbackCondition {
    boolean shouldEmit();

    CallbackCondition TRUE = () -> true;
  }
}
