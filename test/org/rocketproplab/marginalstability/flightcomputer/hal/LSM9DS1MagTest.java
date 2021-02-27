package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1Mag.MODE;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1Mag.ODR;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1Mag.PERFORMANCE_XY;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1Mag.PERFORMANCE_Z;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1Mag.Registers;
import org.rocketproplab.marginalstability.flightcomputer.hal.LSM9DS1Mag.SCALE;
import org.rocketproplab.marginalstability.flightcomputer.math.Vector3;

public class LSM9DS1MagTest {
  private MockI2C mockI2C;
  private LSM9DS1Mag sensor;

  @Before
  public void before() {
    mockI2C = new MockI2C();
    sensor  = new LSM9DS1Mag(mockI2C);
  }
  
  @Test
  public void setODRSetsODRInREG1M() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG1_M.getAddress(), (byte) 0);
    sensor.setODR(ODR.ODR_5);
    byte ctrlReg1M = mockI2C.writeMap.get(Registers.CTRL_REG1_M.getAddress());
    assertEquals((byte) 0b00001100, ctrlReg1M);
    
    mockI2C.readMap.put(Registers.CTRL_REG1_M.getAddress(), (byte) 0xFF);
    sensor.setODR(ODR.ODR_40);
    ctrlReg1M = mockI2C.writeMap.get(Registers.CTRL_REG1_M.getAddress());
    assertEquals((byte) 0b11111011, ctrlReg1M);
  }
  
  @Test
  public void setScaleSetsScaleInREG2M() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG2_M.getAddress(), (byte) 0);
    sensor.setScale(SCALE.GAUSS_8);
    byte ctrlReg2M = mockI2C.writeMap.get(Registers.CTRL_REG2_M.getAddress());
    assertEquals((byte) 0b00100000, ctrlReg2M);
    
    mockI2C.readMap.put(Registers.CTRL_REG2_M.getAddress(), (byte) 0xFF);
    sensor.setScale(SCALE.GAUSS_12);
    ctrlReg2M = mockI2C.writeMap.get(Registers.CTRL_REG2_M.getAddress());
    assertEquals((byte) 0b11011111, ctrlReg2M);
  }
  
  @Test
  public void getScaleReturnsCorrectScale() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG2_M.getAddress(), (byte) 0);
    assertEquals(sensor.getScale(), SCALE.GAUSS_4);
    
    sensor.setScale(SCALE.GAUSS_8);
    assertEquals(sensor.getScale(), SCALE.GAUSS_8);
    assertNotEquals(sensor.getScale(), SCALE.GAUSS_4);
    
    sensor.setScale(SCALE.GAUSS_12);
    assertEquals(sensor.getScale(), SCALE.GAUSS_12);
    
    sensor.setScale(SCALE.GAUSS_16);
    assertEquals(sensor.getScale(), SCALE.GAUSS_16);
  }
  
  @Test
  public void setModeSetsModeInREG3M() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG3_M.getAddress(), (byte) 0);
    sensor.setMode(MODE.SINGLE_CONVERSION);
    byte ctrlReg3M = mockI2C.writeMap.get(Registers.CTRL_REG3_M.getAddress());
    assertEquals((byte) 0b00000001, ctrlReg3M);
    
    mockI2C.readMap.put(Registers.CTRL_REG3_M.getAddress(), (byte) 0xFF);
    sensor.setMode(MODE.POWER_DOWN);
    ctrlReg3M = mockI2C.writeMap.get(Registers.CTRL_REG3_M.getAddress());
    assertEquals((byte) 0b11111110, ctrlReg3M);
  }
  
  @Test
  public void setXYPerformanceSetsXYPerformanceInREG1M() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG1_M.getAddress(), (byte) 0);
    sensor.setXYPerformance(PERFORMANCE_XY.MEDIUM);
    byte ctrlReg1M = mockI2C.writeMap.get(Registers.CTRL_REG1_M.getAddress());
    assertEquals((byte) 0b00100000, ctrlReg1M);
    
    mockI2C.readMap.put(Registers.CTRL_REG1_M.getAddress(), (byte) 0xFF);
    sensor.setXYPerformance(PERFORMANCE_XY.HIGH);
    ctrlReg1M = mockI2C.writeMap.get(Registers.CTRL_REG1_M.getAddress());
    assertEquals((byte) 0b11011111, ctrlReg1M);
  }
  
  @Test
  public void setZPerformanceSetsZPerformanceInREG1M() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG4_M.getAddress(), (byte) 0);
    sensor.setZPerformance(PERFORMANCE_Z.MEDIUM);
    byte ctrlReg4M = mockI2C.writeMap.get(Registers.CTRL_REG4_M.getAddress());
    assertEquals((byte) 0b00000100, ctrlReg4M);
    
    mockI2C.readMap.put(Registers.CTRL_REG4_M.getAddress(), (byte) 0xFF);
    sensor.setZPerformance(PERFORMANCE_Z.HIGH);
    ctrlReg4M = mockI2C.writeMap.get(Registers.CTRL_REG4_M.getAddress());
    assertEquals((byte) 0b11111011, ctrlReg4M);
  }
  
  @Test
  public void setTempCompensateEnablesTempCompensateInREG1M() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG1_M.getAddress(), (byte) 0);
    sensor.setTemperatureCompensationEnabled(true);
    byte ctrlReg2M = mockI2C.writeMap.get(Registers.CTRL_REG1_M.getAddress());
    assertEquals((byte) 0b10000000, ctrlReg2M);
    
    mockI2C.readMap.put(Registers.CTRL_REG1_M.getAddress(), (byte) 0xFF);
    sensor.setTemperatureCompensationEnabled(false);
    ctrlReg2M = mockI2C.writeMap.get(Registers.CTRL_REG1_M.getAddress());
    assertEquals((byte) 0b01111111, ctrlReg2M);
  }
  
  @Test
  public void setBlockDataUpdateEnablesBDUInREG1M() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG5_M.getAddress(), (byte) 0);
    sensor.setBlockDataUpdateUntilAllReadEnabled(true);
    byte ctrlReg5M = mockI2C.writeMap.get(Registers.CTRL_REG5_M.getAddress());
    assertEquals((byte) 0b01000000, ctrlReg5M);
    
    mockI2C.readMap.put(Registers.CTRL_REG5_M.getAddress(), (byte) 0xFF);
    sensor.setBlockDataUpdateUntilAllReadEnabled(false);
    ctrlReg5M = mockI2C.writeMap.get(Registers.CTRL_REG5_M.getAddress());
    assertEquals((byte) 0b10111111, ctrlReg5M);
  }
  
  @Test
  public void isNewXYZDataAvailableReportsAvailableIfSet() throws IOException {
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 0b00001000);
    assertTrue(sensor.isNewXYZDataAvailable());
  }
  
  @Test
  public void isNewXYZDataAvailableReportsNotAvailableIfNoSet() throws IOException {
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 0b00000000);
    assertFalse(sensor.isNewXYZDataAvailable());
  }
  
  @Test
  public void pollDoesNotReadIfQueueEmpty() {
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 0);
    sensor.poll();
  }
  
  @Test
  public void pollReadsSingleDataAvaliable() {
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 00001111);
    mockI2C.data = new byte[] { 0, 0, 0, 0, 0, 0 };
    sensor.poll();
    assertTrue(sensor.hasNext());
    assertEquals(6, mockI2C.size);
    assertEquals(Registers.OUT_X_L_M.getAddress(), mockI2C.address);
  }
  
  @Test
  public void buildReadingParsesDataCorrectly() {
    byte[]     data        = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56 };
    MagReading reading     = sensor.buildReading(data);
    Vector3 mag            = reading.getMagneticField();
    int expectedMagReadingX = -0x8000, expectedMagReadingY = 0x3412, expectedMagReadingZ = 0x5678;
    assertEquals(sensor.computeMagneticField(expectedMagReadingX, expectedMagReadingY, expectedMagReadingZ), mag);
  }
  
  @Test
  public void pollReadsMagDataCorrectly() {
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 00001111);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56 };
    assertFalse(sensor.hasNext());
    sensor.poll();
    assertTrue(sensor.hasNext());
    MagReading reading     = sensor.getNext();
    Vector3    mag         = reading.getMagneticField();
    int expectedMagReadingX = -0x8000, expectedMagReadingY = 0x3412, expectedMagReadingZ = 0x5678;
    assertEquals(sensor.computeMagneticField(expectedMagReadingX, expectedMagReadingY, expectedMagReadingZ), mag);
  }
  
  @Test
  public void queueHoldsMoreThanOneMagDataCorrectly() {
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 00001111);
    mockI2C.data = new byte[] { 0, -0x80, 0x12, 0x34, 0x78, 0x56 };
    sensor.poll();
    
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 0);
    sensor.poll();
    
    mockI2C.readMap.put(Registers.STATUS_REG_M.getAddress(), (byte) 00001111);
    mockI2C.data = new byte[] { 0x13, 0x35, 0x71, 0x54, 0x01, 0x13 };
    sensor.poll();
    
    assertTrue(sensor.hasNext());
    MagReading reading     = sensor.getNext();
    Vector3    mag         = reading.getMagneticField();
    int expectedMagReadingX = -0x8000, expectedMagReadingY = 0x3412, expectedMagReadingZ = 0x5678;
    assertEquals(sensor.computeMagneticField(expectedMagReadingX, expectedMagReadingY, expectedMagReadingZ), mag);
    
    assertTrue(sensor.hasNext());
    reading     = sensor.getNext();
    mag         = reading.getMagneticField();
    expectedMagReadingX = 0x3513; expectedMagReadingY = 0x5471; expectedMagReadingZ = 0x1301;
    assertEquals(sensor.computeMagneticField(expectedMagReadingX, expectedMagReadingY, expectedMagReadingZ), mag);
    
    assertFalse(sensor.hasNext());
  }
  
  @Test
  public void computeMagneticFieldReturnsCorrectResult() throws IOException {
    mockI2C.readMap.put(Registers.CTRL_REG2_M.getAddress(), (byte) 0);
    int magReadingX = 4547, magReadingY = 18548, magReadingZ = 87;
    assertEquals(sensor.computeMagneticField(magReadingX, magReadingY, magReadingZ),
                 new Vector3(magReadingX * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_4GAUSS,
                     magReadingY * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_4GAUSS,
                     magReadingZ * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_4GAUSS));
    
    sensor.setScale(SCALE.GAUSS_16);
    assertEquals(sensor.computeMagneticField(magReadingX, magReadingY, magReadingZ),
        new Vector3(magReadingX * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_16GAUSS,
            magReadingY * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_16GAUSS,
            magReadingZ * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_16GAUSS));
    
    magReadingX = 32767; magReadingY = 32767; magReadingZ = 32767;
    assertEquals(sensor.computeMagneticField(magReadingX, magReadingY, magReadingZ),
        new Vector3(magReadingX * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_16GAUSS,
            magReadingY * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_16GAUSS,
            magReadingZ * Settings.LSM9DS1_SENSITIVITY_MAGNETOMETER_16GAUSS));
  }

}
