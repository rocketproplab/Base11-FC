package org.rocketproplab.marginalstability.flightcomputer.looper;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.commands.DummyCommand;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.DummySubsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

import static org.junit.Assert.*;

public class TestLooper {
  private static class TestCallback implements EventCallback {
    private boolean hasCalled = false;

    @Override
    public void onLooperCallback(Object tag, Looper from) {
      hasCalled = true;
    }
  }

  private static class TestCallbackCondition implements EventCondition {
    private boolean shouldEmit = false;

    @Override
    public boolean shouldEmit() {
      return shouldEmit;
    }
  }

  private static class TestTime extends Time {

    private double time = 0;

    public void addTime(double add) {
      this.time += add;
    }

    public void setTime(double time) {
      this.time = time;
    }

    @Override
    public double getSystemTime() {
      return time;
    }

  }

  @Test
  public void checkSingletonInstance() {
    // get an instance of command scheduler
    Looper singletonInstance = Looper.getInstance();

    // instance should never be null
    assertNotNull(singletonInstance);

    // create a new command scheduler
    Looper differentScheduler = new Looper(new Time());

    // new command scheduler must be different than singleton instance
    assertNotEquals(differentScheduler, singletonInstance);
  }

  @Test
  public void testScheduleSameCommandMultipleTimes() {
    Looper looper = new Looper(new Time());
    DummyCommand command1 = new DummyCommand();

    looper.scheduleCommand(command1);
    looper.scheduleCommand(command1);
    looper.scheduleCommand(command1);

    assertFalse(command1.isDone());
    assertEquals(0, command1.getNumberOfTimesExecuted());

    looper.tick();

    assertTrue(command1.isDone());
    assertEquals(1, command1.getNumberOfTimesExecuted());

    looper.scheduleCommand(command1);
    looper.tick();

    assertTrue(command1.isDone());
    assertEquals(1, command1.getNumberOfTimesExecuted());

    looper.scheduleCommand(command1);
    looper.tick();

    assertTrue(command1.isDone());
    assertEquals(1, command1.getNumberOfTimesExecuted());
  }

  @Test
  public void testSchedulerGetCommandUsingSubsystem() {
    Looper looper = new Looper(new Time());

    DummySubsystem sA = new DummySubsystem();
    DummySubsystem sB = new DummySubsystem();
    DummySubsystem sC = new DummySubsystem();

    DummyCommand command1 = new DummyCommand();
    command1.dependencies = new Subsystem[]{sA, sC};
    DummyCommand command2 = new DummyCommand();
    command2.dependencies = new Subsystem[]{sC};
    DummyCommand command3 = new DummyCommand();
    command3.dependencies = new Subsystem[]{sB};

    looper.scheduleCommand(command1);
    looper.scheduleCommand(command2);
    looper.scheduleCommand(command3);

    assertNull(looper.getCommandUsingSubsystem(sA));
    assertNull(looper.getCommandUsingSubsystem(sB));
    assertNull(looper.getCommandUsingSubsystem(sC));

    looper.tick();

    assertEquals(command1, looper.getCommandUsingSubsystem(sA));
    assertEquals(command3, looper.getCommandUsingSubsystem(sB));
    assertEquals(command1, looper.getCommandUsingSubsystem(sC));

    looper.tick();

    assertNull(looper.getCommandUsingSubsystem(sA));
    assertNull(looper.getCommandUsingSubsystem(sB));
    assertEquals(command2, looper.getCommandUsingSubsystem(sC));

    looper.tick();

    assertNull(looper.getCommandUsingSubsystem(sA));
    assertNull(looper.getCommandUsingSubsystem(sB));
    assertNull(looper.getCommandUsingSubsystem(sC));
  }

  @Test
  public void testScheduleCommandsWithNoDependencies() {
    Looper looper = new Looper(new Time());

    DummyCommand command1 = new DummyCommand();
    command1.doneAfter = 3;
    DummyCommand command2 = new DummyCommand();
    command2.doneAfter = 1;

    looper.scheduleCommand(command1);
    looper.scheduleCommand(command2);

    assertFalse(command1.isDone());
    assertFalse(command2.isDone());

    looper.tick();

    assertFalse(command1.isDone());
    assertTrue(command2.isDone());

    looper.tick();

    assertFalse(command1.isDone());
    assertTrue(command2.isDone());

    looper.tick();

    assertTrue(command1.isDone());
    assertTrue(command2.isDone());
  }

