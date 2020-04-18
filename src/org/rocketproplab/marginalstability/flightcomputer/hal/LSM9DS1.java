package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;

import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

import com.pi4j.io.i2c.I2CDevice;

public class LSM9DS1 implements PollingSensor, IMU {
  private static final int ODR_MASK                    = 0b111;
  private static final int ODR_LSB_POS                 = 5;
  private static final int ACCELEROMETER_SCALE_MASK    = 0b11;
  private static final int ACCELEROMETER_SCALE_LSB_POS = 3;
  private static final int GYRO_SCALE_MASK             = 0b11;
  private static final int GYRO_SCALE_LSB_POS          = 3;
  private static final int FIFO_EN_VAL_MASK            = 0b1;
  private static final int FIFO_EN_LSB_POS             = 1;
  private static final int FIFO_MODE_MASK              = 0b111;
  private static final int FIFO_MODE_LSB_POS           = 5;
  private static final int FIFO_THRESHOLD_MASK         = 0b11111;
  private static final int FIFO_THRESHOLD_LSB_POS      = 0;
  public static final int  FIFO_THRESHOLD_MAX          = 31;
  public static final int  FIFO_THRESHOLD_MIN          = 0;
  public static final int  FIFO_OVERRUN_POS            = 6;
  public static final int  FIFO_THRESHOLD_STATUS_POS   = 7;
  public static final int  FIFO_SAMPLES_STORED_MASK    = 0b111111;

  private static final int BYTES_PER_FIFO_LINE = 12;
  private static final int BITS_PER_BYTE       = 8;

  public enum Registers {
    ACT_THS(0x04),
    ACT_DUR(0x05),
    INT_GEN_CFG_XL(0x06),
    INT_GEN_THS_X_XL(0x07),
    INT_GEN_THS_Y_XL(0x08),
    INT_GEN_THS_Z_XL(0x09),
    INT_GEN_DUR_XL(0x0A),
    REFERENCE_G(0x0B),
    INT1_CTRL(0x0C),
    INT2_CTRL(0x0D),
    WHO_AM_I(0x0F),
    CTRL_REG1_G(0x10),
    CTRL_REG2_G(0x11),
    CTRL_REG3_G(0x12),
    ORIENT_CFG_G(0x13),
    INT_GEN_SRC_G(0x14),
    OUT_TEMP_L(0x15),
    OUT_TEMP_H(0x16),
    STATUS_REG(0x17),
    OUT_X_L_G(0x18),
    OUT_X_H_G(0x19),
    OUT_Y_L_G(0x1A),
    OUT_Y_H_G(0x1B),
    OUT_Z_L_G(0x1C),
    OUT_Z_H_G(0x1D),
    CTRL_REG4(0x1E),
    CTRL_REG5_XL(0x1F),
    CTRL_REG6_XL(0x20),
    CTRL_REG7_XL(0x21),
    CTRL_REG8(0x22),
    CTRL_REG9(0x23),
    CTRL_REG10(0x24),
    INT_GEN_SRC_XL(0x26),
    STATUS_REG_2(0x27),
    OUT_X_L_XL(0x28),
    OUT_X_H_XL(0x29),
    OUT_Y_L_XL(0x2A),
    OUT_Y_H_XL(0x2B),
    OUT_Z_L_XL(0x2C),
    OUT_Z_H_XL(0x2D),
    FIFO_CTRL(0x2E),
    FIFO_SRC(0x2F),
    INT_GEN_CFG_G(0x30),
    INT_GEN_THS_XH_G(0x31),
    INT_GEN_THS_XL_G(0x32),
    INT_GEN_THS_YH_G(0x33),
    INT_GEN_THS_YL_G(0x34),
    INT_GEN_THS_ZH_G(0x35),
    INT_GEN_THS_ZL_G(0x36),
    INT_GEN_DUR_G(0x37);

    final int address;

    Registers(int address) {
      this.address = address;
    }

    public int getAddress() {
      return this.address;
    }
  }

  public interface RegisterValue {
    public int getValueMask();

    public int getValueLSBPos();

    public int ordinal();
  }

  public enum ODR implements RegisterValue {
    ODR_OFF,
    ODR_14_9,
    ODR_59_5,
    ODR_119,
    ODR_238,
    ODR_476,
    ODR_952;

    @Override
    public int getValueMask() {
      return ODR_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return ODR_LSB_POS;
    }

  }

  public enum AccelerometerScale implements RegisterValue {
    G_2,
    G_16,
    G_4,
    G_8;

    @Override
    public int getValueMask() {
      return ACCELEROMETER_SCALE_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return ACCELEROMETER_SCALE_LSB_POS;
    }
  }

  public enum GyroScale implements RegisterValue {
    DPS_245,
    DPS_500,
    DPS_NA,
    DPS_2000;

    @Override
    public int getValueMask() {
      return GYRO_SCALE_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return GYRO_SCALE_LSB_POS;
    }
  }

  public enum FIFOMode implements RegisterValue {
    BYPASS,
    FIFO,
    NA,
    CONTINUOUS_THEN_FIFO,
    BYPASS_THEN_CONTINUOUS,
    NA_2,
    CONTINUOUS;

    @Override
    public int getValueMask() {
      return FIFO_MODE_MASK;
    }

    @Override
    public int getValueLSBPos() {
      return FIFO_MODE_LSB_POS;
    }
  }

