package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1AccelGyro.AccelerometerScale;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1AccelGyro.FIFOMode;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1AccelGyro.GyroScale;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1AccelGyro.ODR;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1AccelGyro.Registers;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

public class LSM9DS1AccelGyroTest {
  private MockI2C mockI2C;
  private LSM9DS1AccelGyro imu;

  @Before
  public void before() {
    mockI2C = new MockI2C();
    imu     = new LSM9DS1AccelGyro(mockI2C);
  }

  @Test
  public void setODRSetsODRInREG1G() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG1_G.getAddress(), (byte) 0);
    imu.setODR(ODR.ODR_119);
    byte ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b01100000, ctrlReg1G);

    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG1_G.getAddress(), (byte) 0xFF);
    imu.setODR(ODR.ODR_952);
    ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b11011111, ctrlReg1G);
  }

  @Test
  public void setAccScaleSetsInCTRLReg6() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG6_XL.getAddress(), (byte) 0);
    imu.setAccelerometerScale(AccelerometerScale.G_2);
    byte ctrlReg6XL = mockI2C.writeMap.get(Registers.CTRL_REG6_XL.getAddress());
    assertEquals((byte) 0b00000000, ctrlReg6XL);

    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG6_XL.getAddress(), (byte) 0xFF);
    imu.setAccelerometerScale(AccelerometerScale.G_4);
    ctrlReg6XL = mockI2C.writeMap.get(Registers.CTRL_REG6_XL.getAddress());
    assertEquals((byte) 0b11110111, ctrlReg6XL);
  }
  
  @Test
  public void getAccScaleReturnsCorrectScale() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG6_XL.getAddress(), (byte) 0);
    assertEquals(imu.getAccelerometerScale(), AccelerometerScale.G_2);
    
    imu.setAccelerometerScale(AccelerometerScale.G_4);
    assertEquals(imu.getAccelerometerScale(), AccelerometerScale.G_4);
    assertNotEquals(imu.getAccelerometerScale(), AccelerometerScale.G_2);
    
    imu.setAccelerometerScale(AccelerometerScale.G_8);
    assertEquals(imu.getAccelerometerScale(), AccelerometerScale.G_8);
    
    imu.setAccelerometerScale(AccelerometerScale.G_16);
    assertEquals(imu.getAccelerometerScale(), AccelerometerScale.G_16);
  }

  @Test
  public void setGyroScaleSetsScaleInREG1G() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG1_G.getAddress(), (byte) 0);
    imu.setGyroscopeScale(GyroScale.DPS_2000);
    byte ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b00011000, ctrlReg1G);

    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG1_G.getAddress(), (byte) 0xFF);
    imu.setGyroscopeScale(GyroScale.DPS_245);
    ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b11100111, ctrlReg1G);
  }
  
  @Test
  public void getGyroScaleReturnsCorrectScale() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG1_G.getAddress(), (byte) 0);
    assertEquals(imu.getGyroscopeScale(), GyroScale.DPS_245);
    
    imu.setGyroscopeScale(GyroScale.DPS_500);
    assertEquals(imu.getGyroscopeScale(), GyroScale.DPS_500);
    assertNotEquals(imu.getGyroscopeScale(), GyroScale.DPS_245);
    
    imu.setGyroscopeScale(GyroScale.DPS_2000);
    assertEquals(imu.getGyroscopeScale(), GyroScale.DPS_2000);
  }

  @Test
  public void setFIFOEnabledSetsREG9() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG9.getAddress(), (byte) 0);
    imu.setFIFOEnabled(true);
    byte reg = mockI2C.writeMap.get(Registers.CTRL_REG9.getAddress());
    assertEquals((byte) 0b00000010, reg);

    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG9.getAddress(), (byte) 0xFF);
    imu.setFIFOEnabled(false);
    reg = mockI2C.writeMap.get(Registers.CTRL_REG9.getAddress());
    assertEquals((byte) 0b11111101, reg);
  }

  @Test
  public void setFIFOModeSetsModeInFIFOCtrl() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOMode(FIFOMode.CONTINUOUS);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b11000000, reg);

    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_CTRL.getAddress(), (byte) 0xFF);
    imu.setFIFOMode(FIFOMode.CONTINUOUS_THEN_FIFO);
    reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b01111111, reg);
  }

  @Test
  public void setFIFOThresholdSetsThresholdInFIFOCtrl() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOThreshold(5);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b00000101, reg);

    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_CTRL.getAddress(), (byte) 0xFF);
    imu.setFIFOThreshold(21);
    reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b11110101, reg);
  }

  @Test
  public void setFIFOThresholdLimitedToUpper() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOThreshold(LSM9DS1AccelGyro.FIFO_THRESHOLD_MAX + 1);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) LSM9DS1AccelGyro.FIFO_THRESHOLD_MAX, reg);
  }

  @Test
  public void setFIFOThresholdLimitedToLower() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOThreshold(LSM9DS1AccelGyro.FIFO_THRESHOLD_MIN - 1);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) LSM9DS1AccelGyro.FIFO_THRESHOLD_MIN, reg);
  }

  @Test
  public void hasFIFOOverrunReportsOverrunIfSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 0b01000000);
    assertTrue(imu.hasFIFOOverrun());
  }

  @Test
  public void hasFIFOOverrunReportsNoOverrunIfNotSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 0b00000000);
    assertFalse(imu.hasFIFOOverrun());
  }

  @Test
  public void isFIFOThresholdReachedReportsThresholdReachedIfSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 0b10000000);
    assertTrue(imu.isFIFOThresholdReached());
  }

  @Test
  public void isFIFOThresholdReachedReportsThresholdNotReachedIfNotSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 0b00000000);
    assertFalse(imu.isFIFOThresholdReached());
  }

  @Test
  public void samplesInFifoReturnsValInReg() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 0b10010101);
    assertEquals(21, imu.getSamplesInFIFO());
  }

  @Test
  public void pollDoesNotReadIfFifoEmpty() {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 0);
    imu.poll();
  }

  @Test
  public void pollReadsSingleDataAvaliable() {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 1);
    mockI2C.data = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    imu.poll();
    assertTrue(imu.hasNext());
    assertEquals(12, mockI2C.size);
    assertEquals(Registers.OUT_X_L_G.getAddress(), mockI2C.address);
  }

  @Test
  public void buildReadingParsesDataCorrectly() {
    byte[]     data        = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0 };
    AccelGyroReading reading     = imu.buildReading(data);
    Vector3    acc         = reading.getXYZAcceleration();
    int expectedAccReadingX = 0, expectedAccReadingY = -1, expectedAccReadingZ = 6;
    assertEquals(imu.computeAcceleration(expectedAccReadingX, expectedAccReadingY, expectedAccReadingZ), acc);
    Vector3 gyro         = reading.getXYZRotation();
    int expectedGyroReadingX = -0x8000, expectedGyroReadingY = 0x3412, expectedGyroReadingZ = 0x5678;
    assertEquals(imu.computeAngularAcceleration(expectedGyroReadingX, expectedGyroReadingY, expectedGyroReadingZ), gyro);
  }

  @Test
  public void pollReadsAccAndGyroDataCorrectly() {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 1);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0 };
    imu.poll();
    AccelGyroReading reading     = imu.getNext();
    Vector3    acc         = reading.getXYZAcceleration();
    int expectedAccReadingX = 0, expectedAccReadingY = -1, expectedAccReadingZ = 6;
    assertEquals(imu.computeAcceleration(expectedAccReadingX, expectedAccReadingY, expectedAccReadingZ), acc);
    Vector3 gyro         = reading.getXYZRotation();
    int expectedGyroReadingX = -0x8000, expectedGyroReadingY = 0x3412, expectedGyroReadingZ = 0x5678;
    assertEquals(imu.computeAngularAcceleration(expectedGyroReadingX, expectedGyroReadingY, expectedGyroReadingZ), gyro);
  }

  @Test
  public void pollReadsAccAndGyroDataCorrectlyWhenFewerReturned() {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 2);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0 };
    imu.poll();
    AccelGyroReading reading     = imu.getNext();
    Vector3    acc         = reading.getXYZAcceleration();
    int expectedAccReadingX = 0, expectedAccReadingY = -1, expectedAccReadingZ = 6;
    assertEquals(imu.computeAcceleration(expectedAccReadingX, expectedAccReadingY, expectedAccReadingZ), acc);
    Vector3 gyro         = reading.getXYZRotation();
    int expectedGyroReadingX = -0x8000, expectedGyroReadingY = 0x3412, expectedGyroReadingZ = 0x5678;
    assertEquals(imu.computeAngularAcceleration(expectedGyroReadingX, expectedGyroReadingY, expectedGyroReadingZ), gyro);
  }

  @Test
  public void pollReadsAccAndGyroDataCorrectlyWhenMoreThanOneRetruned() {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.FIFO_SRC.getAddress(), (byte) 2);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 1, 0,
        0 };
    imu.poll();
    AccelGyroReading reading     = imu.getNext();
    Vector3    acc         = reading.getXYZAcceleration();
    int expectedAccReadingX = 0, expectedAccReadingY = -1, expectedAccReadingZ = 6;
    assertEquals(imu.computeAcceleration(expectedAccReadingX, expectedAccReadingY, expectedAccReadingZ), acc);
    Vector3 gyro         = reading.getXYZRotation();
    int expectedGyroReadingX = -0x8000, expectedGyroReadingY = 0x3412, expectedGyroReadingZ = 0x5678;
    assertEquals(imu.computeAngularAcceleration(expectedGyroReadingX, expectedGyroReadingY, expectedGyroReadingZ), gyro);
    
    assertTrue(imu.hasNext());
    reading     = imu.getNext();
    acc         = reading.getXYZAcceleration();
    expectedAccReadingX = 0; expectedAccReadingY = 0x100; expectedAccReadingZ = 0;
    assertEquals(imu.computeAcceleration(expectedAccReadingX, expectedAccReadingY, expectedAccReadingZ), acc);
    gyro         = reading.getXYZRotation();
    expectedGyroReadingX = 0x300; expectedGyroReadingY = 0x100; expectedGyroReadingZ = 0;
    assertEquals(imu.computeAngularAcceleration(expectedGyroReadingX, expectedGyroReadingY, expectedGyroReadingZ), gyro);

    assertFalse(imu.hasNext());
  }
  
  @Test
  public void computeAccelerationReturnsCorrectResult() throws IOException {
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG6_XL.getAddress(), (byte) 0);
    int accReadingX = 53, accReadingY = 28135, accReadingZ = 18343;
    assertEquals(imu.computeAcceleration(accReadingX, accReadingY, accReadingZ),
                 new Vector3(accReadingX * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_2G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED,
                     accReadingY * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_2G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED,
                     accReadingZ * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_2G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED));
    
    imu.setAccelerometerScale(AccelerometerScale.G_16);
    assertEquals(imu.computeAcceleration(accReadingX, accReadingY, accReadingZ),
        new Vector3(accReadingX * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_16G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED,
            accReadingY * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_16G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED,
            accReadingZ * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_16G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED));
    
    accReadingX = 32767; accReadingY = 32767; accReadingZ = 32767;
    assertEquals(imu.computeAcceleration(accReadingX, accReadingY, accReadingZ),
        new Vector3(accReadingX * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_16G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED,
            accReadingY * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_16G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED,
            accReadingZ * Settings.LSM9DS1_SENSITIVITY_ACCELEROMETER_16G * LSM9DS1AccelGyro.ACCELEROMETER_OUTPUT_TO_MPS_SQUARED));
  }
  
  @Test
  public void computeAngularAccelerationReturnsCorrectResult() throws IOException {
    final double ONE_DEGREE_IN_RADIANS  = Math.PI / 180.0;
    
    mockI2C.readMap.put(LSM9DS1AccelGyro.Registers.CTRL_REG1_G.getAddress(), (byte) 0);
    int gyroReadingX = 53, gyroReadingY = 28135, gyroReadingZ = 18343;
    assertEquals(imu.computeAngularAcceleration(gyroReadingX, gyroReadingY, gyroReadingZ),
                 new Vector3(gyroReadingX * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_245DPS * ONE_DEGREE_IN_RADIANS,
                     gyroReadingY * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_245DPS * ONE_DEGREE_IN_RADIANS,
                     gyroReadingZ * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_245DPS * ONE_DEGREE_IN_RADIANS));
    
    imu.setGyroscopeScale(GyroScale.DPS_2000);
    assertEquals(imu.computeAngularAcceleration(gyroReadingX, gyroReadingY, gyroReadingZ),
        new Vector3(gyroReadingX * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_2000DPS * ONE_DEGREE_IN_RADIANS,
            gyroReadingY * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_2000DPS * ONE_DEGREE_IN_RADIANS,
            gyroReadingZ * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_2000DPS * ONE_DEGREE_IN_RADIANS));
    
    gyroReadingX = 32767; gyroReadingY = 32767; gyroReadingZ = 32767;
    assertEquals(imu.computeAngularAcceleration(gyroReadingX, gyroReadingY, gyroReadingZ),
        new Vector3(gyroReadingX * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_2000DPS * ONE_DEGREE_IN_RADIANS,
            gyroReadingY * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_2000DPS * ONE_DEGREE_IN_RADIANS,
            gyroReadingZ * Settings.LSM9DS1_SENSITIVITY_GYROSCOPE_2000DPS * ONE_DEGREE_IN_RADIANS));
  }
}
