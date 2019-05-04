package org.rocketproplab.marginalstability.flightcomputer.tracking;

/**
 * The different states which the rocket can be in while flying
 * 
 * @author Max Apodaca
 *
 */
public enum FlightMode {

  /**
   * We are on the launch-pad and have no ignition yet
   */
  Sitting,

  /**
   * The main engine is on and we are moving upwards
   */
  Burn,

  /**
   * The main engine has been turned off but we are still moving upwards
   */
  Coasting,

  /**
   * We are close to apogee (check settings for how close to apogee we have to
   * be)
   */
  Apogee,

  /**
   * We have passed apogee and are falling back to the ground with only a drogue
   * chute
   */
  Falling,

  /**
   * We are falling down with the parachutes open
   */
  Descending,

  /**
   * We are on the ground after flying
   */
  Landed
}
