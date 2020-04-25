package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;

import org.rocketproplab.marginalstability.flightcomputer.events.SerialListener;

import com.pi4j.io.spi.SpiDevice;

public class MAX14830 implements PollingSensor {
  public enum Port {
    UART0,
    UART1,
    UART2,
    UART3;
  }

  public enum Registers {
    // FIFO DATA
    RHR(0x00),
    THR(0x00),
    // INTERRUPTS
    IRQEn(0x01),
    ISR(0x02),
    LSRIntEn(0x03),
    LSR(0x04),
    SpclChrIntEn(0x05),
    SpclCharInt(0x06),
    STSIntEn(0x07),
    STSInt(0x08),
    // UART MODES
    MODE1(0x09),
    MODE2(0x0A),
    LCR(0x0B),
    RxTimeOut(0x0C),
    HDplxDelay(0x0D),
    IrDA(0x0E),
    // FIFOs CONTROL
    FlowLvl(0x0F),
    FIFOTrgLvl(0x10),
    TxFIFOLvl(0x11),
    RxFIFOLvl(0x12),
    // FLOW CONTROL
    FlowCtrl(0x13),
    XON1(0x14),
    XON2(0x15),
    XOFF1(0x16),
    XOFF2(0x17), 
    // GPIOs
    GPIOConfg(0x18),
    GPIOData(0x19),
    // CLOCK CONFIGURATION
    PLLConfig(0x1A),
    BRGConfig(0x1B),
    DIVLSB(0x1C),
    DIVMSB(0x1D),
    CLKSource(0x1E),
    // GLOBAL REGISTERS
    GlobalRQ(0x1F),
    GloblComnd(0x1F),
    // SYNCHRONIZATION REGISTERS
    TxSynch(0x20),
    SynchDelay1(0x21),
    SynchDelay2(0x22),
    // TIMER REGISTERS
    TIMER1(0x23),
    TIMER2(0x24), 
    // REVISION
    REVID(0x25);

    byte idx;

    Registers(int idx) {
      this.idx = (byte) idx;
    }

    public byte address() {
      return this.idx;
    }
  }

  private static final int UART_SELECT_LSB_IDX = 5;
  private static final byte WRITE = -0x80;
  private static final int BYTE_MSB_VALUE = 128;

  private SpiDevice spi;
  private StringBuffer uart0Buffer;
  private StringBuffer uart1Buffer;
  private StringBuffer uart2Buffer;
  private StringBuffer uart3Buffer;

  public MAX14830(SpiDevice spi) {
    this.spi = spi;
    this.uart0Buffer = new StringBuffer();
    this.uart1Buffer = new StringBuffer();
    this.uart2Buffer = new StringBuffer();
    this.uart3Buffer = new StringBuffer();
  }

  protected int getTXBufferLen(Port port) throws IOException {
    int uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte command = (byte) (uartSelect | Registers.TxFIFOLvl.address());
    return this.readRegister(command);
  }

  protected int getRXBufferLen(Port port) throws IOException {
    int uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte command = (byte) (uartSelect | Registers.RxFIFOLvl.address());
    return this.readRegister(command);
  }
  
  private int readRegister(byte command) throws IOException {
    byte[] data = {command, 0};
    byte[] readData = this.spi.write(data);
    if(readData.length < 2) {
      return -1;
    }
    int result = readData[1];
    if(result < 0) {
      result += 2*BYTE_MSB_VALUE;
    }
    return result;
  }
  
  protected int writeToTxFifo(Port port, int charCount) throws IOException {
    StringBuffer stringBuffer = this.selectBuffer(port);
    
    int readCount = Math.min(charCount, stringBuffer.length());
    
    String first = stringBuffer.substring(0, readCount);
    stringBuffer.delete(0, readCount);
    int uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte command = (byte) (uartSelect | WRITE | Registers.RHR.address());
    byte[] data = first.getBytes();
    byte[] toWrite = new byte[data.length + 1];
    toWrite[0] = command;
    System.arraycopy(data, 0, toWrite, 1, data.length);
    this.spi.write(toWrite);
    return readCount;
  }

  public SerialPort getPort(Port port) {
    return new SerialPort() {

      @Override
      public void registerListener(SerialListener listener) {
        MAX14830.this.registerListener(port, listener);
      }

      @Override
      public void write(String data) {
        writeToPort(port, data);
      }
      
    };
  }

  public void writeToPort(Port port, String data) {
    StringBuffer stringBuffer = this.selectBuffer(port);
    stringBuffer.append(data);
  }
  
  private StringBuffer selectBuffer(Port port) {
    switch(port) {
    case UART0:
      return this.uart0Buffer;
    case UART1:
      return this.uart1Buffer;
    case UART2:
      return this.uart2Buffer;
    default:
      return this.uart3Buffer;
    }
  }

  public void registerListener(Port port, SerialListener listener) {

  }
  

  @Override
  public void poll() {

  }
}
