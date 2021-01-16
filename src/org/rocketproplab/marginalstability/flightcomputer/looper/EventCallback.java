package org.rocketproplab.marginalstability.flightcomputer.looper;

/**
 * Callback when condition to invoke an event is true.
 *
 * @author Chi Chow
 */
@FunctionalInterface
public interface EventCallback {
  void onLooperCallback(Object tag, Looper from);
}