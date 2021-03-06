package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.hal.SamplableSensor;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

public class StatisticReporterTest {

  private static final double EPSILON = 1E-10;

  private class DummyTelemetry extends Telemetry {

    public ArrayList<Double>        reports;
    public ArrayList<SCMPacketType> reportTypes;

    public DummyTelemetry() {
      super(null, null);
      this.reports     = new ArrayList<>();
      this.reportTypes = new ArrayList<>();
    }

    public void reportTelemetry(SCMPacketType type, double data) {
      this.reports.add(data);
      this.reportTypes.add(type);
    }

  }

  private class SampleSensor implements SamplableSensor<Double> {

    public double  value;
    public boolean newData;
    public double  time;

    @Override
    public boolean hasNewData() {
      return newData;
    }

    @Override
    public Double getNewData() {
      return value;
    }

    @Override
    public double getLastSampleTime() {
      return time;
    }

  }

  private DummyTelemetry telem;

  @Before
  public void before() {
    this.telem = new DummyTelemetry();
  }

  @Test
  public void notSampledSensorDoesNothing() {
    SampleSensor sensor = new SampleSensor();
    new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    assertTrue(this.telem.reports.isEmpty());
    assertTrue(this.telem.reportTypes.isEmpty());
  }

  @Test
  public void sampledSensorStillDoesNotReport() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    reporter.sample();
    assertTrue(this.telem.reports.isEmpty());
    assertTrue(this.telem.reportTypes.isEmpty());
  }

  @Test
  public void reportedWithNoDataReportsNaN() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    reporter.report();
    assertEquals(1, this.telem.reports.size());
    assertEquals(1, this.telem.reportTypes.size());
    assertTrue(Double.isNaN(this.telem.reports.get(0)));
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
  }

  @Test
  public void oneSampleReportsThatValue() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    sensor.newData = true;
    sensor.value   = 5;
    reporter.sample();
    reporter.report();
    assertEquals(1, this.telem.reports.size());
    assertEquals(1, this.telem.reportTypes.size());
    assertEquals(5, this.telem.reports.get(0), EPSILON);
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
  }

  @Test
  public void shouldSampleReportsPassthrough() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    sensor.newData = true;
    sensor.value   = 5;
    assertTrue(reporter.shouldSample());
    sensor.newData = false;
    assertFalse(reporter.shouldSample());
  }

  @Test
  public void defaultWindowSizeIsOne() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    sensor.newData = true;
    sensor.value   = 5;
    reporter.sample();
    sensor.newData = true;
    sensor.value   = 1;
    reporter.sample();
    reporter.report();
    assertEquals(1, this.telem.reports.size());
    assertEquals(1, this.telem.reportTypes.size());
    assertEquals(1, this.telem.reports.get(0), EPSILON);
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
  }

  @Test
  public void changingWindowSizeAllowsForAverage() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    reporter.setWindowSize(5);
    sensor.newData = true;
    sensor.value   = 5;
    reporter.sample();
    sensor.newData = true;
    sensor.value   = 1;
    reporter.sample();
    reporter.report();
    assertEquals(1, this.telem.reports.size());
    assertEquals(1, this.telem.reportTypes.size());
    assertEquals(3, this.telem.reports.get(0), EPSILON);
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
  }

  @Test
  public void settingLookbackTimeOverridesLength() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD);
    reporter.setWindowSize(5);
    reporter.setLookbackTime(1);
    sensor.newData = true;
    sensor.value   = 5;
    sensor.time    = 0;
    reporter.sample();
    sensor.newData = true;
    sensor.value   = 1;
    sensor.time    = 10;
    reporter.sample();
    reporter.report();
    assertEquals(1, this.telem.reports.size());
    assertEquals(1, this.telem.reportTypes.size());
    assertEquals(1, this.telem.reports.get(0), EPSILON);
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
  }

  @Test
  public void reportingTheVarianceWorksWhenEnabled() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD, SCMPacketType.ER);
    reporter.setWindowSize(5);
    sensor.newData = true;
    sensor.value   = 5;
    sensor.time    = 0;
    reporter.sample();
    reporter.report();
    assertEquals(2, this.telem.reports.size());
    assertEquals(2, this.telem.reportTypes.size());
    assertEquals(5, this.telem.reports.get(0), EPSILON);
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
    assertEquals(0, this.telem.reports.get(1), EPSILON);
    assertEquals(SCMPacketType.ER, this.telem.reportTypes.get(1));
    this.telem.reports.clear();
    this.telem.reportTypes.clear();
    sensor.newData = true;
    sensor.value   = 0;
    sensor.time    = 10;
    reporter.sample();
    reporter.report();
    assertEquals(2, this.telem.reports.size());
    assertEquals(2, this.telem.reportTypes.size());
    assertEquals(2.5, this.telem.reports.get(0), EPSILON);
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
    assertEquals(12.5, this.telem.reports.get(1), EPSILON);
    assertEquals(SCMPacketType.ER, this.telem.reportTypes.get(1));
  }

  @Test
  public void lookbackTimeWorksForVariance() {
    SampleSensor      sensor   = new SampleSensor();
    StatisticReporter reporter = new StatisticReporter(sensor, this.telem, SCMPacketType.DD, SCMPacketType.ER);
    reporter.setWindowSize(5);
    reporter.setLookbackTime(1);
    sensor.newData = true;
    sensor.value   = 5;
    sensor.time    = 0;
    reporter.sample();
    sensor.newData = true;
    sensor.value   = 1;
    sensor.time    = 10;
    reporter.sample();
    reporter.report();
    assertEquals(2, this.telem.reports.size());
    assertEquals(2, this.telem.reportTypes.size());
    assertEquals(1, this.telem.reports.get(0), EPSILON);
    assertEquals(SCMPacketType.DD, this.telem.reportTypes.get(0));
    assertEquals(0, this.telem.reports.get(1), EPSILON);
    assertEquals(SCMPacketType.ER, this.telem.reportTypes.get(1));
  }

}
