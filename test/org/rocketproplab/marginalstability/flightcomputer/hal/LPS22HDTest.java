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
import org.rocketproplab.marginalstability.flightcomputer.comm.TestPacketListener;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HDTest {
  private class MockI2CDevice implements I2CDevice {
    public HashMap<Integer, Integer> readMap = new HashMap<>();
    
    private int address;
    
    public void initValues() {
    	readMap.put(0, 255);
    	readMap.put(1, 2000);
    	readMap.put(2, 1000);
    }
    
    public void changeAddress(int address) {
    	this.address = address;
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
    	return readMap.get(getAddress());
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
  
  @Test
  public void readsOutsideOfBoundsOnStartup() {
    MockI2CDevice i2c = new MockI2CDevice();
    LPS22HD barometer = new LPS22HD(i2c);
    assertFalse(barometer.inUsableRange());
  }
  
  @Test
  public void pollReadsNewBarometricData() {
    MockI2CDevice i2c = new MockI2CDevice();
    LPS22HD barometer = new LPS22HD(i2c);
    assertFalse(barometer.inUsableRange());
  }
  
  @Test
  public void getPressure() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  LPS22HD barometer = new LPS22HD(i2c);
	  assertEquals(0.0, barometer.getPressure(), 0.000005);
  }
  
  @Test
  public void getPressureNonZero() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  LPS22HD barometer = new LPS22HD(i2c);
	  
	  try {
		  i2c.write((byte)5);
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  
	  barometer.poll();
	  assertEquals(5.0, barometer.getPressure(), 0.000005);
  }
  
  @Test
  public void getPressureNonZeroTwo() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  LPS22HD barometer = new LPS22HD(i2c);
	  
	  try {
		  i2c.write(12345, (byte)5);
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  
	  barometer.poll();
	  assertEquals(5.0, barometer.getPressure(), 0.000005);
  }
  
  @Test
  public void getLastMeasurementTime() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  LPS22HD barometer = new LPS22HD(i2c);
	  assertEquals(0.0, barometer.getLastMeasurementTime(), 0.000005);
  }
  
  @Test
  public void getLastMeasurementTimeNonZero() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  LPS22HD barometer = new LPS22HD(i2c);
	  
	  try {
		  i2c.write(12345, (byte)5);
	  } catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
	  
	  barometer.poll();
	  barometer.getLastMeasurementTime();
	  assertEquals(new Time().getSystemTime(), barometer.getLastMeasurementTime(), 0.000005);
  }
  
  @Test
  public void inUsableRangeTests () {
	  MockI2CDevice i2c = new MockI2CDevice();
	  LPS22HD barometer = new LPS22HD(i2c);
	  
	  i2c.initValues();
	  i2c.changeAddress(0);
	  
	  barometer.poll();
	  assertFalse(barometer.inUsableRange());
	  
	  i2c.changeAddress(1);
	  barometer.poll();
	  assertFalse(barometer.inUsableRange());
	  
	  i2c.changeAddress(2);
	  barometer.poll();
	  assertTrue(barometer.inUsableRange());
  }
  //test turning it on
  	//address = 10, and as long byte isn't 000
  	// try using an init method and use it in a sensor interface
  @Test
  public void initSetsCTRL_REG1() {
	  MockI2CDevice i2c = new MockI2CDevice();
	  LPS22HD barometer = new LPS22HD(i2c);
	  barometer.init();
	  byte val = 0b01100000;
	  byte readMapVal = i2c.readMap.get(0xA).byteValue();
	  
	  assertEquals(val, readMapVal);
  }
  
}
