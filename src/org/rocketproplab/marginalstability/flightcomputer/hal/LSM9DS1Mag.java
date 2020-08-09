package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;
import java.util.ArrayDeque;

import org.rocketproplab.marginalstability.flightcomputer.ErrorReporter;
import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

import com.pi4j.io.i2c.I2CDevice;

/**
 * The HAL implementation for the LSM9DS1 magnetometer sensor. Datasheet can be
 * found here: <a href="https://www.st.com/resource/en/datasheet/lsm9ds1.pdf">https://www.st.com/resource/en/datasheet/lsm9ds1.pdf</a><br>
 * 
 * Every time poll is called, this sensor will acquire 1 sample from the LSM9DS1
 * magnetometer registers. Each sample gets put into a queue which can be accessed
 * with the {@link #getNext()} method. Whether or not the queue is empty can be
 * read using the {@link #hasNext()} method. It is recommended to call
 * {@link #hasNext()} before each call of {@link #getNext()}.
 * 
 * @author Enlil Odisho
 *
 */
public class LSM9DS1Mag implements PollingSensor, IMU {
  private static final int ODR_MASK                     = 0b111;
  private static final int ODR_LSB_POS                  = 2;
  private static final int SCALE_MASK                   = 0b11;
  private static final int SCALE_LSB_POS                = 5;
  private static final int PERFORMANCE_MASK             = 0b11;
  private static final int PERFORMANCE_XY_LSB_POS       = 5;
  private static final int PERFORMANCE_Z_LSB_POS        = 2;
  private static final int TEMP_COMPENSATE_MASK         = 0b1;
  private static final int TEMP_COMPENSATE_POS          = 7;
  private static final int BLOCK_DATA_UPDATE_MASK       = 0b1;
  private static final int BLOCK_DATA_UPDATE_POS        = 6;
  private static final int NEW_XYZ_DATA_AVAILABLE_POS   = 3;
  
  private static final int BYTES_PER_SAMPLE = 6;
  private static final int BITS_PER_BYTE    = 8;

  /**
   * All the registers that can be found in the LSM9DS1 imu for the magnetometer
   * sensor. This is taken directly from the datasheet.
   */
  public enum Registers {
    OFFSET_X_REG_L_M(0x05),
    OFFSET_X_REG_H_M(0x06),
    OFFSET_Y_REG_L_M(0x07),
    OFFSET_Y_REG_H_M(0x08),
    OFFSET_Z_REG_L_M(0x09),
    OFFSET_Z_REG_H_M(0x0A),
    WHO_AM_I_M(0x0F),
    CTRL_REG1_M(0x20),
    CTRL_REG2_M(0x21),
    CTRL_REG3_M(0x22),
    CTRL_REG4_M(0x23),
    CTRL_REG5_M(0x24),
    STATUS_REG_M(0x27),
    OUT_X_L_M(0x28),
    OUT_X_H_M(0x29),
    OUT_Y_L_M(0x2A),
    OUT_Y_H_M(0x2B),
    OUT_Z_L_M(0x2C),
    OUT_Z_H_M(0x2D),
    INT_CFG_M(0x30),
    INT_SRC_M(0x31),
    INT_THS_L(0x32),
    INT_THS_H(0x33);
    
    final int address;
    
    Registers(int address) {
      this.address = address;
    }
    
    /**
     * Read the address of a register, this is not necessarily the same as the
     * ordinal and should be used for I2C access.
     * 
     * @return the I2C address of the register.
     */
    public int getAddress() {
      return this.address;
    }
  };
  
  public enum ODR implements RegisterValue {
    ODR_0_625,
    ODR_1_25,
    ODR_2_5,
    ODR_5,
    ODR_10,
    ODR_20,
    ODR_40,
    ODR_80;

    @Override
    public int getValueMask() {
      return ODR_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return ODR_LSB_POS;
    }
    
  }
  
  public enum SCALE implements RegisterValue {
    GAUSS_4,
    GAUSS_8,
    GAUSS_12,
    GAUSS_16;

    @Override
    public int getValueMask() {
      return SCALE_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return SCALE_LSB_POS;
    }
  }
  
