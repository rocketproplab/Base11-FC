package org.rocketproplab.marginalstability.flightcomputer.looper;

import org.rocketproplab.marginalstability.flightcomputer.Time;

/**
 * An event that is triggered if some condition is true for a
 * certain amount of time.
 *
 * @author Chi Chow
 */
public class DurationRequiredEvent extends Looper.Event {
  private final double durationTrue;
  private       double sinceTrueTime;

  public DurationRequiredEvent(
          double durationTrue, Looper.CallbackCondition condition, Looper.Callback callback, Time time) {
    super(condition, callback, time);
    this.durationTrue  = durationTrue;
    this.sinceTrueTime = Double.NaN;
  }

  @Override
  public boolean shouldEmit() {
    if (!super.shouldEmit()) {
      sinceTrueTime = Double.NaN;
      return false;
    }
    double currentTime = getCurrentTime();
    if (Double.isNaN(sinceTrueTime)) {
      sinceTrueTime = currentTime;
    }
    return currentTime - sinceTrueTime >= durationTrue;
  }

  @Override
  public void onLooperCallback(Object tag, Looper from) {
    from.removeEvent(tag);
    super.onLooperCallback(tag, from);
  }
}
