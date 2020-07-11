package org.rocketproplab.marginalstability.flightcomputer;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

public class ErrorReporterTest {

  private class FakeTelemetry extends Telemetry {
    private ArrayList<Errors> errorsReported;

    public FakeTelemetry() {
      super(null, null);
      this.errorsReported = new ArrayList<>();
    }

    @Override
    public void reportError(Errors error) {
      errorsReported.add(error);
    }

  }

  private ByteArrayOutputStream outputStream;
  private FakeTelemetry         telemetry;
  private ErrorReporter         reporter;

  @Before
  public void before() {
    this.outputStream = new ByteArrayOutputStream();
    PrintStream stream = new PrintStream(this.outputStream);
    this.telemetry = new FakeTelemetry();
    this.reporter  = new ErrorReporter(stream, this.telemetry);
  }

  @Test
  public void errorReporterPrintsMessageToOutputStream() {
    String errorMsg = "Error in room 2";
    this.reporter.reportError(null, null, errorMsg);
    assertEquals(errorMsg + "\n", outputStream.toString());
    assertEquals(0, this.telemetry.errorsReported.size());
  }

  @Test
  public void errorReporterPrintsErrorInfoToOutputStream() {
    this.reporter.reportError(Errors.TOP_LEVEL_EXCEPTION, null, null);
    assertEquals(Errors.TOP_LEVEL_EXCEPTION.toString() + "\n", outputStream.toString());
    assertEquals(1, this.telemetry.errorsReported.size());
    assertEquals(Errors.TOP_LEVEL_EXCEPTION, this.telemetry.errorsReported.get(0));
  }

  @Test
  public void errorReporterPrintsStackTraceToOutputStream() {
    Exception             exception       = new IllegalArgumentException();
    ByteArrayOutputStream exceptionStream = new ByteArrayOutputStream();
    exception.printStackTrace(new PrintStream(exceptionStream));
    this.reporter.reportError(null, exception, null);
    assertEquals(exceptionStream.toString(), this.outputStream.toString());
    assertEquals(0, this.telemetry.errorsReported.size());
  }

  @Test
  public void reportingErrorAndExceptionJoinsTheTwoToOutputStream() {
    Exception             exception       = new BrokenBarrierException();
    ByteArrayOutputStream exceptionStream = new ByteArrayOutputStream();
    exception.printStackTrace(new PrintStream(exceptionStream));
    this.reporter.reportError(Errors.UNKNOWN_ERROR, exception);
    String expected = Errors.UNKNOWN_ERROR.toString() + "\n" + exceptionStream.toString();
    assertEquals(expected, this.outputStream.toString());
    assertEquals(1, this.telemetry.errorsReported.size());
    assertEquals(Errors.UNKNOWN_ERROR, this.telemetry.errorsReported.get(0));
  }
  
  @Test
  public void reportingErrorAndInfoJoinsTheTwoToOutputStream() {
    String errorMessage = "Bad alloc";
    this.reporter.reportError(Errors.TOP_LEVEL_EXCEPTION, errorMessage);
    String expected = errorMessage + "\n" + Errors.TOP_LEVEL_EXCEPTION.toString() + "\n";
    assertEquals(expected, this.outputStream.toString());
    assertEquals(1, this.telemetry.errorsReported.size());
    assertEquals(Errors.TOP_LEVEL_EXCEPTION, this.telemetry.errorsReported.get(0));
  }
}
