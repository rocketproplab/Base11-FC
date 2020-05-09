package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.hal.Barometer;
import org.rocketproplab.marginalstability.flightcomputer.hal.Solenoid;
import org.rocketproplab.marginalstability.flightcomputer.math.InterpolatingVector3;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;
import org.rocketproplab.marginalstability.flightcomputer.tracking.FlightMode;

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
    @Override
    public double getPressure() {
      return -1;
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

  @Before
  public void init() {
    main       = new TestSolenoid();
    drogue     = new TestSolenoid();
    time       = new TestTime();
    barometer  = new TestBarometer();
    paraSystem = new ParachuteSubsystem(main, drogue, time, barometer);
  }

  @Test
  public void parachuteDoesNotOpenInSitting() {
    paraSystem.onFlightModeChange(FlightMode.Sitting);
    paraSystem.update();
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void parachuteDoesNotOpenInBurn() {
    paraSystem.onFlightModeChange(FlightMode.Burn);
    paraSystem.update();
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void parachuteDoesNotOpenInCoast() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    paraSystem.update();
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void drogueOpensAtApogee() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    paraSystem.onFlightModeChange(FlightMode.Apogee);
    paraSystem.update();
    assertFalse(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void drogueOpensEvenIfApogeeIsSkipped() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    paraSystem.onFlightModeChange(FlightMode.Falling);
    paraSystem.update();
    assertFalse(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void mainDoesNotActivateOnWayUpBurn() {
    paraSystem.onFlightModeChange(FlightMode.Burn);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    paraSystem.update();
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void mainDoesNotActivateOnWayUpCoast() {
    paraSystem.onFlightModeChange(FlightMode.Coasting);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    paraSystem.update();
    assertFalse(main.active);
    assertFalse(drogue.active);
  }

  @Test
  public void mainDeploysOnWayDownFalling() {
    paraSystem.onFlightModeChange(FlightMode.Falling);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2));
    paraSystem.update();
    assertTrue(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void mainDoesntDeployOnWayDownTooHighWhileFalling() {
    paraSystem.onFlightModeChange(FlightMode.Falling);
    paraSystem.onPositionEstimate(
            new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT * 2));
    paraSystem.update();
    assertFalse(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void mainDeploysThroughInterpolationWhileFalling() {
    paraSystem.onFlightModeChange(FlightMode.Falling);
    TestIntVec testVec = new TestIntVec(0, 0, Settings.MAIN_CHUTE_HEIGHT * 2);
    testVec.setAfter(0, 0, Settings.MAIN_CHUTE_HEIGHT / 2, 10);
    paraSystem.onPositionEstimate(testVec);
    paraSystem.update();
    assertFalse(main.active);
    assertTrue(drogue.active);
    time.time = 20;
    paraSystem.update();
    assertTrue(main.active);
    assertTrue(drogue.active);
  }

  @Test
  public void mainDeploysWhenPacketReceived() {
    SCMPacket mainDeploy = new SCMPacket(SCMPacketType.MD, "00000");
    paraSystem.onPacket(PacketDirection.RECIVE, mainDeploy);
    assertTrue(main.active);
  }

  @Test
  public void drogueDeploysWhenPacketsRecived() {
    SCMPacket drogueDeploy = new SCMPacket(SCMPacketType.DD, "00000");
    paraSystem.onPacket(PacketDirection.RECIVE, drogueDeploy);
    assertTrue(drogue.active);
  }

}
