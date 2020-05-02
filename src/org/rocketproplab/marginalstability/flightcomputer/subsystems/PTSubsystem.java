package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.hal.AnalogDigitalConverter;

/*
 *
 *
 *
 *
 * @author Clara Chun
 *
 */
public class PTSubsystem {
  private static PTSubsystem instance;

  public static PTSubsystem getInstance() {
    if (instance == null) {
      instance = new PTSubsystem(null);
    }
    return instance;
  }

  private AnalogDigitalConverter adc;

  public PTSubsystem(AnalogDigitalConverter adc) {
    this.adc = adc;
  }

  public double getPTValue(ChannelIndex index) {
    double voltage = adc.get(index.getAdcChannel());
    return getPTValueFromVoltage(index, voltage);
  }

  private double getPTValueFromVoltage(ChannelIndex index, double voltage) {
    int adcChannel = index.getAdcChannel();
    return ((Settings.A_PT_CONSTANTS[adcChannel] * voltage * voltage)
            + (Settings.B_PT_CONSTANTS[adcChannel] * voltage) + (Settings.C_PT_CONSTANTS[adcChannel]));
  }

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
