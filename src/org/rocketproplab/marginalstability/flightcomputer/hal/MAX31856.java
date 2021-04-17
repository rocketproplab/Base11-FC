package org.rocketproplab.marginalstability.flightcomputer.hal;

import com.pi4j.io.spi.SpiDevice;
import org.rocketproplab.marginalstability.flightcomputer.Time;

import java.io.IOException;

/**
 * Implements SPI protocol for the MAX31856, contains code to read and quantify
 * linearized thermocouple temperature data.
 *
 * @author Rudy Thurston
 */
public class MAX31856 implements Thermocouple, PollingSensor {

  private SpiDevice spi;
  private double temp;
  private long sampleTime;
  private Time clock;

  private static final byte REG_TC_TEMP = 0x0C;
  private static final double ZERO_TIME = 0.0;
  private static final double SCALING_FACTOR = 128.0;
  private static final double MINIMUM_RANGE = -200.0;
  private static final double MAXIMUM_RANGE = 1372.0;
  private static final double TEMP_ERROR = 2048.0;

  /**
   * Create instance of MAX31856 with given SpiDevice. Time will be used to
   * determine the {@link #getLastMeasurementTime()} return value.
   *
   * @param spi  SpiDevice communicating with MAX31856, no validation check
   * @param time the time to use when reporting measurement time.
   */
  public MAX31856(SpiDevice spi, Time time) {
    this.spi = spi;
    this.clock = time;
    this.temp = TEMP_ERROR;
  }

  @Override
  public double getTemperature() {
    return temp;
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
  public boolean inUsableRange() {
    if ((temp >= MINIMUM_RANGE) && (temp <= MAXIMUM_RANGE)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void poll() {
    try {
      this.readTemp();
    } catch (IOException e) {
      // TODO Report error
      e.printStackTrace();
    }
    sampleTime = (long) clock.getSystemTime();
  }

  /**
   * Read the linearized thermocouple temperature from the sensor using a
   * multi-byte read. Read/Write register memory map,
   * https://datasheets.maximintegrated.com/en/ds/MAX31856.pdf
   * <p>
   * 0x0C (readData[1]) : | Sign | 2^10 | 2^9 | 2^8 | 2^7 | 2^6 | 2^5 | 2^4 |
   * 0x0D (readData[2]) : | 2^3  | 2^2  | 2^1 | 2^0 | 2^-1| 2^-2| 2^-3| 2^-4|
   * 0x0E (readData[3]) : | 2^-5 | 2^-6 | 2^-7|  X  |  X  |  X  |  X  |  X  |
   *
   * @throws IOException
   */
  private void readTemp() throws IOException {
    byte[] data = {REG_TC_TEMP, 0, 0, 0};
    byte[] readData = this.spi.write(data);
    if (readData.length < 4) {
      return;
    }

    // Perform 2's complement if rawData is negative
    byte sign = 0;
    byte mask = (byte) 0b10000000;
    if ((readData[1] & mask) != 0) {
      sign = 1;
      readData[1] = (byte) ~readData[1];
      readData[2] = (byte) ~readData[2];
      readData[3] = (byte) ~readData[3];
    }

    // Store 24 bit temperature read into integer placeholder
    int rawData = (Byte.toUnsignedInt(readData[1]) << 16) +
            (Byte.toUnsignedInt(readData[2]) << 8) +
            Byte.toUnsignedInt(readData[3]) + sign;

    // Returns magnitude of temperature read with correct sign
    temp = tempCalc(rawData) * Math.pow(-1, sign);
  }

  /**
   * Calculates magnitude of temperature from raw thermocouple read.
   * <p>
   * data : |  X   |  X   |  X  |  X  |  X  |  X  |  X  |  X  |
   * |  0   | 2^10 | 2^9 | 2^8 | 2^7 | 2^6 | 2^5 | 2^4 |
   * | 2^3  | 2^2  | 2^1 | 2^0 | 2^-1| 2^-2| 2^-3| 2^-4|
   * | 2^-5 | 2^-6 | 2^-7|  X  |  X  |  X  |  X  |  X  |
   *
   * @param data the 24 bit thermocouple read
   * @return Magnitude of linearized temperature
   */
  private double tempCalc(int data) {
    data = data >>> 5;
    return data / SCALING_FACTOR;
  }

}
