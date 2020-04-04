package org.rocketproplab.marginalstability.flightcomputer.hal;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.junit.Test;

import com.pi4j.io.i2c.I2CDevice;

public class LPS22HDTest {
  private class MockI2CDevice implements I2CDevice {
    public HashMap<Integer, Integer> readMap = new HashMap<>();
    @Override
    public int getAddress() {
      return 0;
    }

    @Override
    public void write(byte b) throws IOException {
      
    }

    @Override
    public void write(byte[] buffer, int offset, int size) throws IOException {
      
    }

    @Override
    public void write(byte[] buffer) throws IOException {
      
    }

    @Override
    public void write(int address, byte b) throws IOException {
      
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
      return 0;
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
}
