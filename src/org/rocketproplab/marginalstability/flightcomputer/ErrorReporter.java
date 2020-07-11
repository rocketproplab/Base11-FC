package org.rocketproplab.marginalstability.flightcomputer;

import java.io.PrintStream;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

public class ErrorReporter {
  
  private static ErrorReporter instance;
  
  public static ErrorReporter getInstance() {
    if(instance == null) {
      instance = new ErrorReporter();
    }
    return instance;
  }
  
  public static void setInstance(ErrorReporter reporter) {
    instance = reporter;
  }
  
  
  public ErrorReporter() {
  }
  
  public ErrorReporter(PrintStream stream, Telemetry telemetry) {
    
  }

  public void reportError(Errors error) {
    this.reportError(error, null, null);
  }
  
  public void reportError(Exception exception) {
    this.reportError(Errors.UNKNOWN_ERROR, exception, null);
  }
  
  public void reportError(String extraInfo) {
    this.reportError(Errors.UNKNOWN_ERROR, null, extraInfo);
  }
  
  public void reportError(Errors error, String extraInfo) {
    this.reportError(error, null, extraInfo);
  }
  
  public void reportError(Errors error, Exception exception) {
    this.reportError(error, exception, null);
  }
  
  public void reportError(Exception exception, String extraInfo) {
    this.reportError(Errors.UNKNOWN_ERROR, exception, extraInfo);
  }
  
  public void reportError(Errors error, Exception exception, String extraInfo) {
    
  }
  
}