  public enum PERFORMANCE_XY implements RegisterValue {
    LOW,
    MEDIUM,
    HIGH,
    ULTRA;
    
    @Override
    public int getValueMask() {
      return PERFORMANCE_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return PERFORMANCE_XY_LSB_POS;
    }
  }
  
  public enum PERFORMANCE_Z implements RegisterValue {
    LOW,
    MEDIUM,
    HIGH,
    ULTRA;
    
    @Override
    public int getValueMask() {
      return PERFORMANCE_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return PERFORMANCE_Z_LSB_POS;
    }
  }
  
  private I2CDevice i2c;
  private ArrayDeque<MagReading> samples = new ArrayDeque<>();
  
  /**
   * Create a new LSM9DS1Mag on the given {@link I2CDevice}. There is no
   * validation for the {@link I2CDevice} address.
   * 
   * @param device the device to use for the I2C communication
   */
  public LSM9DS1Mag(I2CDevice device) {
    this.i2c = device;
  }
  
  /**
   * Sets the output data rate of the sensor
   * 
   * @param odr the rate to set sensor to
   * @throws IOException if we are unable to access the i2c device
   */
  public void setODR(ODR odr) throws IOException {
    genericRegisterWrite(Registers.CTRL_REG1_M, odr);
  }
  
  /**
   * Sets the scale of the sensor
   * 
   * @param scale the scale of the magnetometer data
   * @throws IOException if we are unable to access the i2c device
   */
  public void setScale(SCALE scale) throws IOException {
    genericRegisterWrite(Registers.CTRL_REG2_M, scale);
  }
  
  /**
   * Sets the performance of the sensor for x and y magnetometer data
   * 
   * @param performance the performance of the magnetometer sensor for xy data
   * @throws IOException if we are unable to access the i2c device
   */
  public void setXYPerformance(PERFORMANCE_XY performance) throws IOException {
    genericRegisterWrite(Registers.CTRL_REG1_M, performance);
  }
  
  /**
   * Sets the performance of the sensor for z magnetometer data
   * 
   * @param performance the performance of the magnetometer sensor for z data
   * @throws IOException if we are unable to access the i2c device
   */
  public void setZPerformance(PERFORMANCE_Z performance) throws IOException {
    genericRegisterWrite(Registers.CTRL_REG4_M, performance);
  }
  
  /**
   * Enable or disable temperature compensation for magnetometer sensor
   * 
   * @param enabled whether or not to enable temperature compensation
   * @throws IOException if we are unable to access the i2c device
   */
  public void setTemperatureCompensationEnabled(boolean enabled) throws IOException {
    int registerValue = this.i2c.read(Registers.CTRL_REG1_M.getAddress());
    int result        = mask(registerValue, enabled ? 1 : 0, TEMP_COMPENSATE_POS, TEMP_COMPENSATE_MASK);
    this.i2c.write(Registers.CTRL_REG1_M.getAddress(), (byte) result);
  }
  
  /**
   * Whether or not to block the magnetic data from updating until all LSB and
   * MSB are read for the current sample.
   * 
   * @param enabled whether or not to enable BDU feature
   * @throws IOException if we are unable to access the i2c device
   */
  public void setBlockDataUpdateUntilAllReadEnabled(boolean enabled) throws IOException {
    int registerValue = this.i2c.read(Registers.CTRL_REG5_M.getAddress());
    int result        = mask(registerValue, enabled ? 1 : 0, BLOCK_DATA_UPDATE_POS, BLOCK_DATA_UPDATE_MASK);
    this.i2c.write(Registers.CTRL_REG5_M.getAddress(), (byte) result);
  }
  
  /**
   * @return whether new x/y/z data is available or not.
   * @throws IOException if we are unable to access the i2c device
   */
  public boolean isNewXYZDataAvailable() throws IOException {
    int statusRegValue = this.i2c.read(Registers.STATUS_REG_M.getAddress());
    int masked         = (1 << NEW_XYZ_DATA_AVAILABLE_POS) & statusRegValue;
    return masked != 0;
  }
  
