package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.hal.AnalogDigitalConverter;

import static org.junit.Assert.*;

public class TestPTSubsystem {

  private static final double EPSILON = 0.0000001;
  private static final double[] TEST_VOLTAGES = {
          3.243100861992423,
          4.96298205308679,
          3.4319099852820822,
          3.6941158775736316,
          1.0388381644118727,
          1.6895078811799191,
          0.9166823110491484,
          2.3365812211691708};
  private static final double[] TEST_PRESSURES = {
          3.243100861992423,
          4.96298205308679,
          3.4319099852820822,
          3.6941158775736316,
          1.0388381644118727,
          1.6895078811799191,
          0.9166823110491484,
          2.3365812211691708};
  private static final AnalogDigitalConverter TEST_ADC = index -> TEST_VOLTAGES[index];

  @Test(expected = NullPointerException.class)
  public void nullADCInDefaultInstance() {
    PTSubsystem ptSubsystem = new PTSubsystem(null);
    ptSubsystem.getPTValue(PTSubsystem.ChannelIndex.CH0);
  }

  @Test
  public void voltageToPTValueConversion() {
    PTSubsystem ptSubsystem = new PTSubsystem(TEST_ADC);
    for (PTSubsystem.ChannelIndex channelIndex : PTSubsystem.ChannelIndex.values()) {
      double ptValue  = ptSubsystem.getPTValue(channelIndex);
      double expected = TEST_PRESSURES[channelIndex.getAdcChannel()];
      assertEquals(expected, ptValue, EPSILON);
    }
  }
}