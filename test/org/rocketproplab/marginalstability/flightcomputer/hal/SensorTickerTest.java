package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SensorTickerTest {
  
  private static int staticCounter = 0;

  @Test
  public void sensorTickerShouldAlwaysTickAtStart() {
    SensorTicker ticker = new SensorTicker(null, 1);
    assertTrue(ticker.shouldTickSensor(0));
  }
  
  @Test
  public void afterCallToTickSensorShouldNotTickAtSameTime() {
    SensorTicker ticker = new SensorTicker(() -> {} , 1);
    ticker.tick(0);
    assertFalse(ticker.shouldTickSensor(0));
  }
  
  @Test
  public void afterCallToTickAndWaitTicksAgain() {
    SensorTicker ticker = new SensorTicker(() -> {} , 1);
    ticker.tick(0);
    assertTrue(ticker.shouldTickSensor(1.1));
  }
  
  @Test
  public void tooShortDirrationDoesNotRetick() {
    SensorTicker ticker = new SensorTicker(() -> {} , 1);
    ticker.tick(0);
    assertFalse(ticker.shouldTickSensor(0.9));
  }
  
  @Test
  public void invalidTickDoesNotDeferValidTick() {
    SensorTicker ticker = new SensorTicker(() -> {} , 1);
    ticker.tick(0);
    ticker.tick(0.9);
    assertTrue(ticker.shouldTickSensor(1.1));
  }
  
  @Test
  public void ticksIncrementAtRateNotFromLastTick() {
    SensorTicker ticker = new SensorTicker(() -> {} , 1);
    ticker.tick(0);
    ticker.tick(1.1);
    assertTrue(ticker.shouldTickSensor(2.05));
  }
  
  @Test
  public void tickActuallyTicksSensor() {
    SensorTickerTest.staticCounter = 0;
    SensorTicker ticker = new SensorTicker(() -> SensorTickerTest.staticCounter++, 1);
    ticker.tick(0);
    assertEquals(1, SensorTickerTest.staticCounter);
  }
  
  @Test
  public void sensorAtRateZeroAlwaysCalled() {
    SensorTickerTest.staticCounter = 0;
    SensorTicker ticker = new SensorTicker(() -> SensorTickerTest.staticCounter++, 0);
    ticker.tick(0);
    ticker.tick(0);
    assertEquals(2, SensorTickerTest.staticCounter);
  }
  
  @Test
  public void tickingAtReallyLongRateDoesNotTickTwice() {
    SensorTickerTest.staticCounter = 0;
    SensorTicker ticker = new SensorTicker(() -> SensorTickerTest.staticCounter++, 1);
    ticker.tick(0);
    ticker.tick(2);
    assertEquals(2, SensorTickerTest.staticCounter);
  }
  
}
