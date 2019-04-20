package org.rocketproplab.marginalstability.flightcomputer.tracking;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

public class TestFlightState {

  @Test
  public void FlightStateStartsOnGround() {
    FlightState flightState = new FlightState();
    assertEquals(FlightMode.Sitting, flightState.getFlightMode());
  }

  @Test
  public void AfterEngineIgnitionEventFlightStateIsFlying() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    assertEquals(FlightMode.Burn, flightState.getFlightMode());
  }

  @Test
  public void AfterEngineShutdownEventFlightStateIsCoasting() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    assertEquals(FlightMode.Coasting, flightState.getFlightMode());
  }

  @Test
  public void DuringCoastAtHighSpeedRocketStaysInCoast() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 100),
        0);
    assertEquals(FlightMode.Coasting, flightState.getFlightMode());
  }

  @Test
  public void DuringCoastAtLowSpeedRocketTransitionsToApogee() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 100),
        0);

    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED - 1),
        1);
    assertEquals(FlightMode.Apogee, flightState.getFlightMode());
  }
  
  @Test
  public void RocketStateStaysInApogeeIfPositiveVelocityExceeded() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 100),
        0);

    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED - 1),
        1);
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 1),
        2);
    assertEquals(FlightMode.Apogee, flightState.getFlightMode());
  }
  
  @Test
  public void InFallingIfThresholdVelocityExceeded() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 100),
        0);

    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED - 1),
        1);
    flightState.onVelocityUpdate(new Vector3(0, 0, -Settings.APOGEE_SPEED - 1),
        2);
    assertEquals(FlightMode.Falling, flightState.getFlightMode());
  }
  
  @Test
  public void InDescendingAfterMainChuteOpens() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 100),
        0);

    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED - 1),
        2);
    flightState.onDrogueOpen();
    flightState.onVelocityUpdate(new Vector3(0, 0, -Settings.APOGEE_SPEED - 1),
        3);
    flightState.onDrougeCut();
    flightState.onMainChuteOpen();
    assertEquals(FlightMode.Descending, flightState.getFlightMode());
  }
  
  @Test
  public void StillFallingWithZeroVelocityInFallingWithoutMainChuteOpen() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 100),
        0);

    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED - 1),
        2);
    flightState.onDrogueOpen();
    flightState.onVelocityUpdate(new Vector3(0, 0, -Settings.APOGEE_SPEED - 1),
        3);
    flightState.onDrougeCut();
    flightState.onVelocityUpdate(new Vector3(0,0,0), 5);
    assertEquals(FlightMode.Falling, flightState.getFlightMode());
  }
  
  @Test
  public void LandedIfZeroVelocityAndMainChuteOpen() {
    FlightState flightState = new FlightState();
    flightState.onEngineActivation();
    flightState.onEngineShutdown();
    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED + 100),
        0);

    flightState.onVelocityUpdate(new Vector3(0, 0, Settings.APOGEE_SPEED - 1),
        2);
    flightState.onDrogueOpen();
    flightState.onVelocityUpdate(new Vector3(0, 0, -Settings.APOGEE_SPEED - 1),
        3);
    flightState.onDrougeCut();
    flightState.onMainChuteOpen();
    flightState.onVelocityUpdate(new Vector3(0,0,0), 5);
    assertEquals(FlightMode.Landed, flightState.getFlightMode());
  }

}
