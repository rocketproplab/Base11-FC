package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

public class StatisticCollectorTest {
  private static final double EPSILON = 1e-10;

  @Test
  public void newlyCreatedObjectDoesNotHaveNext() {
    StatisticCollector collector = new StatisticCollector(1.0);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void afterSamplingASingleStatisticInPeriodStillNoNext() {
    StatisticCollector collector = new StatisticCollector(1.0);
    collector.sampleStatistic(0, 1);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void afterFullSamplePeriodHasNext() {
    StatisticCollector collector = new StatisticCollector(1.0);
    collector.sampleStatistic(0, 1);
    collector.sampleStatistic(1.1, 1);
    assertTrue(collector.hasNext());
    assertEquals(1.0, collector.getNext().getMean(), EPSILON);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void differentValueOutputsDifferentPacket() {
    StatisticCollector collector = new StatisticCollector(1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(1.1, 1);
    assertTrue(collector.hasNext());
    assertEquals(5.0, collector.getNext().getMean(), EPSILON);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void multipleSamplesEvenlyAveraged() {
    StatisticCollector collector = new StatisticCollector(1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(1.1, 1);
    assertTrue(collector.hasNext());
    assertEquals(3.0, collector.getNext().getMean(), EPSILON);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void sampleAtDifferentRates() {
    StatisticCollector collector = new StatisticCollector(2.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(1.1, 1);
    collector.sampleStatistic(2.1, 1);
    assertTrue(collector.hasNext());
    assertEquals(7/3D, collector.getNext().getMean(), EPSILON);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void twoSamplingPeriods() {
    StatisticCollector collector = new StatisticCollector(1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(1.1, 1);
    collector.sampleStatistic(2.1, 1);
    assertTrue(collector.hasNext());
    assertEquals(3.0, collector.getNext().getMean(), EPSILON);
    assertTrue(collector.hasNext());
    assertEquals(1.0, collector.getNext().getMean(), EPSILON);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void skippingTimePeriodWorks() {
    StatisticCollector collector = new StatisticCollector(1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(5.1, 1);
    collector.sampleStatistic(5.9, 1);
    assertTrue(collector.hasNext());
    assertEquals(3.0, collector.getNext().getMean(), EPSILON);
    assertFalse(collector.hasNext());
  }
  
}
