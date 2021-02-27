package org.rocketproplab.marginalstability.flightcomputer.looper;

import org.rocketproplab.marginalstability.flightcomputer.Time;

/**
 * An event that is scheduled to be triggered by a specific condition.
 *
 * @author Chi Chow
 */
public class ScheduledConditionEvent extends GenericEvent {
  private double interval;
  private double lastInvoked;

  public ScheduledConditionEvent(
          double interval, EventCondition condition, EventCallback callback, Time time) {
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