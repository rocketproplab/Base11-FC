package org.rocketproplab.marginalstability.flightcomputer.looper;

public class DurationRequiredEvent extends Event {
  private double durationTrue;
  private double sinceTrueTime;
  private Looper fromLooper;

  public DurationRequiredEvent(
          Object tag, double durationTrue, Looper fromLooper,
          Looper.CallbackCondition condition, Looper.Callback callback) {
    super(tag, condition, callback);
    this.durationTrue  = durationTrue;
    this.fromLooper    = fromLooper;
    this.sinceTrueTime = Double.NaN;
  }

  @Override
  public void tick(double time) {
    if (!condition.shouldEmit()) {
      sinceTrueTime = Double.NaN;
      return;
    }
    if (Double.isNaN(sinceTrueTime)) {
      sinceTrueTime = time;
    }
    if (time - sinceTrueTime >= durationTrue) {
      callback.onCallback(tag);
      fromLooper.removeEvent(tag);
    }
  }
}
