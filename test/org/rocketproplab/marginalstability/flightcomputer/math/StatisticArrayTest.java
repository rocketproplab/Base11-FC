package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatisticArrayTest {

  private static final double EPSILON = 1e-10;
  
  @Test
  public void meanIsZeroOfNoElements() {
    StatisticArray array = new StatisticArray(5);
    assertEquals(0, array.getMean(), EPSILON);
  }
  
  @Test
  public void meanOfOneElementIsThatElement() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0);
    assertEquals(5, array.getMean(), EPSILON);
  }
  
  @Test
  public void meanOfTwoElementsIsSumOverTwo() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0);
    array.addSample(3.0);
    assertEquals(4, array.getMean(), EPSILON);
  }
  
  @Test
  public void varianceOfZeroElementsIsZero() {
    StatisticArray array = new StatisticArray(5);
    assertEquals(0, array.getVariance(), EPSILON);
  }
  
  @Test
  public void varianceOfOneElementsIsZero() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0);
    assertEquals(0, array.getVariance(), EPSILON);
  }
  
  @Test
  public void varianceOfTwoElementsIsSumFromMeanSquared() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0);
    array.addSample(3.0);
    assertEquals(2, array.getVariance(), EPSILON);
  }
  
  @Test
  public void varianceOfThreeElementsIsSumFromMeanSquaredOverTwo() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0);
    array.addSample(3.0);
    array.addSample(4.0);
    assertEquals(1, array.getVariance(), EPSILON);
  }
  
  @Test
  public void ringBufferReplacesOldElementInMean() {
    StatisticArray array = new StatisticArray(1);
    array.addSample(5.0);
    array.addSample(3.0);
    assertEquals(3, array.getMean(), EPSILON);
  }
  
}
