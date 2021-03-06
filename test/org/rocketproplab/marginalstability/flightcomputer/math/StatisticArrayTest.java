package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    array.addSample(5.0, 0);
    assertEquals(5, array.getMean(), EPSILON);
  }
  
  @Test
  public void meanOfTwoElementsIsSumOverTwo() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0, 0);
    array.addSample(3.0, 0);
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
    array.addSample(5.0, 0);
    assertEquals(0, array.getVariance(), EPSILON);
  }
  
  @Test
  public void varianceOfTwoElementsIsSumFromMeanSquared() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0, 0);
    array.addSample(3.0, 0);
    assertEquals(2, array.getVariance(), EPSILON);
  }
  
  @Test
  public void varianceOfThreeElementsIsSumFromMeanSquaredOverTwo() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0, 0);
    array.addSample(3.0, 0);
    array.addSample(4.0, 0);
    assertEquals(1, array.getVariance(), EPSILON);
  }
  
  @Test
  public void ringBufferReplacesOldElementInMean() {
    StatisticArray array = new StatisticArray(1);
    array.addSample(5.0, 0);
    array.addSample(3.0, 0);
    assertEquals(3, array.getMean(), EPSILON);
  }
  
  @Test
  public void getMeanReturnsOnlyLastNElements() {
    StatisticArray array = new StatisticArray(3);
    array.addSample(5.0, 0);
    array.addSample(3.0, 0);
    assertEquals(3, array.getMean(1), EPSILON);
  }
  
  @Test
  public void getMeanReturnsAllIfLessThanNElements() {
    StatisticArray array = new StatisticArray(3);
    array.addSample(5.0, 0);
    assertEquals(5, array.getMean(2), EPSILON);
  }
  
  @Test
  public void negativeNInMeanReturnsZero() {
    StatisticArray array = new StatisticArray(3);
    array.addSample(5.0, 0);
    assertEquals(0, array.getMean(-1), EPSILON);
  }
  
  @Test
  public void getVarianceReturnsOnlyLastNElements() {
    StatisticArray array = new StatisticArray(5);
    array.addSample(5.0, 0);
    array.addSample(3.0, 0);
    array.addSample(3.0, 0);
    assertEquals(0, array.getVariance(2), EPSILON);
  }
  
  @Test
  public void getVarianceReturnsAllIfLessThanNElements() {
    StatisticArray array = new StatisticArray(4);
    array.addSample(5.0, 0);
    array.addSample(3.0, 0);
    assertEquals(2, array.getVariance(3), EPSILON);
  }
  
  @Test
  public void negativeNInVarianceReturnsZero() {
    StatisticArray array = new StatisticArray(3);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0);
    assertEquals(0, array.getVariance(-1), EPSILON);
  }
  
  @Test
  public void timeBasedMeanReturnsLastSecondOfSamples() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0.3);
    array.addSample(7.0, 0.6);
    assertEquals(6.5, array.getMean(0.5), EPSILON);
  }
  
  @Test
  public void timeBasedMeanReturnsAllSamplesWhenTooLongInterval() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0.3);
    array.addSample(7.0, 0.6);
    assertEquals(6, array.getMean(1.5), EPSILON);
  }
  
  @Test
  public void timeBasedMeanReturnsZeroWithNegativeTime() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0.3);
    array.addSample(7.0, 0.6);
    assertEquals(0, array.getMean(-1.5), EPSILON);
  }
  
  @Test
  public void timeBasedVarianceReturnsLastSecondOfSamples() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0.3);
    array.addSample(7.0, 0.6);
    assertEquals(0.5, array.getVariance(0.5), EPSILON);
  }
  
  @Test
  public void timeBasedVarianceReturnsAllSamplesWhenTooLongInterval() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0.3);
    array.addSample(6.0, 0.5);
    array.addSample(7.0, 0.6);
    assertEquals(2/3D, array.getVariance(1.5), EPSILON);
  }
  
  @Test
  public void timeBasedVarianceReturnsZeroWithNegativeTime() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0.3);
    array.addSample(7.0, 0.6);
    assertEquals(0, array.getVariance(-1.5), EPSILON);
  }
  
  @Test
  public void withNoValidatorDataIsAlwaysValid() {
    StatisticArray array = new StatisticArray(10);
    assertTrue(array.isValid());
    array.addSample(7.0, 0.6);
    assertTrue(array.isValid());
  }
  
  @Test
  public void validatorUsedForValidation() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(7.0, 0.6);
    
    array.setValidator(a -> true);
    assertTrue(array.isValid());
    array.setValidator(a -> false);
    assertFalse(array.isValid());
  }
  
  @Test
  public void SingleSampleTimeVarianceReturnsZero() {
    StatisticArray array = new StatisticArray(10);
    array.addSample(5.0, 0);
    array.addSample(6.0, 0.3);
    array.addSample(7.0, 2);
    assertEquals(0, array.getVariance(0.5), EPSILON);
  }
  
}
