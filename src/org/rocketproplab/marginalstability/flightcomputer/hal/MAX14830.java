package org.rocketproplab.marginalstability.flightcomputer.hal;

import java.io.IOException;
import java.nio.charset.Charset;

import org.rocketproplab.marginalstability.flightcomputer.ErrorReporter;
import org.rocketproplab.marginalstability.flightcomputer.Errors;
import org.rocketproplab.marginalstability.flightcomputer.Settings;

import com.pi4j.io.spi.SpiDevice;

/**
 * A class that implements the SPI protocol for the MAX14830, it contains the
 * code to emit serial port events to any listeners listening to ports provided
 * by the MAX14830.
 * 
 * @author Max Apodaca
 *
 */
public class MAX14830 implements PollingSensor {

  /**
   * The list of ports which we can access on the MAX14830
   * 
   * @author Max Apodaca
   *
   */
  public enum Port {
    UART0,
    UART1,
    UART2,
    UART3;
  }

  /**
   * The list of registers found on the MAX14830
   * 
   * @author Max Apodaca
   *
   */
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

  /**
   * Create the MAX14830 and initialize the event handlers with
   * {@link SerialPortAdapter}. TODO Use chipselect via gpio
   * 
   * @param spi the Spi device to use, no validation is done
   */
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

  /**
   * Read the length of the transmit buffer for a given port. This will return how
   * many bytes can currently be written.
   * 
   * @param port which of the four uart channels to read from
   * @return the number of free bytes in the FIFO buffer
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
  protected int getTXBufferLen(Port port) throws IOException {
    int  uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte command    = (byte) (uartSelect | Registers.TxFIFOLvl.address());
    return this.readRegister(command);
  }

  /**
   * Read the receive buffer length for a given port. This reports the number of
   * bytes in the receive buffer that can be currently read.
   * 
   * @param port which of the four uart channels to read for.
   * @return the number of bytes that can be read from the given FIFO buffer.
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
  protected int getRXBufferLen(Port port) throws IOException {
    int  uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte command    = (byte) (uartSelect | Registers.RxFIFOLvl.address());
    return this.readRegister(command);
  }

  /**
   * Read the given register from the chip by executing the given command. Command
   * are the command bits as outlined in the datasheet <a href=
   * "https://datasheets.maximintegrated.com/en/ds/MAX14830.pdf">https://datasheets.maximintegrated.com/en/ds/MAX14830.pdf</a>.<br>
   * <br>
   * The command looks like this on a bit level: <br>
   * {@code | RW | U1 | U0 | A4 | A3 | A2 | A1 | A0 |}
   * <ul>
   * <li>RW should be 0 to indicate reading.</li>
   * <li>U1 and U0 from a 2 bit integer to specify UART 0 - 3.</li>
   * <li>A4 - A0 form a 5 bit integer to specify which register to read. Registers
   * above 0x1F (31) are currently not supported as they are read in a different
   * way.</li>
   * </ul>
   * 
   * @param command the command to write
   * @return -1 on failure or the value of the register
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
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

  /**
   * Trigger a write to the given UART tx FIFO buffer. The data for the write
   * comes from the internal buffer {@link #uartBufferArray}. <br>
   * This method does not check the buffer fill level
   * {@link #getTXBufferLen(Port)} and therefore it is possible to loose data if
   * too many bytes are sent.. If there are too few bytes in the
   * {@link #uartBufferArray} then the remaining bytes are written.
   * 
   * @param port      Which UART channel to use
   * @param charCount how many bytes to write at most
   * @return how many bytes were actually written.
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
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

  /**
   * Try to read from the RX FIFO buffer for the specified UART channel. This
   * method does not check if the buffer actually has enough data.
   * 
   * @param port      which channel to read from
   * @param charCount how many characters to read
   * @return the characters as a byte stream.
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
  protected byte[] readFromRxFifo(Port port, int charCount) throws IOException {
    int    uartSelect = port.ordinal() << UART_SELECT_LSB_IDX;
    byte   command    = (byte) (uartSelect | Registers.RHR.address());
    byte[] zeros      = new byte[charCount + 1];
    zeros[0] = command;
    return this.spi.write(zeros);
  }

  /**
   * Get a serial port interface compatible representation of the given port. This
   * can be used as any other serial port would be.
   * 
   * @param port which UART interface to use
   * @return the serial port created for the UART interface.
   */
  public SerialPort getPort(Port port) {
    return this.serialPortArray[port.ordinal()];
  }

  /**
   * Queue the string to be written to the specified port as soon as possible.
   * 
   * @param port which UART interface to write to
   * @param data the string to write. Will wait until previous stirngs have been
   *             written.
   */
  public void writeToPort(Port port, String data) {
    StringBuffer stringBuffer = this.selectBuffer(port);
    stringBuffer.append(data);
  }

  /**
   * Get the string buffer acting as a queue for the given port.
   * 
   * @param port which UART buffer to select.
   * @return the string buffer acting as a queue
   */
  private StringBuffer selectBuffer(Port port) {
    return this.uartBufferArray[port.ordinal()];
  }

  /**
   * Check if there is enough space left in the TX FIFO for the given port and if
   * so write the queued strings to it. If not write as much as we can.<br>
   * <br>
   * The length is cached to prevent unnecessary calls to
   * {@link #getTXBufferLen(Port)}. If we see that there is enough space from the
   * last call to {@link #getTXBufferLen(Port)} we write to it.
   * 
   * @param port   Which port to select
   * @param length How many characters to write, must be &gt;=  0
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
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

  /**
   * Read from the given port and call the SerilPort callbacks. Length must be
   * smaller or equal to the number of bytes in the RX FIFO.
   * 
   * @param port   which port to read
   * @param length how many bytes to read, passed directly to
   *               {@link #readFromRxFifo(Port, int)}
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
  private void readFromPort(Port port, int length) throws IOException {
    byte[]            byteMessage        = this.readFromRxFifo(port, length);
    String            unprocessedMessage = new String(byteMessage, this.charset);
    String            message            = unprocessedMessage.substring(1);
    SerialPortAdapter serialPort         = this.serialPortArray[port.ordinal()];
    serialPort.newMessage(message);
  }

  /**
   * Check if we should send more data to the TX buffer of the port. If so send
   * the data.<br>
   * Then check if we have data to receive in the RX buffer. If so receive it and
   * emit events.
   * 
   * @param port which UART port to read
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
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
      ErrorReporter errorReporter = ErrorReporter.getInstance();
      String errorMsg = "Unable to access /dev/spix.x via Pi4J";
      errorReporter.reportError(Errors.MAX14830_IO_ERROR, e, errorMsg);
    }

  }

  /**
   * Set the baud rate for the UART channel as best as possible. The baud rate is calculated with the
   * equation BaudRate = fREF / (16 * D) where D is an integer and fREF is the
   * crystal frequency. To select D we use the equation fREF / ( 16 * BaudRate )
   * which is located on page 21 of the datasheet. The fREF used is
   * {@link Settings#MAX14830_F_REF}. All math is integer math so fREF must be an
   * integer multiple of 16 * BaudRate or the actual baud rate will differ from the
   * requested.
   * 
   * @param port the UART channel to use
   * @param baudrate the baud rate in baud
   * @throws IOException if we are unable to access /dev/spix.x via Pi4J
   */
  public void setBaudRate(Port port, int baudrate) throws IOException {
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
