package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;

/**
 * The basic super class of a subsystem.
 *
 * @author Max Apodaca, Chi Chow
 */
public interface Subsystem {

  /**
   * Called when the subsystem is initialized.
   * Looper can be used to register events.
   */
  void prepare(Looper looper);
}