  /**
   * Tests scenarios:
   * <p>
   * 1. Command 1 in queue depends on subsystem A, command 2 depends on
   * subsystems A and B, and command 3 depends on subsystem B. Expected order of
   * commands: 1, 2, 3. Even though command 3 could run while command 1 is
   * running, if we did that then we would be blocking command 2 from running
   * since it depends on both subsystems A and B. Thus command 3 must not run
   * until command 2 is done.
   * <p>
   * 2. Command 3 depends on subsystem B and command 4 depends on subsystems A
   * and C. Expected order of commands: 3 & 4 in parallel Since commands 3 and 4
   * do not require the same dependencies, they should both run at the same
   * time.
   */
  @Test
  public void testScheduleCommandsWithDependencies() {
    Looper looper = new Looper(new Time());

    Subsystem subsystemA = new DummySubsystem();
    Subsystem subsystemB = new DummySubsystem();
    Subsystem subsystemC = new DummySubsystem();

    // Create dummy commands.
    DummyCommand command1 = new DummyCommand();
    command1.dependencies = new Subsystem[]{subsystemA};
    command1.doneAfter = 2;
    DummyCommand command2 = new DummyCommand();
    command2.dependencies = new Subsystem[]{subsystemA, subsystemB};
    command2.doneAfter = 1;
    DummyCommand command3 = new DummyCommand();
    command3.dependencies = new Subsystem[]{subsystemB};
    command3.doneAfter = 1;
    DummyCommand command4 = new DummyCommand();
    command4.dependencies = new Subsystem[]{subsystemA, subsystemC};
    command4.doneAfter = 1;
    DummyCommand command5 = new DummyCommand();
    command5.doneAfter = 1;

    // Add all commands to scheduler
    looper.scheduleCommand(command1);
    looper.scheduleCommand(command2);
    looper.scheduleCommand(command3);
    looper.scheduleCommand(command4);
    looper.scheduleCommand(command5);

    assertFalse(command1.isDone());
    assertFalse(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());
    assertFalse(command5.isDone());

    looper.tick();

    assertFalse(command1.isDone());
    assertFalse(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());
    assertTrue(command5.isDone());

    looper.tick();

    assertTrue(command1.isDone());
    assertFalse(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());
    assertTrue(command5.isDone());

    looper.tick();

    assertTrue(command1.isDone());
    assertTrue(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());
    assertTrue(command5.isDone());

    looper.tick();

    assertTrue(command1.isDone());
    assertTrue(command2.isDone());
    assertTrue(command3.isDone());
    assertTrue(command4.isDone());
    assertTrue(command5.isDone());
  }

  @Test
  public void emitAlways() {
    Looper looper = new Looper(new Time());
    String tag = "";
    TestCallback callback = new TestCallback();
    looper.emitAlways(tag, callback);

    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    looper.tick();
    assertTrue(callback.hasCalled);

    assertNotNull(looper.getEvent(tag));
  }

  @Test
  public void emitScheduled() {
    TestTime time = new TestTime();
    Looper looper = new Looper(time);
    TestCallback callback = new TestCallback();
    String tag = "";
    double interval = 50.0, add = 30.0;
    looper.emitScheduled(tag, interval, callback);

    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertTrue(callback.hasCalled);

    assertNotNull(looper.getEvent(tag));
  }

  @Test
  public void emitIf() {
    Looper looper = new Looper(new Time());
    TestCallbackCondition condition = new TestCallbackCondition();
    TestCallback callback = new TestCallback();
    String tag = "";
    looper.emitIf(tag, condition, callback);

    looper.tick();
    assertFalse(callback.hasCalled);

    condition.shouldEmit = true;
    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    looper.tick();
    assertTrue(callback.hasCalled);

    assertNotNull(looper.getEvent(tag));
  }

  @Test
  public void emitScheduledIf() {
    TestTime time = new TestTime();
    Looper looper = new Looper(time);
    TestCallbackCondition condition = new TestCallbackCondition();
    TestCallback callback = new TestCallback();
    String tag = "";
    double interval = 50.0, add = 30.0;
    looper.emitScheduledIf(tag, interval, condition, callback);

    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    condition.shouldEmit = true;
    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    condition.shouldEmit = false;
    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    assertNotNull(looper.getEvent(tag));
  }

  @Test
  public void immediatelyEmitOnceIf() {
    Looper looper = new Looper(new Time());
    TestCallbackCondition condition = new TestCallbackCondition();
    TestCallback callback = new TestCallback();
    String tag = "";
    looper.emitOnceIf(tag, condition, callback);

    looper.tick();
    assertFalse(callback.hasCalled);

    condition.shouldEmit = true;
    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    looper.tick();
    assertFalse(callback.hasCalled);

    assertNull(looper.getEvent(tag));
  }

  @Test
  public void durationEmitOnceIf() {
    TestTime time = new TestTime();
    Looper looper = new Looper(time);
    TestCallbackCondition condition = new TestCallbackCondition();
    TestCallback callback = new TestCallback();
    String tag = "";
    double duration = 50.0, add = 30.0;
    looper.emitOnceIf(tag, duration, condition, callback);

    looper.tick();
    assertFalse(callback.hasCalled);

    condition.shouldEmit = true;
    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    time.addTime(add);
    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    time.addTime(add);
    looper.tick();
    assertFalse(callback.hasCalled);

    assertNull(looper.getEvent(tag));
  }

  @Test
  public void removeEventAndCallback() {
    Looper looper = new Looper(new Time());
    String tag = "";
    TestCallback callback = new TestCallback();
    looper.emitAlways(tag, callback);

    looper.tick();
    assertTrue(callback.hasCalled);

    callback.hasCalled = false;
    looper.removeEvent(tag);
    looper.tick();
    assertFalse(callback.hasCalled);

    assertNull(looper.getEvent(tag));
  }
}
