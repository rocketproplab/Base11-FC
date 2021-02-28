package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.hal.TimeBasedSensorSampler.GetNewDataFunction;
import org.rocketproplab.marginalstability.flightcomputer.hal.TimeBasedSensorSampler.LastReadTimeFunction;

public class TimeBasedSensorSamplerTest {

  private class LastReadTimeImplementation implements LastReadTimeFunction {

    public double lastMeasurementTime;

    @Override
    public double getLastMeasurementTime() {
      return this.lastMeasurementTime;
    }

  }

  private class GetNewDataImplementation implements GetNewDataFunction<Double> {

    public double lastValue;

    @Override
    public Double getNewData() {
      return lastValue;
    }

  }

  private LastReadTimeImplementation     lastReadTime;
  private GetNewDataImplementation       lastReadData;
  private TimeBasedSensorSampler<Double> sampler;

  @Before
  public void before() {
    this.lastReadTime = new LastReadTimeImplementation();
    this.lastReadData = new GetNewDataImplementation();
    this.sampler      = new TimeBasedSensorSampler<Double>(this.lastReadData, this.lastReadTime);
  }

  @Test
  public void firstCallReturnsFirstValue() {
    this.lastReadTime.lastMeasurementTime = 1;
    assertTrue(this.sampler.hasNewData());
    this.lastReadData.lastValue = 10;
    assertEquals(10, this.sampler.getNewData(), 0.0);
    assertEquals(1, this.sampler.getLastSampleTime(), 0);
    assertFalse(this.sampler.hasNewData());
  }

  @Test
  public void afterTimeAdvancesNextSampleAvaliable() {
    this.lastReadTime.lastMeasurementTime = 1;
    this.sampler.getNewData();
    this.lastReadTime.lastMeasurementTime = 1.01;
    this.lastReadData.lastValue           = 35;

    assertTrue(this.sampler.hasNewData());
    assertEquals(35, this.sampler.getNewData(), 0.0);
    this.lastReadTime.lastMeasurementTime = 5;
    assertEquals(1.01, this.sampler.getLastSampleTime(), 0);
  }

}
