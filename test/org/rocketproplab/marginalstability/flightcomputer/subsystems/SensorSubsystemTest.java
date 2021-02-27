package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;

public class SensorSubsystemTest {
  private class TestTime extends Time {
    public double time = 0;

    @Override
    public double getSystemTime() {
      return time;
    }
  }

  private static int staticCounter;

  private SensorSubsystem sensorSubsystem;
  private TestTime        time;
  private Looper          looper;

  @Before
  public void before() {
    this.time                         = new TestTime();
    this.sensorSubsystem              = new SensorSubsystem(this.time);
    this.looper                       = new Looper(time);
    this.sensorSubsystem.prepare(looper);
    SensorSubsystemTest.staticCounter = 0;
  }

  @Test
  public void subsystemDoesNothingWhenEmptyUpdateCalled() {
    this.looper.tick();
  }

  @Test
  public void pollingSensorAtRateZeroCalledEverytime() {
    this.sensorSubsystem.addSensor(() -> SensorSubsystemTest.staticCounter++, 0);
    this.looper.tick();
    assertEquals(1, SensorSubsystemTest.staticCounter);
    this.looper.tick();
    assertEquals(2, SensorSubsystemTest.staticCounter);
  }

  @Test
  public void pollingSensorCalledFirstTimeEvenAtZeroTime() {
    this.sensorSubsystem.addSensor(() -> SensorSubsystemTest.staticCounter++, 10000);
    this.looper.tick();
    assertEquals(1, SensorSubsystemTest.staticCounter);
  }

  @Test
  public void nonZeroDelayNotRepeatedWhenUpdateCalled() {
    this.sensorSubsystem.addSensor(() -> SensorSubsystemTest.staticCounter++, 10);
    this.looper.tick();
    assertEquals(1, SensorSubsystemTest.staticCounter);
    this.looper.tick();
    assertEquals(1, SensorSubsystemTest.staticCounter);
  }

  @Test
  public void advancingTimeTriggersNextTick() {
    this.sensorSubsystem.addSensor(() -> SensorSubsystemTest.staticCounter++, 10);
    this.looper.tick();
    this.time.time = 11;
    this.looper.tick();
    assertEquals(2, SensorSubsystemTest.staticCounter);
  }

}