  /**
   * Write a register value to a register.
   * 
   * @param register the register to write to
   * @param value    the value to write, uses all of the values in
   *                 {@link RegisterValue}
   * @throws IOException if we are unable to access the i2c device
   */
  private void genericRegisterWrite(Registers register, RegisterValue value) throws IOException {
    int registerValue = this.i2c.read(register.getAddress());
    int result        = mask(registerValue, value.ordinal(), value.getValueLSBPos(), value.getValueMask());
    this.i2c.write(register.getAddress(), (byte) result);
  }
  
  /**
   * Masks the value in toMask with the given parameters. newData is the data to
   * replace the masked bits. LSBPos is how many bits to the left newData is in
   * toMask. valueMask is the mask for the value in newData. valueMask should be
   * right aligned. valueMask will be shifted lsbPos bits to the left before
   * anding with toMask.<br>
   * <br>
   * <b>Note</b>: No values in newData are masked out.
   * 
   * @param toMask    the value to mask
   * @param newData   the value to replace the masked areas of to mask
   * @param lsbPos    how far to the left the masked value is in toMask
   * @param valueMask the mask to apply to toMask but right aligned.
   * @return combination of toMask and newData combined based on valueMask
   */
  private int mask(int toMask, int newData, int lsbPos, int valueMask) {
    int mask    = valueMask << lsbPos;
    int notMask = ~mask;
    int result  = newData << lsbPos | (toMask & notMask);
    return result;
  }
  
  @Override
  public void poll() {
    this.readFromSensor();
  }
  
  /**
   * Read one sample from the sensor. This method will read the sample by
   * reading BYTES_PER_SAMPLE number of bytes from the register OUT_X_L_M.
   */
  private void readFromSensor() {
    try {
      if (!isNewXYZDataAvailable()) {
        return;
      }
      int dataLength = BYTES_PER_SAMPLE;
      byte[] data         = new byte[dataLength];
      int bytesRead       = this.i2c.read(Registers.OUT_X_L_M.getAddress(), data, 0, dataLength);
      if (bytesRead != BYTES_PER_SAMPLE) {
        ErrorReporter errorReporter = ErrorReporter.getInstance();
        String errorMsg = "Incorrect number of bytes for magnetometer read from IMU.";
        errorReporter.reportError(Errors.IMU_IO_ERROR, errorMsg);
        return;
      }
      MagReading reading  = this.buildReading(data);
      this.samples.add(reading);
    } catch(IOException e) {
      ErrorReporter errorReporter = ErrorReporter.getInstance();
      String errorMsg = "Unable to read from IMU IO Exception";
      errorReporter.reportError(Errors.IMU_IO_ERROR, e, errorMsg);
    }
  }
  
  /**
   * Parse a set of BYTES_PER_SAMPLE bytes into an MagReading.
   * TODO Normalzie to gauss
   * 
   * @param data the set of six bytes representing a reading
   * @return the MagReading which the six bytes belong to
   */
  public MagReading buildReading(byte[] data) {
    int[] results = this.getData(data);
    
    int xMag = results[0];
    int yMag = results[1];
    int zMag = results[2];
    
    Vector3 magVec = new Vector3(xMag, yMag, zMag);
    return new MagReading(magVec);
  }
  
  /**
   * Converts the input bytes to shorts where it assumes that the first byte of
   * the tuple is less significant. <br>
   * The array looks like {@code [L,H,L,H, ..., L, H]}.
   * 
   * @param data byte array of little-endian shorts
   * @return array of the shorts
   */
  private int[] getData(byte[] data) {
    int[] results = new int[data.length / 2];
    for (int i = 0; i < data.length / 2; i++) {
      short low    = (short) (char) data[i * 2];
      short high   = (short) (char) data[i * 2 + 1];
      short result = (short) (low | (high << BITS_PER_BYTE));
      results[i] = result;
    }
    return results;
  }
  
  @Override
  public MagReading getNext() {
    return samples.pollFirst();
  }

  @Override
  public boolean hasNext() {
    return !samples.isEmpty();
  }

}
