package org.rocketproplab.marginalstability.flightcomputer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Telemetry;

public class FlightComputerTest {
  private Telemetry         telemetry;
  private ArrayList<Errors> errorList;
  private boolean           throwErrorOnError;

  private class MockSubsystem implements Subsystem {
    public boolean hasUpdateCalled = false;
    public boolean throwError      = false;

    @Override
    public void prepare(Looper looper) {
      looper.emitAlways(this, (tag, from) -> {
        this.hasUpdateCalled = true;
        if (this.throwError) {
          throw new RuntimeException();
        }
      });
    }
  }


  @Before
  public void beforeEach() {
    this.throwErrorOnError = false;
    errorList              = new ArrayList<>();
    this.telemetry         = new Telemetry(Logger.getLogger("Dummy"), null) {
      @Override
      public void reportError(Errors error) {
        errorList.add(error);
        if (throwErrorOnError) {
          throw new RuntimeException();
        }
      }
    };
  }

  @Test
  public void flightComputerCallsSubsystemUpdateOnTick() {
    FlightComputer flightComputer = new FlightComputer(this.telemetry, null);
    MockSubsystem  mockSubsystem  = new MockSubsystem();
    flightComputer.registerSubsystem(mockSubsystem);
    flightComputer.tick();
    assertTrue(mockSubsystem.hasUpdateCalled);
  }

  @Test
  public void flightComputerRecoversFromException() {
    FlightComputer flightComputer = new FlightComputer(this.telemetry, null);
    MockSubsystem  mockSubsystem  = new MockSubsystem();
    mockSubsystem.throwError = true;
    flightComputer.registerSubsystem(mockSubsystem);
    flightComputer.tick();
    mockSubsystem.hasUpdateCalled = false;
    mockSubsystem.throwError      = false;
    flightComputer.tick();
    assertTrue(mockSubsystem.hasUpdateCalled);
  }

  @Test
  public void flightComputerReportsErrorToTelemetry() {
    FlightComputer flightComputer = new FlightComputer(this.telemetry, null);
    MockSubsystem  mockSubsystem  = new MockSubsystem();
    mockSubsystem.throwError = true;
    flightComputer.registerSubsystem(mockSubsystem);
    flightComputer.tick();
    assertEquals(1, this.errorList.size());
    assertEquals(Errors.TOP_LEVEL_EXCEPTION, this.errorList.get(0));
  }

  @Test
  public void errorInReportErrorAllowConintuedExecution() {
    FlightComputer flightComputer = new FlightComputer(this.telemetry, null);
    MockSubsystem  mockSubsystem  = new MockSubsystem();
    mockSubsystem.throwError = true;
    this.throwErrorOnError   = true;
    flightComputer.registerSubsystem(mockSubsystem);
    flightComputer.tick();
    mockSubsystem.hasUpdateCalled = false;
    mockSubsystem.throwError      = false;
    flightComputer.tick();
    assertTrue(mockSubsystem.hasUpdateCalled);
  }

}
