package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;

import org.rocketproplab.marginalstability.flightcomputer.Time;

import com.pi4j.io.i2c.I2CDevice;

/**
 * The LPS22HD HAL implementation. Refer to the datasheet for specification <a
 * href=https://www.st.com/resource/en/datasheet/lps22hd.pdf>https://www.st.com/resource/en/datasheet/lps22hd.pdf</a>
 * 
 * @author Clara Chun, Max Apodaca
 *
 */
public class LPS22HD implements Barometer, PollingSensor {

  private I2CDevice i2cDevice;
  private double    pressure;
  private long      sampleTime;
  private Time      clock;

  private static final byte   ODR_25HZ                      = 0b00110000;
  private static final byte   LOW_PASS_ENABLE               = 0b00001000;
  private static final byte   LOW_PASS_20TH                 = 0b00000100;
  private static final byte   KEEP_REGISTERS_SYCHONISED_BDU = 0b00000010;
  private static final int    CTRL_REG1                     = 0x10;
  private static final double MINIMUM_RANGE                 = 259;
  private static final double MAXIMUM_RANGE                 = 1261;
  private static final double ZERO_TIME                     = 0.0;
  private static final double SCALING_FACTOR                = 4096;
  private static final int    REG_PRESSURE_HIGH             = 0x2A;
  private static final int    REG_PRESSURE_LOW              = 0x29;
  private static final int    REG_PRESSURE_EXTRA_LOW        = 0x28;

  /**
   * Create a new LPS22HD with the given i2cDevice and time. Time will be used to
   * determine the {@link #getLastMeasurementTime()} return value. <br>
   * The i2cDevice parameter is not checked for a valid address.
   * 
   * @param i2cDevice the device which should be used to communicate via i2c
   * @param time      the time to use when reporting measurement time.
   */
  public LPS22HD(I2CDevice i2cDevice, Time time) {
    this.i2cDevice = i2cDevice;
    this.clock     = time;
  }

  /**
   * Set the initial registers to turn on the device and set polling rate to 25Hz
   * with a lowpass cutoff of 1.25 Hz. Also turn on register synchronization to
   * keep the high low and very low pressure registers updating in sync.
   */
  public void init() {
    try {
      i2cDevice.write(CTRL_REG1, (byte) (ODR_25HZ | LOW_PASS_ENABLE | LOW_PASS_20TH | KEEP_REGISTERS_SYCHONISED_BDU));
    } catch (IOException e) {
      // TODO report IO Error
      e.printStackTrace();
    }
  }

  @Override
  public double getPressure() {
    return pressure;
  }

  @Override
  public boolean inUsableRange() {
    if ((pressure > MINIMUM_RANGE) && (pressure < MAXIMUM_RANGE)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public double getLastMeasurementTime() {

    if (clock != null) {
      return sampleTime;
    } else {
      return ZERO_TIME;
    }
  }

  @Override
  public void poll() {
    this.readPressure();
  }

  /**
   * Read the current pressure form the sensor using a one shot read method.
   */
  private void readPressure() {
    // TODO Read at once so we don't read high on sample 1 and low on sample 2. As
    // in if the sample changes while we are reading.
    try {
      byte[] buffer = {0, 0, 0};
      i2cDevice.read(REG_PRESSURE_EXTRA_LOW, buffer, 0, 3);

      // Perform 2's complement if value is negative
      byte sign = 0;
      byte mask = (byte)0b10000000;
      if((buffer[2] & mask) > 0){
        sign = 1;
        buffer[0] = (byte)~buffer[0];
        buffer[1] = (byte)~buffer[1];
        buffer[2] = (byte)~buffer[2];
      }

      int rawPressure = (Byte.toUnsignedInt(buffer[2])<<16) + (Byte.toUnsignedInt(buffer[1])<<8) + Byte.toUnsignedInt(buffer[0]) + sign;
      pressure = rawPressure / (double)SCALING_FACTOR;
    } catch (IOException e) {
      // TODO Report IO Error
      e.printStackTrace();
    }
    sampleTime = (long) clock.getSystemTime();
  }

}
