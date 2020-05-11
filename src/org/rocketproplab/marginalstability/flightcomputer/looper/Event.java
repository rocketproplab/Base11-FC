package org.rocketproplab.marginalstability.flightcomputer.looper;

/**
 * Abstract class for events registered to Looper
 */
public abstract class Event {
  protected Object                   tag;
  protected Looper.CallbackCondition condition;
  protected Looper.Callback          callback;

  public Event(Object tag, Looper.CallbackCondition condition, Looper.Callback callback) {
    this.tag       = tag;
    this.condition = condition;
    this.callback  = callback;
  }

  public abstract void tick(double time);
}