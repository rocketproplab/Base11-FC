package org.rocketproplab.marginalstability.flightcomputer.events;

import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;

/**
 * An interface for listening to new position estimations
 *
 * @author Max Apodaca
 */
public interface PositionListener {

  /**
   * Called when new estimate is available
   *
   * @param positionEstimate the new position estimate
   */
  public void onPositionEstimate(InterpolatingVector3 positionEstimate);

}
