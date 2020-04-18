package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1.AccelerometerScale;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1.FIFOMode;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1.GyroScale;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1.ODR;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1.Registers;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

public class LSM9DS1Test {
  private MockI2C mockI2C;
  private LSM9DS1 imu;

  @Before
  public void before() {
    mockI2C = new MockI2C();
    imu     = new LSM9DS1(mockI2C);
  }

  @Test
  public void setODRSetsODRInREG1G() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG1_G.getAddress(), (byte) 0);
    imu.setODR(ODR.ODR_119);
    byte ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b01100000, ctrlReg1G);

    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG1_G.getAddress(), (byte) 0xFF);
    imu.setODR(ODR.ODR_952);
    ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b11011111, ctrlReg1G);
  }

  @Test
  public void setAccScaleSetsInCTRLReg6() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG6_XL.getAddress(), (byte) 0);
    imu.setAccelerometerScale(AccelerometerScale.G_2);
    byte ctrlReg6XL = mockI2C.writeMap.get(Registers.CTRL_REG6_XL.getAddress());
    assertEquals((byte) 0b00000000, ctrlReg6XL);

    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG6_XL.getAddress(), (byte) 0xFF);
    imu.setAccelerometerScale(AccelerometerScale.G_4);
    ctrlReg6XL = mockI2C.writeMap.get(Registers.CTRL_REG6_XL.getAddress());
    assertEquals((byte) 0b11110111, ctrlReg6XL);
  }

  @Test
  public void setGyroScaleSetsScaleInREG1G() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG1_G.getAddress(), (byte) 0);
    imu.setGyroscopeScale(GyroScale.DPS_2000);
    byte ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b00011000, ctrlReg1G);

    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG1_G.getAddress(), (byte) 0xFF);
    imu.setGyroscopeScale(GyroScale.DPS_245);
    ctrlReg1G = mockI2C.writeMap.get(Registers.CTRL_REG1_G.getAddress());
    assertEquals((byte) 0b11100111, ctrlReg1G);
  }

  @Test
  public void setFIFOEnabledSetsREG9() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG9.getAddress(), (byte) 0);
    imu.setFIFOEnabled(true);
    byte reg = mockI2C.writeMap.get(Registers.CTRL_REG9.getAddress());
    assertEquals((byte) 0b00000010, reg);

    mockI2C.readMap.put(LSM9DS1.Registers.CTRL_REG9.getAddress(), (byte) 0xFF);
    imu.setFIFOEnabled(false);
    reg = mockI2C.writeMap.get(Registers.CTRL_REG9.getAddress());
    assertEquals((byte) 0b11111101, reg);
  }

  @Test
  public void setFIFOModeSetsModeInFIFOCtrl() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOMode(FIFOMode.CONTINUOUS);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b11000000, reg);

    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_CTRL.getAddress(), (byte) 0xFF);
    imu.setFIFOMode(FIFOMode.CONTINUOUS_THEN_FIFO);
    reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b01111111, reg);
  }

  @Test
  public void setFIFOThresholdSetsThresholdInFIFOCtrl() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOThreshold(5);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b00000101, reg);

    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_CTRL.getAddress(), (byte) 0xFF);
    imu.setFIFOThreshold(21);
    reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) 0b11110101, reg);
  }

  @Test
  public void setFIFOThresholdLimitedToUpper() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOThreshold(LSM9DS1.FIFO_THRESHOLD_MAX + 1);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) LSM9DS1.FIFO_THRESHOLD_MAX, reg);
  }

  @Test
  public void setFIFOThresholdLimitedToLower() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_CTRL.getAddress(), (byte) 0);
    imu.setFIFOThreshold(LSM9DS1.FIFO_THRESHOLD_MIN - 1);
    byte reg = mockI2C.writeMap.get(Registers.FIFO_CTRL.getAddress());
    assertEquals((byte) LSM9DS1.FIFO_THRESHOLD_MIN, reg);
  }

  @Test
  public void hasFIFOOverrunReportsOverrunIfSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 0b01000000);
    assertTrue(imu.hasFIFOOverrun());
  }

  @Test
  public void hasFIFOOverrunReportsNoOverrunIfNotSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 0b00000000);
    assertFalse(imu.hasFIFOOverrun());
  }

  @Test
  public void isFIFOThresholdReachedReportsThresholdReachedIfSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 0b10000000);
    assertTrue(imu.isFIFOThresholdReached());
  }

  @Test
  public void isFIFOThresholdReachedReportsThresholdNotReachedIfNotSet() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 0b00000000);
    assertFalse(imu.isFIFOThresholdReached());
  }

  @Test
  public void samplesInFifoReturnsValInReg() throws IOException {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 0b10010101);
    assertEquals(21, imu.getSamplesInFIFO());
  }

  @Test
  public void pollDoesNotReadIfFifoEmpty() {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 0);
    imu.poll();
  }

  @Test
  public void pollReadsSingleDataAvaliable() {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 1);
    mockI2C.data = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    imu.poll();
    assertTrue(imu.hasNext());
    assertEquals(12, mockI2C.size);
    assertEquals(Registers.OUT_X_L_G.getAddress(), mockI2C.offset);
  }

  @Test
  public void buildReadingParsesDataCorrectly() {
    byte[]     data        = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0 };
    IMUReading reading     = imu.buildReading(data);
    Vector3    acc         = reading.getXYZAcceleration();
    Vector3    expectedAcc = new Vector3(0, -1, 6);
    assertEquals(expectedAcc, acc);
    Vector3 gyro         = reading.getXYZRotation();
    Vector3 expectedGyro = new Vector3(-0x8000, 0x3412, 0x5678);
    assertEquals(expectedGyro, gyro);
  }

  @Test
  public void pollReadsAccAndGyroDataCorrectly() {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 1);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0 };
    imu.poll();
    IMUReading reading     = imu.getNext();
    Vector3    acc         = reading.getXYZAcceleration();
    Vector3    expectedAcc = new Vector3(0, -1, 6);
    assertEquals(expectedAcc, acc);
    Vector3 gyro         = reading.getXYZRotation();
    Vector3 expectedGyro = new Vector3(-0x8000, 0x3412, 0x5678);
    assertEquals(expectedGyro, gyro);
  }

  @Test
  public void pollReadsAccAndGyroDataCorrectlyWhenFewerReturned() {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 2);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0 };
    imu.poll();
    IMUReading reading     = imu.getNext();
    Vector3    acc         = reading.getXYZAcceleration();
    Vector3    expectedAcc = new Vector3(0, -1, 6);
    assertEquals(expectedAcc, acc);
    Vector3 gyro         = reading.getXYZRotation();
    Vector3 expectedGyro = new Vector3(-0x8000, 0x3412, 0x5678);
    assertEquals(expectedGyro, gyro);
  }

  @Test
  public void pollReadsAccAndGyroDataCorrectlyWhenMoreThanOneRetruned() {
    mockI2C.readMap.put(LSM9DS1.Registers.FIFO_SRC.getAddress(), (byte) 2);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56, 0, 0, -1, -1, 6, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 1, 0,
        0 };
    imu.poll();
    IMUReading reading     = imu.getNext();
    Vector3    acc         = reading.getXYZAcceleration();
    Vector3    expectedAcc = new Vector3(0, -1, 6);
    assertEquals(expectedAcc, acc);
    Vector3 gyro         = reading.getXYZRotation();
    Vector3 expectedGyro = new Vector3(-0x8000, 0x3412, 0x5678);
    assertEquals(expectedGyro, gyro);
    assertTrue(imu.hasNext());
    reading     = imu.getNext();
    acc         = reading.getXYZAcceleration();
    expectedAcc = new Vector3(0, 0x100, 0);
    assertEquals(expectedAcc, acc);
    gyro         = reading.getXYZRotation();
    expectedGyro = new Vector3(0x300, 0x100, 0);
    assertEquals(expectedGyro, gyro);
    assertFalse(imu.hasNext());
  }
}
