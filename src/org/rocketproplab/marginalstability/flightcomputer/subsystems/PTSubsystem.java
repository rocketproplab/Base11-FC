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
    ONE(0),
    TWO(1),
    THREE(2),
    FOUR(3),
    FIVE(4),
    SIX(5),
    SEVEN(6),
    EIGHT(7);

    private int adcChannel;

    ChannelIndex(int adcChannel) {
      this.adcChannel = adcChannel;
    }

    public int getAdcChannel() {
      return adcChannel;
    }
  }
}
