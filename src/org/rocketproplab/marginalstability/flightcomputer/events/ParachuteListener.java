package org.rocketproplab.marginalstability.flightcomputer.events;

/**
 * A listener for changes in the parachute state.
 *
 * @author Max Apodaca
 */
public interface ParachuteListener {

  /**
   * Called when the drogue chute opens
   */
  public void onDrogueOpen();

  /**
   * Called when the drogue chute is cut
   */
  public void onDrougeCut();

  /**
   * Called when the main chute opens.
   */
  public void onMainChuteOpen();

}
