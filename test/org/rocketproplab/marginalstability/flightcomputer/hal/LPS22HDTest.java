package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.DummyPacketListener;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HDTest {
  private class MockI2CDevice implements I2CDevice {
    public HashMap<Integer, Byte> readMap = new HashMap<>();
    
    private int address;
    
    public void initValuesOne() {
    	readMap.put(0x2A, (byte)0b0);
    	readMap.put(0x29, (byte)0b10);
    	readMap.put(0x28, (byte)0b100010);
    }
    
    public void initValuesTwo() {
    	readMap.put(0x2A, (byte)0b01111111);
    	readMap.put(0x29, (byte)0b11111111);
    	readMap.put(0x28, (byte)0b11111111);
    }
    
    public void initValuesThree() {
    	readMap.put(0x2A, (byte)0b111111);
    	readMap.put(0x29, (byte)0b11111111);
    	readMap.put(0x28, (byte)0b11111111);
    }
    
    
    @Override
    public int getAddress() {
      return address;
    }

    @Override
    public void write(byte b) throws IOException {
    	address = 0;
    	readMap.put(0, b);
    	
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
    	readMap.put(address, b);
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
      for(int i = 0; i < size; i++){
        buffer[i+offset] = readMap.get(address+i);
      }
      return size;
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
  
  public class BarometerTime extends Time {
	  public double getSystemTime() {
		  return 105;
	  }
  }
  
  @Test
  public void getPressureLowerBound() {
    MockI2CDevice i2c = new MockI2CDevice();
    BarometerTime time = new BarometerTime();
    LPS22HD barometer = new LPS22HD(i2c, time);
      
    i2c.readMap.put(0x2A, (byte) 0b10000);
    i2c.readMap.put(0x29, (byte) 0b01000000);
    i2c.readMap.put(0x28, (byte) 0);
    barometer.poll();
    assertEquals(260, barometer.getPressure(), 0.0005);
  } 
  
  @Test
  public void getPressureUpperBound() {
    MockI2CDevice i2c = new MockI2CDevice();
    BarometerTime time = new BarometerTime();
    LPS22HD barometer = new LPS22HD(i2c, time);
      
    i2c.readMap.put(0x2A, (byte) 0b1001110);
    i2c.readMap.put(0x29, (byte) 0b11000000);
    i2c.readMap.put(0x28, (byte) 0);
    barometer.poll();
    assertEquals(1260, barometer.getPressure(), 0.0005);
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
	  
	  i2c.initValuesThree();
	  barometer.poll();
	  assertEquals(1024, barometer.getPressure(), 0.0005);
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
	  
	  i2c.initValuesOne();
	  barometer.poll();
	  barometer.getLastMeasurementTime();
	  assertEquals(105, barometer.getLastMeasurementTime(), 0.000000001);
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
	  byte val = 0b00111110;
	  byte readMapVal = i2c.readMap.get(0x10).byteValue();
	  
	  assertEquals(val, readMapVal);
  }
  
}
