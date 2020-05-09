package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

import com.pi4j.io.spi.SpiDevice;

public class MockSPI implements SpiDevice {
  
  public byte[] lastWritten;
  public byte[] toReturn;
  
  public HashMap<Integer, byte[]> toReturnMap = new HashMap<>();
  public HashMap<Integer, byte[]> lastWrittenMap = new HashMap<>();

  @Override
  public String write(String data, Charset charset) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String write(String data, String charset) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ByteBuffer write(ByteBuffer data) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public byte[] write(InputStream input) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int write(InputStream input, OutputStream output) throws IOException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public byte[] write(byte[] data, int start, int length) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public byte[] write(byte... data) throws IOException {
    this.lastWritten = data;
    this.lastWrittenMap.put((int) data[0], data);
    if(this.toReturnMap.containsKey((int)data[0])) {
      return this.toReturnMap.get((int)data[0]);
    }
    return this.toReturn;
  }

  @Override
  public short[] write(short[] data, int start, int length) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public short[] write(short... data) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

}
