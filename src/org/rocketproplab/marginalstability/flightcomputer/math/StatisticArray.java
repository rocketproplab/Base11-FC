package org.rocketproplab.marginalstability.flightcomputer.math;

import java.util.ArrayList;

/**
 * Array to place statistics into that returns only statistical values.
 * 
 * @author Max Apodaca
 *
 */
public class StatisticArray {

  public interface StatisticValidator {
    boolean isValid(StatisticArray array);
  }

  private ArrayList<Double> samples;

  /**
   * Create a new empty array
   */
  public StatisticArray(int maxCount) {
    this.samples = new ArrayList<>();
  }

  /**
   * Adds a sample to the array
   * 
   * @param sample the sample to add
   */
  public void addSample(double sample) {
    this.samples.add(sample);
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
  
  public double getMean(double previousSeconds) {
    return 0;
  }


  public double getMean(int lastN) {
    if (this.samples.isEmpty()) {
      return 0;
    }
    double sum = 0;
    for (double sample : this.samples) {
      sum += sample;
    }
    return sum / this.samples.size();
  }
  
  /**
   * Returns the variance of the inserted samples. Variance is given by
   * 
   * <pre>
   *       Sum (x_i - x_mean)^2
   * S^2 = --------------------
   *              n - 1
   * </pre>
   * 
   * For fewer than two samples 0 is returned.
   * 
   * @return the variance of the samples
   */
  public double getVariance() {
    int samples = this.getNumberOfSamples();
    return this.getVariance(samples);
  }
  
  public double getVariance(double previousSeconds) {
    return 0;
  }

  public double getVariance(int lastN) {
    if (this.samples.size() < 2) {
      return 0;
    }
    double mean       = this.getMean(lastN);
    double sumSquared = 0;
    for (double sample : this.samples) {
      sumSquared += Math.pow(sample - mean, 2);
    }
    double variance = sumSquared / (this.samples.size() - 1);
    return variance;
  }

  public int getNumberOfSamples() {
    return 0;
  }

  public boolean isValid() {
    return false;
  }

  public void setValidator(StatisticValidator validator) {

  }
}
