package org.rocketproplab.marginalstability.flightcomputer.looper;

import org.rocketproplab.marginalstability.flightcomputer.Time;

/**
 * Event that contains all the information needed to determine
 * when and why a callback is invoked.
 *
 * @author Chi Chow
 */
public class GenericEvent implements EventCallback, EventCondition {
  private final EventCondition condition;
  private final EventCallback callback;
  private final Time time;

  public GenericEvent(EventCondition condition, EventCallback callback, Time time) {
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