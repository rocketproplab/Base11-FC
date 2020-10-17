package org.rocketproplab.marginalstability.flightcomputer.looper;

import org.rocketproplab.marginalstability.flightcomputer.Time;

public class ScheduledConditionEvent extends Looper.Event {
  private double interval;
  private double lastInvoked;

  public ScheduledConditionEvent(
          double interval, Looper.CallbackCondition condition, Looper.Callback callback, Time time) {
    super(condition, callback, time);
    this.interval    = interval;
    this.lastInvoked = Double.NaN;
  }

  @Override
  public boolean shouldEmit() {
    double currentTime = getCurrentTime();
    if (Double.isNaN(lastInvoked)) {
      lastInvoked = currentTime;
    }
    if (super.shouldEmit() && currentTime - lastInvoked >= interval) {
      lastInvoked = currentTime;
      return true;
    }
    return false;
  }
}