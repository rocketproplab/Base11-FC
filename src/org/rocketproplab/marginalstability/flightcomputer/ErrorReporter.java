package org.rocketproplab.marginalstability.flightcomputer;

import java.io.PrintStream;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

/**
 * A class to report errors which are encountered during the operation of the
 * flight computer. Errors get logged to a printstream and to the Telemetry
 * subsystem given the appropriate information. <br>
 * <br>
 * A good way to use this class would be to have a printstream writing to a file
 * along with a copy of that print stream writing to stderr. That was
 * interactive applications can report errors instantly while all errors will be
 * recorded.
 * 
 * @author Max Apodaca
 *
 */
public class ErrorReporter {

  private static ErrorReporter instance;

  /**
   * Instantiate the singleton if not already and return it.
   * 
   * @return A singleton of type ErrorReporter
   */
  public static ErrorReporter getInstance() {
    if (instance == null) {
      instance = new ErrorReporter();
    }
    return instance;
  }

  /**
   * Sets the singleton in case you want to output to a particular stream. This
   * should be called before any calls to {@link #getInstance()}.
   * 
   * @param reporter the new singleton
   */
  public static void setInstance(ErrorReporter reporter) {
    instance = reporter;
  }

  /**
   * No arg constructor that should print to stderr and not send telemetry.
   */
  public ErrorReporter() {
  }

  /**
   * Error reporter that should print to the given print stream and send telemetry
   * to the given telemetry.
   * 
   * @param stream    the stream to print to
   * @param telemetry the telemetry object to use to send telemetry.
   */
  public ErrorReporter(PrintStream stream, Telemetry telemetry) {

  }

  /**
   * @see #reportError(Errors, Exception, String)
   */
  public void reportError(Errors error) {
    this.reportError(error, null, null);
  }

  /**
   * @see #reportError(Errors, Exception, String)
   */
  public void reportError(Exception exception) {
    this.reportError(Errors.UNKNOWN_ERROR, exception, null);
  }

  /**
   * @see #reportError(Errors, Exception, String)
   */
  public void reportError(String extraInfo) {
    this.reportError(Errors.UNKNOWN_ERROR, null, extraInfo);
  }

  /**
   * @see #reportError(Errors, Exception, String)
   */
  public void reportError(Errors error, String extraInfo) {
    this.reportError(error, null, extraInfo);
  }

  /**
   * @see #reportError(Errors, Exception, String)
   */
  public void reportError(Errors error, Exception exception) {
    this.reportError(error, exception, null);
  }

  /**
   * @see #reportError(Errors, Exception, String)
   */
  public void reportError(Exception exception, String extraInfo) {
    this.reportError(Errors.UNKNOWN_ERROR, exception, extraInfo);
  }

  /**
   * Reports a given error, exception and extra info. The String gets sent to the
   * printstream along with the exception if present. The Errors enum gets
   * reported to telemetry. All parameters may be null.
   * 
   * @param error     What error should be reported to the telemetry, may be null.
   * @param exception What exception caused the error, may be null.
   * @param extraInfo Additional useful text related to the error, may be null.
   */
  public void reportError(Errors error, Exception exception, String extraInfo) {

  }

}
