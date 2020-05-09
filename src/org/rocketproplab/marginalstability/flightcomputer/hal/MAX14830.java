package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;
import java.nio.charset.Charset;

import org.rocketproplab.marginalstability.flightcomputer.Settings;

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

  private static final String CHARSET = "US-ASCII";

  private static final int  UART_SELECT_LSB_IDX = 5;
  private static final byte WRITE               = -0x80;
  private static final int  BYTE_MSB_VALUE      = 128;
  private static final int  BITS_PER_BYTE       = 8;

  private static final int TX_BUFFER_SIZE = 128;

  private SpiDevice           spi;
  private StringBuffer[]      uartBufferArray;
  private SerialPortAdapter[] serialPortArray;
  private int[]               txFifoLengths;
  private Charset             charset;

  public MAX14830(SpiDevice spi) {
    this.spi             = spi;
    this.uartBufferArray = new StringBuffer[Port.values().length];
    this.serialPortArray = new SerialPortAdapter[Port.values().length];
    this.txFifoLengths   = new int[Port.values().length];
    for (int i = 0; i < Port.values().length; i++) {
      this.uartBufferArray[i] = new StringBuffer();
      this.txFifoLengths[i]   = TX_BUFFER_SIZE;
      final Port port = Port.values()[i];
      this.serialPortArray[i] = new SerialPortAdapter(message -> this.writeToPort(port, message));
    }
    this.charset = Charset.forName(CHARSET);
  }

  protected int getTXBufferLen(Port port) throws IOException {
    int  uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte command    = (byte) (uartSelect | Registers.TxFIFOLvl.address());
    return this.readRegister(command);
  }

  protected int getRXBufferLen(Port port) throws IOException {
    int  uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte command    = (byte) (uartSelect | Registers.RxFIFOLvl.address());
    return this.readRegister(command);
  }

  private int readRegister(byte command) throws IOException {
    byte[] data     = { command, 0 };
    byte[] readData = this.spi.write(data);
    if (readData.length < 2) {
      return -1;
    }
    int result = readData[1];
    if (result < 0) {
      result += 2 * BYTE_MSB_VALUE;
    }
    return result;
  }

  protected int writeToTxFifo(Port port, int charCount) throws IOException {
    StringBuffer stringBuffer = this.selectBuffer(port);

    int readCount = Math.min(charCount, stringBuffer.length());

    String first = stringBuffer.substring(0, readCount);
    stringBuffer.delete(0, readCount);
    int    uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte   command    = (byte) (uartSelect | WRITE | Registers.THR.address());
    byte[] data       = first.getBytes(this.charset);
    byte[] toWrite    = new byte[data.length + 1];
    toWrite[0] = command;
    System.arraycopy(data, 0, toWrite, 1, data.length);
    this.spi.write(toWrite);
    return readCount;
  }

  protected byte[] readFromRxFifo(Port port, int charCount) throws IOException {
    int    uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte   command    = (byte) (uartSelect | Registers.RHR.address());
    byte[] zeros      = new byte[charCount + 1];
    zeros[0] = command;
    return this.spi.write(zeros);
  }

  public SerialPort getPort(Port port) {
    return this.serialPortArray[port.ordinal()];
  }

  public void writeToPort(Port port, String data) {
    StringBuffer stringBuffer = this.selectBuffer(port);
    stringBuffer.append(data);
  }

  private StringBuffer selectBuffer(Port port) {
    return this.uartBufferArray[port.ordinal()];
  }

  private void writeToPort(Port port, int length) throws IOException {
    int spaceLeft = TX_BUFFER_SIZE - this.txFifoLengths[port.ordinal()];
    if (spaceLeft < length) {
      int txBufferLen = this.getTXBufferLen(port);
      spaceLeft                          = TX_BUFFER_SIZE - txBufferLen;
      this.txFifoLengths[port.ordinal()] = txBufferLen;
    }

    if (spaceLeft > 0) {
      int written = this.writeToTxFifo(port, spaceLeft);
      this.txFifoLengths[port.ordinal()] += written;
    }
  }

  private void readFromPort(Port port, int length) throws IOException {
    byte[]            byteMessage        = this.readFromRxFifo(port, length);
    String            unprocessedMessage = new String(byteMessage, this.charset);
    String            message            = unprocessedMessage.substring(1);
    SerialPortAdapter serialPort         = this.serialPortArray[port.ordinal()];
    serialPort.newMessage(message);
  }

  private void pollPort(Port port) throws IOException {
    StringBuffer buffer = this.selectBuffer(port);
    int          length = buffer.length();
    if (length != 0) {
      this.writeToPort(port, length);
    }
    int rxLen = this.getRXBufferLen(port);
    if (rxLen > 0) {
      this.readFromPort(port, rxLen);
    }
  }

  @Override
  public void poll() {
    try {
      for (Port port : Port.values()) {
        pollPort(port);
      }
    } catch (IOException e) {
      e.printStackTrace();
      // TODO Handle error
    }

  }

  public void setBaudrate(Port port, int baudrate) throws IOException {
    // D = fREF / ( 16 * BaudRate ) from datasheet page 21
    int d = 1;
    if (baudrate > 0) {
      d = Settings.MAX14830_F_REF / (16 * baudrate);
    }
    int    uartSelect           = port.ordinal() << UART_SELECT_LSB_IDX;
    byte   command              = (byte) (uartSelect | WRITE | Registers.BRGConfig.address());
    byte   leastSignificantBits = (byte) (d & 0xFF);
    byte   mostSignificantBits  = (byte) ((d >> BITS_PER_BYTE) & 0xFF);
    byte[] data                 = { command, 0, leastSignificantBits, mostSignificantBits };
    this.spi.write(data);

  }
}
