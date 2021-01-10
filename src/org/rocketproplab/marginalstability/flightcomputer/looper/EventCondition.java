package org.rocketproplab.marginalstability.flightcomputer.looper;

/**
 * Condition to determine whether a callback should be invoked.
 *
 * @author Chi Chow
 */
@FunctionalInterface
public interface EventCondition {
  boolean shouldEmit();

  EventCondition TRUE = () -> true;
}
