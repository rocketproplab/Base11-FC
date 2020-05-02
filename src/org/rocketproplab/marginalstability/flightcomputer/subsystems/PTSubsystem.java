package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.hal.AnalogDigitalConverter;

/**
 * A subsystem that converts voltages from pressure transducers to pressure values.
 *
 * @author Clara Chun, Chi Chow
 */
public class PTSubsystem {
  private static PTSubsystem instance;

  /**
   * Singleton instance of PTSubsystem
   *
   * @return a PTSubsystem instance
   */
  public static PTSubsystem getInstance() {
    if (instance == null) {
      instance = new PTSubsystem(null);
    }
    return instance;
  }

  private AnalogDigitalConverter adc;

  /**
   * Create a new PTSubsystem
   *
   * @param adc AnalogDigitalConverter
   */
  public PTSubsystem(AnalogDigitalConverter adc) {
    this.adc = adc;
  }

  /**
   * Obtains pressure value from a ChannelIndex.
   * Converts voltage to pressure value.
   *
   * @param index of Analog to Digital Converter
   * @return pressure value of index
   */
  public double getPTValue(ChannelIndex index) {
    double voltage = adc.get(index.getAdcChannel());
    return getPTValueFromVoltage(index, voltage);
  }

  /**
   * Converts voltage to pressure value.
   *
   * @param index   of Analog to Digital Converter
   * @param voltage voltage from ADC
   * @return pressure value from voltage
   */
  private double getPTValueFromVoltage(ChannelIndex index, double voltage) {
    int adcChannel = index.getAdcChannel();
    return ((Settings.A_PT_CONSTANTS[adcChannel] * voltage * voltage)
            + (Settings.B_PT_CONSTANTS[adcChannel] * voltage) + (Settings.C_PT_CONSTANTS[adcChannel]));
  }

  /**
   * Channels on the Analog-To-Digital Converter
   * https://www.ti.com/lit/ds/symlink/ads7828-q1.pdf?ts=1587840208908
   */
  public enum ChannelIndex {
    CH0(0),
    CH1(1),
    CH2(2),
    CH3(3),
    CH4(4),
    CH5(5),
    CH6(6),
    CH7(7);

    private int adcChannel;

    ChannelIndex(int adcChannel) {
      this.adcChannel = adcChannel;
    }

    public int getAdcChannel() {
      return adcChannel;
    }
  }
}
