package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.hal.Barometer;
import org.rocketproplab.marginalstability.flightcomputer.hal.Solenoid;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestParachuteSubsystem {

  private class TestSolenoid implements Solenoid {

    public boolean active = false;

    @Override
    public boolean isActive() {
      return active;
    }

    @Override
    public void set(boolean active) {
      this.active = active;
    }

  }

  private class TestTime extends Time {
    public double time = 0;

    public double getSystemTime() {
      return time;
    }
  }

  private class TestIntVec implements InterpolatingVector3 {

    public Vector3 vec      = new Vector3(0, 0, 0);
    public Vector3 vecAfter = new Vector3(0, 0, 0);
    public double  time     = 1;

    public TestIntVec(double x, double y, double z) {
      vec = new Vector3(x, y, z);
    }

    public void setAfter(double x, double y, double z, double time) {
      vecAfter  = new Vector3(x, y, z);
      this.time = time;
    }

    @Override
    public Vector3 getAt(double time) {
      if (time < this.time) {
        return vec;
      }
      return vecAfter;
    }

  }

  private class TestBarometer implements Barometer {
    public double pressure = -1;

    @Override
    public double getPressure() {
      return pressure;
    }

    @Override
    public boolean inUsableRange() {
      return false;
    }

    @Override
    public double getLastMeasurementTime() {
      return 0;
    }
  }

  private ParachuteSubsystem paraSystem;
  private TestSolenoid       main;
  private TestSolenoid       drogue;
  private TestTime           time;
  private TestBarometer      barometer;
  private Looper             looper;

  @Before
  public void init() {
    this.main       = new TestSolenoid();
    this.drogue     = new TestSolenoid();
    this.time       = new TestTime();
    this.barometer  = new TestBarometer();
    this.paraSystem = new ParachuteSubsystem(main, drogue, time, barometer);
    this.looper     = new Looper(time);
    this.paraSystem.prepare(looper);
  }

  @Test
  public void parachuteDoesNotOpenInSitting() {
    paraSystem.onFlightModeChange(FlightMode.Sitting);
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void parachuteDoesNotOpenInBurn() {
    paraSystem.onFlightModeChange(FlightMode.Burn);
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void parachuteDoesNotOpenInCoast() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void drogueOpensAtApogee() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    paraSystem.onFlightModeChange(FlightMode.Apogee);
    assertFalse(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void drogueOpensEvenIfApogeeIsSkipped() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    paraSystem.onFlightModeChange(FlightMode.Falling);
    assertFalse(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void mainDoesNotActivateOnWayUpBurn() {
    paraSystem.onFlightModeChange(FlightMode.Burn);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    looper.tick();
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void mainDoesNotActivateOnWayUpCoast() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    looper.tick();
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void mainDeploysOnWayDownFalling() {
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    paraSystem.onFlightModeChange(FlightMode.Falling);
    barometer.pressure = -1;
    looper.tick();
    time.time = 20;
    looper.tick();
    assertTrue(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void mainDoesNotDeployOnWayDownTooHighWhileFalling() {
    paraSystem.onFlightModeChange(FlightMode.Falling);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT * 2));
    looper.tick();
    assertFalse(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void mainDeploysThroughInterpolationWhileFalling() {
    TestIntVec testVec = new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT * 2);
    testVec.setAfter(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2, 10);
    paraSystem.onPositionEstimate(testVec);
    paraSystem.onFlightModeChange(FlightMode.Falling);
    barometer.pressure = -1;
    looper.tick();
    assertFalse(main.active);
    assertTrue(drogue.active);

    // chute does not deploy immediately when pressure drops below threshold
    time.time = 20;
    looper.tick();
    assertFalse(main.active);
    assertTrue(drogue.active);

    // chute deploys after the pressure has been below the threshold for some time
    time.time = 30;
    looper.tick();
    assertTrue(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void parachuteDoesNotOpenAbovePressure() {
    paraSystem.onFlightModeChange(FlightMode.Falling);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    barometer.pressure = 1;
    looper.tick();
    assertFalse(main.active);
    time.time = 20;
    looper.tick();
    assertFalse(main.active);
  }

  @Test
  public void parachuteOpensAfterTimeThreshold() {
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    paraSystem.onFlightModeChange(FlightMode.Falling);
    barometer.pressure = -1;
    looper.tick();
    assertFalse(main.active);
    time.time = 20;
    looper.tick();
    assertTrue(main.active);
  }
}
