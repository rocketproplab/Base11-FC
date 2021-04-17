package org.rocketproplab.marginalstability.flightcomputer.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A ring buffer that allows you to iterate based on time
 *
 * @param <E> The type which the buffer is of
 * @author Max Apodaca
 */
public class TimedRingBuffer<E> implements Iterable<E> {

  /**
   * An iterator for the ring buffer, will iterate section of ring buffer in
   * increasing add time.
   *
   * @author Max Apodaca
   */
  public class RingBufferIterator implements Iterator<E>, Iterable<E> {
    private Iterator<E> sublistIterator;
    private Iterator<E> nextSublistIterator;

    /**
     * Create a new ring buffer iterator that will iterate the first iterator then
     * the second
     *
     * @param sublistIterator     first iterator to iterate
     * @param nextSublistIterator next iterator to iterate
     */
    private RingBufferIterator(Iterator<E> sublistIterator, Iterator<E> nextSublistIterator) {
      this.sublistIterator = sublistIterator;
      this.nextSublistIterator = nextSublistIterator;
    }

    @Override
    public boolean hasNext() {
      boolean hasNext = this.sublistIterator.hasNext();
      hasNext = hasNext || this.nextSublistIterator.hasNext();
      return hasNext;
    }

    @Override
    public E next() {
      if (this.sublistIterator.hasNext()) {
        return this.sublistIterator.next();
      }
      return this.nextSublistIterator.next();
    }

    @Override
    public Iterator<E> iterator() {
      return this;
    }

  }

  private int capacity;
  private int insertPointer;
  private ArrayList<E> elements;
  private double[] times;

  /**
   * Create a new timed ring buffer with the given capacity
   *
   * @param capacity how many elements to store at most
   */
  public TimedRingBuffer(int capacity) {
    this.capacity = capacity;
    this.insertPointer = 0;
    this.elements = new ArrayList<E>(capacity);
    this.times = new double[capacity];
  }

  /**
   * Add a new element to the buffer and specifies the time
   *
   * @param value the element to add
   * @param time  the time at which the element was added, must be greater than or
   *              equal to the previous time
   */
  public void add(E value, double time) {
    if (this.size() < this.capacity) {
      this.elements.add(value);
    } else {
      this.elements.set(this.insertPointer, value);
    }
    this.times[this.insertPointer] = time;
    this.insertPointer++;
    this.insertPointer %= this.capacity;
  }

  /**
   * Get how many elements are in the buffer
   *
   * @return the number of elements in the buffer
   */
  public int size() {
    return this.elements.size();
  }

  /**
   * Get an iterator for the whole buffer. Iterates in increasing time
   *
   * @return an iterator for the whole buffer
   */
  public RingBufferIterator get() {
    if (this.size() < this.capacity) {
      return new RingBufferIterator(this.elements.iterator(), Collections.emptyIterator());
    }
    int elementsSize = this.elements.size();
    List<E> sublistA = this.elements.subList(this.insertPointer, elementsSize);
    List<E> sublistB = this.elements.subList(0, this.insertPointer);
    RingBufferIterator result = new RingBufferIterator(sublistA.iterator(), sublistB.iterator());
    return result;
  }

  /**
   * Takes the mathematical definition of modulo so that -1 mod 5 = 4
   *
   * @param numerator the numerator in the modulo division
   * @param divisor   the divisor in the modulo division
   * @return numerator modulo divisor
   */
  private int trueMod(int numerator, int divisor) {
    int fakeMod = numerator % divisor;
    if (fakeMod < 0) {
      return divisor + fakeMod;
    }
    return fakeMod;
  }

  /**
   * Get the most recent n elements from the ring buffer. If n is larger than the
   * number of elements all the elements are returned. The elements are iterated
   * in increasing order of time.
   * <p>
   * Returns size if n > size.
   *
   * @param n the number of elements to get
   * @return an iterator for the most recent n elements
   * @throws IndexOutOfBoundsException if n < 0.
   */
  public RingBufferIterator get(int n) {
    if (n < 0) {
      throw new IndexOutOfBoundsException("Tried to get index " + n);
    }
    if (n > this.size()) {
      n = this.size();
    }
    if (n == 0) {
      return new RingBufferIterator(Collections.emptyIterator(), Collections.emptyIterator());
    }
    int start = this.trueMod(this.insertPointer - n, this.capacity);
    int end = this.insertPointer;
    if (start < end) {
      List<E> sublistA = this.elements.subList(start, end);
      return new RingBufferIterator(sublistA.iterator(), Collections.emptyIterator());
    } else {
      List<E> sublistA = this.elements.subList(start, this.capacity);
      List<E> sublistB = this.elements.subList(0, end);
      return new RingBufferIterator(sublistA.iterator(), sublistB.iterator());
    }
  }

  /**
   * Get an iterator for the elements within the last pastTime seconds.
   *
   * @param pastTime how far in the past to look
   * @return an iterator for the elements added within the last pastTime seconds.
   */
  public RingBufferIterator get(double pastTime) {
    int numberToGet = 1;
    double firstTime = this.times[this.trueMod(this.insertPointer - 1, this.capacity)];
    for (numberToGet = 1; numberToGet < this.size(); numberToGet++) {
      int index = this.insertPointer - numberToGet - 1;
      int modIndex = this.trueMod(index, this.capacity);
      double timeDelta = firstTime - this.times[modIndex];

      if (timeDelta > pastTime) {
        break;
      }
    }
    if (pastTime < 0) {
      numberToGet = 0;
    }
    return this.get(numberToGet);
  }

  @Override
  public Iterator<E> iterator() {
    return this.get();
  }

}
