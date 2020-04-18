package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import com.pi4j.io.i2c.I2CDevice;

public class MockI2C implements I2CDevice {
  public HashMap<Integer, Byte> readMap  = new HashMap<>();
  public HashMap<Integer, Byte> writeMap = new HashMap<>();
  public byte[] data;
  public int offset;
  public int size;

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
    this.writeMap.put(address, b);
  }

  @Override
  public void write(int address, byte[] buffer, int offset, int size) throws IOException {

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
    this.size = size;
    this.offset = offset;
    for(int i = 0; i<data.length; i++) {
      buffer[i] = data[i];
    }
    return data.length;
  }

  @Override
  public int read(int address) throws IOException {
    return readMap.get(address);
  }

  @Override
  public int read(int address, byte[] buffer, int offset, int size) throws IOException {
    return 0;
  }

  @Override
  public void ioctl(long command, int value) throws IOException {

  }

  @Override
  public void ioctl(long command, ByteBuffer data, IntBuffer offsets) throws IOException {

  }

  @Override
  public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize)
      throws IOException {
    return 0;
  }
}
