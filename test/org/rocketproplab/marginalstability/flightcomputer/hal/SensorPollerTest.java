package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SensorPollerTest {
  
  private static int staticCounter = 0;

  @Test
  public void sensorPollerShouldAlwaysPollAtStart() {
    SensorPoller poller = new SensorPoller(null, 1);
    assertTrue(poller.shouldPollSensor(0));
  }
  
  @Test
  public void afterCallToPollSensorShouldNotPollAtSameTime() {
    SensorPoller poller = new SensorPoller(() -> {} , 1);
    poller.update(0);
    assertFalse(poller.shouldPollSensor(0));
  }
  
  @Test
  public void afterCallToUpdateAndWaitMakesPollerPollAgain() {
    SensorPoller poller = new SensorPoller(() -> {} , 1);
    poller.update(0);
    assertTrue(poller.shouldPollSensor(1.1));
  }
  
  @Test
  public void tooShortDurationDoesNotRepoll() {
    SensorPoller poller = new SensorPoller(() -> {} , 1);
    poller.update(0);
    assertFalse(poller.shouldPollSensor(0.9));
  }
  
  @Test
  public void invalidUpdateDoesNotDeferValidPoll() {
    SensorPoller poller = new SensorPoller(() -> {} , 1);
    poller.update(0);
    poller.update(0.9);
    assertTrue(poller.shouldPollSensor(1.1));
  }
  
  @Test
  public void polledBasedOnRateNotFromLastPoll() {
    SensorPoller poller = new SensorPoller(() -> {} , 1);
    poller.update(0);
    poller.update(1.1);
    assertTrue(poller.shouldPollSensor(2.05));
  }
  
  @Test
  public void updatePollsSensor() {
    SensorPollerTest.staticCounter = 0;
    SensorPoller poller = new SensorPoller(() -> SensorPollerTest.staticCounter++, 1);
    poller.update(0);
    assertEquals(1, SensorPollerTest.staticCounter);
  }
  
  @Test
  public void sensorAtRateZeroAlwaysCalled() {
    SensorPollerTest.staticCounter = 0;
    SensorPoller poller = new SensorPoller(() -> SensorPollerTest.staticCounter++, 0);
    poller.update(0);
    poller.update(0);
    assertEquals(2, SensorPollerTest.staticCounter);
  }
  
  @Test
  public void pollingAtReallyLongRateDoesNotPollTwice() {
    SensorPollerTest.staticCounter = 0;
    SensorPoller poller = new SensorPoller(() -> SensorPollerTest.staticCounter++, 1);
    poller.update(0);
    poller.update(2.1);
    assertEquals(2, SensorPollerTest.staticCounter);
  }
  
}