  public enum FIFOThreshold {
    FIFO_THRESHOLD_MASK
  }

  public enum FIFOStatus {

  }

  public enum Status {
    TEMP_AVALIABLE
  }

  private I2CDevice              i2c;
  private ArrayDeque<IMUReading> samples = new ArrayDeque<>();

  public LSM9DS1(I2CDevice device) {
    this.i2c = device;
  }

  /**
   * Sets the output data rate of the sensor
   * 
   * @throws IOException if unable to read
   */
  public void setODR(ODR odr) throws IOException {
    genericRegisterWrite(Registers.CTRL_REG1_G, odr);
  }

  /**
   * Sets the scale of the accelerometer
   * 
   * @throws IOException
   */
  public void setAccelerometerScale(AccelerometerScale scale) throws IOException {
    genericRegisterWrite(Registers.CTRL_REG6_XL, scale);
  }

  /**
   * Sets the scale of the Gyroscope
   * 
   * @throws IOException
   */
  public void setGyroscopeScale(GyroScale scale) throws IOException {
    genericRegisterWrite(Registers.CTRL_REG1_G, scale);
  }

  public void setFIFOEnabled(boolean enabled) throws IOException {
    int registerValue = this.i2c.read(Registers.CTRL_REG9.getAddress());
    int result        = mask(registerValue, enabled ? 1 : 0, FIFO_EN_LSB_POS, FIFO_EN_VAL_MASK);
    this.i2c.write(Registers.CTRL_REG9.getAddress(), (byte) result);
  }

  public void setFIFOMode(FIFOMode mode) throws IOException {
    genericRegisterWrite(Registers.FIFO_CTRL, mode);
  }

  public void setFIFOThreshold(int threshold) throws IOException {
    if (threshold > FIFO_THRESHOLD_MAX) {
      threshold = FIFO_THRESHOLD_MAX;
    }
    if (threshold < FIFO_THRESHOLD_MIN) {
      threshold = FIFO_THRESHOLD_MIN;
    }
    int registerValue = this.i2c.read(Registers.FIFO_CTRL.getAddress());
    int result        = mask(registerValue, threshold, FIFO_THRESHOLD_LSB_POS, FIFO_THRESHOLD_MASK);
    this.i2c.write(Registers.FIFO_CTRL.getAddress(), (byte) result);
  }

  public boolean hasFIFOOverrun() throws IOException {
    int fifoSRCValue = this.i2c.read(Registers.FIFO_SRC.getAddress());
    int masked       = (1 << FIFO_OVERRUN_POS) & fifoSRCValue;
    return masked != 0;
  }

  public boolean isFIFOThresholdReached() throws IOException {
    int fifoSRCValue = this.i2c.read(Registers.FIFO_SRC.getAddress());
    int masked       = (1 << FIFO_THRESHOLD_STATUS_POS) & fifoSRCValue;
    return masked != 0;
  }

  public int getSamplesInFIFO() throws IOException {
    int fifoSRCValue = this.i2c.read(Registers.FIFO_SRC.getAddress());
    int masked       = FIFO_SAMPLES_STORED_MASK & fifoSRCValue;
    return masked;
  }

  private void genericRegisterWrite(Registers register, RegisterValue value) throws IOException {
    int registerValue = this.i2c.read(register.getAddress());
    int result        = mask(registerValue, value.ordinal(), value.getValueLSBPos(), value.getValueMask());
    this.i2c.write(register.getAddress(), (byte) result);
  }

  private int mask(int toMask, int newData, int lsbPos, int valueMask) {
    int mask    = valueMask << lsbPos;
    int notMask = ~mask;
    int result  = newData << lsbPos | (toMask & notMask);
    return result;
  }

  @Override
  public void poll() {
    try {
      int samplesInFIFO = this.getSamplesInFIFO();
      if (samplesInFIFO == 0) {
        return;
      }
      int    dataLength  = samplesInFIFO * BYTES_PER_FIFO_LINE;
      byte[] data        = new byte[dataLength];
      int    samplesRead = this.i2c.read(data, Registers.OUT_X_L_G.getAddress(), dataLength);
      this.parseReadings(data, samplesRead);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void parseReadings(byte[] data, int bytesRead) {
    int samplesRead = bytesRead / BYTES_PER_FIFO_LINE;
    for (int i = 0; i < samplesRead; i++) {
      int        start   = i * BYTES_PER_FIFO_LINE;
      int        end     = (i + 1) * BYTES_PER_FIFO_LINE;
      byte[]     samples = Arrays.copyOfRange(data, start, end);
      IMUReading reading = this.buildReading(samples);
      this.samples.add(reading);
    }
  }

  public IMUReading buildReading(byte[] data) {
    int[] results = this.getData(data);

    int xGyro = results[0];
    int yGyro = results[1];
    int zGyro = results[2];
    int xAcc  = results[3];
    int yAcc  = results[4];
    int zAcc  = results[5];

    Vector3 gyroVec = new Vector3(xGyro, yGyro, zGyro);
    Vector3 accVec  = new Vector3(xAcc, yAcc, zAcc);
    return new IMUReading(accVec, gyroVec);
  }

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
  public IMUReading getNext() {
    return samples.pollFirst();
  }

  @Override
  public boolean hasNext() {
    return !samples.isEmpty();
  }

}
