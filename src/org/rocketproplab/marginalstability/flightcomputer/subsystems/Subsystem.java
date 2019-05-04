package org.rocketproplab.marginalstability.flightcomputer.subsystems;

/**
 * The basic interface of a subsystem.
 * 
 * @author Max Apodaca
 *
 */
public interface Subsystem {

  /**
   * Called every ms to update the subsystem state. Heavy computation should be
   * avoided in this method.
   */
  public void update();
}
