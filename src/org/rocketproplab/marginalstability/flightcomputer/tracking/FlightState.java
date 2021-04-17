package org.rocketproplab.marginalstability.flightcomputer.tracking;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.events.EngineEventListener;
import org.rocketproplab.marginalstability.flightcomputer.events.ParachuteListener;
import org.rocketproplab.marginalstability.flightcomputer.events.VelocityListener;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

/**
 * A class that keeps track of the flight state of the rocket.
 * <p>
 * It looks at different characteristics to determine which flight mode we are
 * in.
 * <p>
 * We look at the engine activation and shutdown to determine if we are burning
 * or coasting.
 * <p>
 * We look at the velocity updates to determine if we are in apogee, falling or
 * landed.
 * <p>
 * We look at the parachute deploying to determine if we are in descending.
 *
 * @author Max Apodaca
 */
public class FlightState
        implements EngineEventListener, VelocityListener, ParachuteListener {

  private FlightMode currentFlightMode;

  /**
   * Create a new Flight State and initializes to being on the ground.
   */
  public FlightState() {
    this.currentFlightMode = FlightMode.Sitting;
  }

  /**
   * Get the current flight mode which the rocket is in.
   *
   * @return the current flight mode
   */
  public FlightMode getFlightMode() {
    return this.currentFlightMode;
  }

  @Override
  public void onEngineActivation() {
    this.currentFlightMode = FlightMode.Burn;
  }

  @Override
  public void onEngineShutdown() {
    this.currentFlightMode = FlightMode.Coasting;
  }

  @Override
  public void onVelocityUpdate(Vector3 velocity, double time) {
    checkForApogee(velocity);
    checkForFalling(velocity);
    checkForLanded(velocity);
  }

  /**
   * Checks if the rocket should transition into the apogee state
   *
   * @param velocity the current velocity
   */
  private void checkForApogee(Vector3 velocity) {
    if (this.currentFlightMode != FlightMode.Coasting) {
      return;
    }
    double verticalVel = velocity.getZ();
    if (Math.abs(verticalVel) < Settings.APOGEE_SPEED) {
      this.currentFlightMode = FlightMode.Apogee;
    }
  }

  /**
   * Checks if the rocket should transition from apogee to falling
   *
   * @param velocity the current velocity
   */
  private void checkForFalling(Vector3 velocity) {
    boolean isApogee = this.currentFlightMode == FlightMode.Apogee;
    boolean isCoasting = this.currentFlightMode == FlightMode.Coasting;
    if (!(isApogee || isCoasting)) {
      return;
    }
    double verticalVel = velocity.getZ();
    if (verticalVel < -Settings.APOGEE_SPEED) {
      this.currentFlightMode = FlightMode.Falling;
    }
  }

  /**
   * Checks if the rocket should transition into the landed state
   *
   * @param velocity the current velocity
   */
  private void checkForLanded(Vector3 velocity) {
    if (this.currentFlightMode != FlightMode.Descending) {
      return;
    }
    double verticalVel = velocity.getZ();
    if (Math.abs(verticalVel) < Settings.LANDED_SPEED) {
      this.currentFlightMode = FlightMode.Landed;
    }
  }

  @Override
  public void onMainChuteOpen() {
    if (this.currentFlightMode == FlightMode.Landed) {
      return;
    }
    this.currentFlightMode = FlightMode.Descending;
  }

  // Even handlers below are not used

  @Override
  public void onEngineData(EngineDataType type, double value) {
  }

  @Override
  public void onDrogueOpen() {
  }

  @Override
  public void onDrougeCut() {
  }

}
