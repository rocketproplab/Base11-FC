package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TimedRingBufferTest {
  private static final double EPSILON = 1e-10;

  @Test
  public void addIncreasesSamplesByOne() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    assertEquals(0, ringBuffer.size());
    ringBuffer.add(1.0, 0);
    assertEquals(1, ringBuffer.size());
  }

  @Test
  public void getReturnsListOfSingleElement() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(1.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get().forEachRemaining(list::add);
    assertEquals(1, list.size());
    assertEquals(1.0, list.get(0), EPSILON);
  }

  @Test
  public void getReturnsListOfSingleElementAfterOverflow() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(1);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get().forEachRemaining(list::add);
    assertEquals(1, list.size());
    assertEquals(2.0, list.get(0), EPSILON);
  }

  @Test
  public void getReturnsListOfTwoElementAfterOverflow() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(2);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    ringBuffer.add(3.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get().forEachRemaining(list::add);
    assertEquals(2, list.size());
    assertEquals(2.0, list.get(0), EPSILON);
    assertEquals(3.0, list.get(1), EPSILON);
  }

  @Test
  public void TwoIteratorsOfOverrideWork() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(2);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get().forEachRemaining(list::add);
    assertEquals(2, list.size());
    assertEquals(4.0, list.get(0), EPSILON);
    assertEquals(5.0, list.get(1), EPSILON);
  }

  @Test
  public void getNReturnsLastN() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(2).forEachRemaining(list::add);
    assertEquals(2, list.size());
    assertEquals(4.0, list.get(0), EPSILON);
    assertEquals(5.0, list.get(1), EPSILON);
  }

  @Test
  public void getNReturnsLastNOverBoundary() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(5);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0);
    ringBuffer.add(7.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(3).forEachRemaining(list::add);
    assertEquals(3, list.size());
    assertEquals(5.0, list.get(0), EPSILON);
    assertEquals(6.0, list.get(1), EPSILON);
    assertEquals(7.0, list.get(2), EPSILON);
  }

  @Test
  public void getNReturnsLastNOverBoundaryAtSize() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(5);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0);
    ringBuffer.add(7.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(5).forEachRemaining(list::add);
    assertEquals(5, list.size());
    assertEquals(3.0, list.get(0), EPSILON);
    assertEquals(4.0, list.get(1), EPSILON);
    assertEquals(5.0, list.get(2), EPSILON);
    assertEquals(6.0, list.get(3), EPSILON);
    assertEquals(7.0, list.get(4), EPSILON);
  }

  @Test
  public void getNReturnsMaxForTooLargeN() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(5);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0);
    ringBuffer.add(7.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(49).forEachRemaining(list::add);
    assertEquals(5, list.size());
    assertEquals(3.0, list.get(0), EPSILON);
    assertEquals(4.0, list.get(1), EPSILON);
    assertEquals(5.0, list.get(2), EPSILON);
    assertEquals(6.0, list.get(3), EPSILON);
    assertEquals(7.0, list.get(4), EPSILON);
  }

  @Test
  public void getNReturnsMaxForTooLargeNNotfull() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0);
    ringBuffer.add(7.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(49).forEachRemaining(list::add);
    assertEquals(5, list.size());
    assertEquals(3.0, list.get(0), EPSILON);
    assertEquals(4.0, list.get(1), EPSILON);
    assertEquals(5.0, list.get(2), EPSILON);
    assertEquals(6.0, list.get(3), EPSILON);
    assertEquals(7.0, list.get(4), EPSILON);
  }

  @Test
  public void forLoopIterationWorks() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(1.0, 0);
    ringBuffer.add(2.0, 0);
    double accumulator = 0;
    for (double element : ringBuffer.get()) {
      assertEquals(++accumulator, element, EPSILON);
    }

    accumulator = 0;
    for (double element : ringBuffer) {
      assertEquals(++accumulator, element, EPSILON);
    }
  }
  
  @Test
  public void getLastSecondReturnsAllAtSameTime() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0);
    ringBuffer.add(7.0, 0);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(1.0).forEachRemaining(list::add);
    assertEquals(5, list.size());
    assertEquals(3.0, list.get(0), EPSILON);
    assertEquals(4.0, list.get(1), EPSILON);
    assertEquals(5.0, list.get(2), EPSILON);
    assertEquals(6.0, list.get(3), EPSILON);
    assertEquals(7.0, list.get(4), EPSILON);
  }
  
  @Test
  public void getLastSecondReturnsAllWithingLastSecond() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0.5);
    ringBuffer.add(7.0, 1.5);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(1.0).forEachRemaining(list::add);
    assertEquals(2, list.size());
    assertEquals(6.0, list.get(0), EPSILON);
    assertEquals(7.0, list.get(1), EPSILON);
  }
  
  @Test
  public void indexOutOfBoundsWithNegativeGet() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0.5);
    ringBuffer.add(7.0, 1.5);
    assertThrows(IndexOutOfBoundsException.class, () ->{
      ringBuffer.get(-1);
    });
    assertThrows(IndexOutOfBoundsException.class, () ->{
      ringBuffer.get(-2);
    });
    assertThrows(IndexOutOfBoundsException.class, () ->{
      ringBuffer.get(-9);
    });
  }
  
  @Test
  public void getWithNegativeTimeReturnsZero() {
    TimedRingBuffer<Double> ringBuffer = new TimedRingBuffer<>(10);
    ringBuffer.add(3.0, 0);
    ringBuffer.add(4.0, 0);
    ringBuffer.add(5.0, 0);
    ringBuffer.add(6.0, 0.5);
    ringBuffer.add(7.0, 1.5);
    List<Double> list = new ArrayList<Double>();
    ringBuffer.get(-1.0).forEachRemaining(list::add);
    assertEquals(0, list.size());
  }

}
