package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;

public class StatisticCollectorTest {

  @Test
  public void newlyCreatedObjectDoesNotHaveNext() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.ER, 1.0);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void afterSamplingASingleStatisticInPeriodStillNoNext() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.ER, 1.0);
    collector.sampleStatistic(0, 1);
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void afterFullSamplePeriodHasNext() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.ER, 1.0);
    collector.sampleStatistic(0, 1);
    collector.sampleStatistic(1.1, 1);
    assertTrue(collector.hasNext());
    SCMPacket expected = new SCMPacket(SCMPacketType.ER, "1.0  ");
    assertEquals(expected, collector.getNext());
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void differentPacketTypeOutputsDifferentPacket() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.GX, 1.0);
    collector.sampleStatistic(0, 1);
    collector.sampleStatistic(1.1, 1);
    assertTrue(collector.hasNext());
    SCMPacket expected = new SCMPacket(SCMPacketType.GX, "1.0  ");
    assertEquals(expected, collector.getNext());
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void differentValueOutputsDifferentPacket() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.GX, 1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(1.1, 1);
    assertTrue(collector.hasNext());
    SCMPacket expected = new SCMPacket(SCMPacketType.GX, "5.0  ");
    assertEquals(expected, collector.getNext());
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void multipleSamplesEvenlyAveraged() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.GX, 1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(1.1, 1);
    assertTrue(collector.hasNext());
    SCMPacket expected = new SCMPacket(SCMPacketType.GX, "3.0  ");
    assertEquals(expected, collector.getNext());
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void sampleAtDifferentRates() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.GX, 2.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(1.1, 1);
    collector.sampleStatistic(2.1, 1);
    assertTrue(collector.hasNext());
    SCMPacket expected = new SCMPacket(SCMPacketType.GX, "2.333");
    assertEquals(expected, collector.getNext());
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void twoSamplingPeriods() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.GX, 1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(1.1, 1);
    collector.sampleStatistic(2.1, 1);
    assertTrue(collector.hasNext());
    SCMPacket expected = new SCMPacket(SCMPacketType.GX, "3.0  ");
    assertEquals(expected, collector.getNext());
    assertTrue(collector.hasNext());
    expected = new SCMPacket(SCMPacketType.GX, "1.0  ");
    assertEquals(expected, collector.getNext());
    assertFalse(collector.hasNext());
  }
  
  @Test
  public void skippingTimePeriodWorks() {
    StatisticCollector collector = new StatisticCollector(SCMPacketType.GX, 1.0);
    collector.sampleStatistic(0, 5);
    collector.sampleStatistic(0.5, 1);
    collector.sampleStatistic(5.1, 1);
    collector.sampleStatistic(5.9, 1);
    assertTrue(collector.hasNext());
    SCMPacket expected = new SCMPacket(SCMPacketType.GX, "3.0  ");
    assertEquals(expected, collector.getNext());
    assertFalse(collector.hasNext());
  }
  
}
