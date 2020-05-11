package org.rocketproplab.marginalstability.flightcomputer.looper;

public class ScheduledConditionEvent extends Event {
  private double interval;
  private double lastInvoked;

  public ScheduledConditionEvent(
          Object tag, double interval, Looper.CallbackCondition condition, Looper.Callback callback) {
    super(tag, condition, callback);
    this.interval    = interval;
    this.lastInvoked = Double.NaN;
  }

  @Override
  public void tick(double time) {
    if (Double.isNaN(lastInvoked)) {
      lastInvoked = time;
    }

    if (condition.shouldEmit() && time - lastInvoked >= interval) {
      callback.onCallback(tag);
      lastInvoked = time;
    }
  }
}