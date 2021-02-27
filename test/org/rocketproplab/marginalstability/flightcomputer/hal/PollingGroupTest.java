package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PollingGroupTest {
  private static int staticCounter;

  @Test
  public void pollingGroupPollsBothSensors() {
    staticCounter = 0;
    PollingGroup group = new PollingGroup(() -> staticCounter++, () -> staticCounter += 2);
    group.poll();
    assertEquals(3, staticCounter);
  }
}
