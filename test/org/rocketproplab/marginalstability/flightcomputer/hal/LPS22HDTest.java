package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.TestPacketListener;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HDTest {
  private class MockI2CDevice implements I2CDevice {
    public HashMap<Integer, Integer> readMap = new HashMap<>();
    
    private int address;
    
    public void initValuesOne() {
    	readMap.put(0x2A, 255);
    	readMap.put(0x29, 2000);
    	readMap.put(0x28, 1000);
    }
    
    public void initValuesTwo() {
    	readMap.put(0x2A, 10000000);
    	readMap.put(0x29, 10000000);
    	readMap.put(0x28, 10000000);
    }
    
    public void initValuesThree() {
    	readMap.put(0x2A, 0);
    	readMap.put(0x29, 0);
    	readMap.put(0x28, 2000000);
    }
    
    
    @Override
    public int getAddress() {
      return address;
    }

    @Override
    public void write(byte b) throws IOException {
    	address = 0;
    	String value = "" + b;
    	readMap.put(0, Integer.parseInt(value));
    	
    }

    @Override
    public void write(byte[] buffer, int offset, int size) throws IOException {
      
    }

    @Override
    public void write(byte[] buffer) throws IOException {
    }

    @Override
    public void write(int address, byte b) throws IOException {
    	this.address = address;
    	readMap.put(address, (int)b);
    }

    @Override
    public void write(int address, byte[] buffer, int offset, int size)
        throws IOException {
      
    }

    @Override
    public void write(int address, byte[] buffer) throws IOException {
      
    }

    @Override
    public int read() throws IOException {
    	return readMap.get(0);
    }

    @Override
    public int read(byte[] buffer, int offset, int size) throws IOException {
      return 0;
    }

    @Override
    public int read(int address) throws IOException {
    	return readMap.get(address);
    }

    @Override
    public int read(int address, byte[] buffer, int offset, int size)
        throws IOException {
      return 0;
    }

    @Override
    public void ioctl(long command, int value) throws IOException {
      
    }

    @Override
    public void ioctl(long command, ByteBuffer data, IntBuffer offsets)
        throws IOException {
      
    }

    @Override
    public int read(byte[] writeBuffer, int writeOffset, int writeSize,
        byte[] readBuffer, int readOffset, int readSize) throws IOException {
      return 0;
    }
    
  }
  
  private class BarometerTime extends Time {
  }
  
  @Test
  public void readsOutsideOfBoundsOnStartup() {
    MockI2CDevice i2c = new MockI2CDevice();
    BarometerTime time = new BarometerTime();
    LPS22HD barometer = new LPS22HD(i2c, time);
    assertFalse(barometer.inUsableRange());
  }
  
  @Test
  public void pollReadsNewBarometricData() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  BarometerTime time = new BarometerTime();
	  LPS22HD barometer = new LPS22HD(i2c, time);
	  assertFalse(barometer.inUsableRange());
  }
  
  @Test
  public void getPressure() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  BarometerTime time = new BarometerTime();
	  LPS22HD barometer = new LPS22HD(i2c, time);
	  assertEquals(0.0, barometer.getPressure(), 0.000005);
  }
  
  @Test
  public void getPressureNonZero() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  BarometerTime time = new BarometerTime();
	  LPS22HD barometer = new LPS22HD(i2c, time);
	  
	  i2c.initValuesOne();
	  barometer.poll();
	  assertNotEquals(0.0, barometer.getPressure(), 0.000005);
  }
  
  @Test
  public void getLastMeasurementTime() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  BarometerTime time = null;
	  LPS22HD barometer = new LPS22HD(i2c, time);
	  assertEquals(0.0, barometer.getLastMeasurementTime(), 0.000000001);
  }
  
  @Test
  public void getLastMeasurementTimeNonZero() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  BarometerTime time = new BarometerTime();
	  LPS22HD barometer = new LPS22HD(i2c, time);
	  
	  barometer.getLastMeasurementTime();
	  assertEquals(new Time().getSystemTime(), barometer.getLastMeasurementTime(), 0.000000001);
  }
  
  @Test
  public void inUsableRangeTests () {
	  MockI2CDevice i2c = new MockI2CDevice();
	  BarometerTime time = new BarometerTime();
	  LPS22HD barometer = new LPS22HD(i2c, time);
	  
	  i2c.initValuesOne();
	  barometer.poll();
	  assertFalse(barometer.inUsableRange());
	  
	  i2c.initValuesTwo();
	  barometer.poll();
	  assertFalse(barometer.inUsableRange());
	  
	  i2c.initValuesThree();
	  barometer.poll();
	  assertTrue(barometer.inUsableRange());
  }

  @Test
  public void initSetsCTRL_REG1() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  BarometerTime time = new BarometerTime();
	  LPS22HD barometer = new LPS22HD(i2c, time);
	  barometer.init();
	  byte val = 0b01100000;
	  byte readMapVal = i2c.readMap.get(0x10).byteValue();
	  
	  assertEquals(val, readMapVal);
  }
  
}
