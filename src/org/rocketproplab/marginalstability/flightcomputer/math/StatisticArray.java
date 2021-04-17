package org.rocketproplab.marginalstability.flightcomputer.math;

/**
 * Array to place statistics into that returns only statistical values. It
 * provides support for validators to determine if the contained data is valid.
 *
 * @author Max Apodaca
 */
public class StatisticArray {

  /**
   * An interface to validate statistic arrays
   */
  public interface StatisticValidator {
    /**
     * Returns if the statistic array is valid
     *
     * @param array the current state of the array
     * @return true if the array is valid
     */
    boolean isValid(StatisticArray array);
  }

  private TimedRingBuffer<Double> samples;
  private StatisticValidator validator;

  /**
   * Create a new empty array
   */
  public StatisticArray(int maxCount) {
    this.samples = new TimedRingBuffer<>(maxCount);
    this.validator = array -> true;
  }

  /**
   * Adds a sample to the array
   *
   * @param sample the sample to add
   * @param time   the time when the sample was taken
   */
  public void addSample(double sample, double time) {
    this.samples.add(sample, time);
  }

  /**
   * Returns the mean of the previous samples. For zero samples zero is returned.
   *
   * @return the mean of the previous samples
   */
  public double getMean() {
    int samples = this.getNumberOfSamples();
    return this.getMean(samples);
  }

  /**
   * Returns the mean for the samples taken in the previous seconds
   *
   * @param previousSeconds how many seconds to look back
   * @return the mean of the samples
   */
  public double getMean(double previousSeconds) {
    return this.getMean(this.samples.get(previousSeconds));
  }

  /**
   * Returns the mean of the last N samples
   *
   * @param lastN how many samples to look at
   * @return the mean of the samples, 0 if lastN is negative
   */
  public double getMean(int lastN) {
    if (lastN < 1) {
      return 0;
    }
    return this.getMean(this.samples.get(lastN));
  }

  /**
   * implementation of the mean algorithm used by both {@link #getMean(int)} and
   * {@link #getMean(double)}.
   *
   * @param iterator the iterator to grab the samples from
   * @return the mean of the samples returned by the iterator
   */
  private double getMean(TimedRingBuffer<Double>.RingBufferIterator iterator) {
    double sum = 0;
    int sampleCount = 0;
    for (double sample : iterator) {
      sum += sample;
      sampleCount++;
    }
    if (sampleCount == 0) {
      return 0;
    }
    return sum / sampleCount;
  }

  /**
   * Returns the variance of the inserted samples. Variance is given by
   *
   * <pre>
   *       Sum (x_i - x_mean)^2
   * S^2 = --------------------
   *              n - 1
   * </pre>
   * <p>
   * For fewer than two samples 0 is returned.
   *
   * @return the variance of the samples
   */
  public double getVariance() {
    int samples = this.getNumberOfSamples();
    return this.getVariance(samples);
  }

  /**
   * Returns the variance for samples taken within the last previousSeconds. See
   * {@link #getVariance()} for details.
   *
   * @param previousSeconds the time to look back in
   * @return the variance of the samples taken in the previous seconds
   */
  public double getVariance(double previousSeconds) {
    double mean = this.getMean(previousSeconds);
    TimedRingBuffer<Double>.RingBufferIterator iterator = this.samples.get(previousSeconds);
    return this.getVariance(mean, iterator);
  }

  /**
   * Returns the variance for the last n samples. See {@link #getVariance()} for
   * details.
   *
   * @param lastN how many samples to look at
   * @return the variance of the previous n samples
   */
  public double getVariance(int lastN) {
    if (this.samples.size() < 2 || lastN < 2) {
      return 0;
    }
    double mean = this.getMean(lastN);
    return this.getVariance(mean, this.samples.get(lastN));
  }

  /**
   * Inner implementation constant to all three types of variance calculation
   *
   * @param mean     the mean of the sample set
   * @param iterator the iterator to get the samples
   * @return the variance of the samples
   */
  private double getVariance(double mean, TimedRingBuffer<Double>.RingBufferIterator iterator) {
    double sumSquared = 0;
    int samplesCounted = 0;
    for (double sample : iterator) {
      sumSquared += Math.pow(sample - mean, 2);
      samplesCounted++;
    }
    if (samplesCounted < 2) {
      return 0;
    }
    double variance = sumSquared / (samplesCounted - 1);
    return variance;
  }

  /**
   * Get the number of samples which are contained in this array
   *
   * @return the number of samples in this array
   */
  public int getNumberOfSamples() {
    return this.samples.size();
  }

  /**
   * Returns if the array is valid. The default validator always returns true.
   *
   * @return if the array is valid
   */
  public boolean isValid() {
    return this.validator.isValid(this);
  }

  /**
   * Sets the validator to use for data validation. Must not be null.
   *
   * @param validator the new validator to use
   */
  public void setValidator(StatisticValidator validator) {
    this.validator = validator;
  }
}
