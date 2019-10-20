package org.rocketproplab.marginalstability.flightcomputer.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

public class TestCommandScheduler {

  @Test
  public void checkSingletonInstance() {
    // get an instance of command scheduler
    CommandScheduler singletonInstance = CommandScheduler.getInstance();

    // instance should never be null
    assertNotNull(singletonInstance);

    // create a new command scheduler
    CommandScheduler differentScheduler = new CommandScheduler();

    // new command scheduler must be different than singleton instance
    assertNotEquals(differentScheduler, singletonInstance);
  }

  @Test
  public void testScheduleCommandsWithNoDependencies() {
    DummyCommand command1 = new DummyCommand();
    command1.doneAfter = 3;
    DummyCommand command2 = new DummyCommand();
    command2.doneAfter = 1;

    CommandScheduler.getInstance().scheduleCommand(command1);
    CommandScheduler.getInstance().scheduleCommand(command2);

    assertFalse(command1.isDone());
    assertFalse(command2.isDone());

    CommandScheduler.getInstance().tick();

    assertFalse(command1.isDone());
    assertTrue(command2.isDone());

    CommandScheduler.getInstance().tick();

    assertFalse(command1.isDone());
    assertTrue(command2.isDone());

    CommandScheduler.getInstance().tick();

    assertTrue(command1.isDone());
    assertTrue(command2.isDone());
  }

  /**
   * Tests scenarios:
   * 
   * 1. Command 1 in queue depends on subsystem A, command 2 depends on
   * subsystems A and B, and command 3 depends on subsystem B.
   * Expected order of commands: 1, 2, 3.
   * Even though command 3 could run while command 1 is running, if we did
   * that then we would be blocking command 2 from running since it depends
   * on both subsystems A and B. Thus command 3 must not run until command 2
   * is done.
   * 
   * 2. Command 3 depends on subsystem B and command 4 depends on subsystems
   * A and C.
   * Expected order of commands: 3 & 4 in parallel
   * Since commands 3 and 4 do not require the same dependencies, they should
   * both run together.
   * 
   */
  @Test
  public void testScheduleCommandsWithDependencies() {
    Subsystem subsystemA = new DummySubsystem();
    Subsystem subsystemB = new DummySubsystem();
    Subsystem subsystemC = new DummySubsystem();

    // Create dummy commands.
    DummyCommand command1 = new DummyCommand();
    command1.dependencies = new Subsystem[] { subsystemA };
    command1.doneAfter    = 2;
    DummyCommand command2 = new DummyCommand();
    command2.dependencies = new Subsystem[] { subsystemA, subsystemB };
    command2.doneAfter    = 1;
    DummyCommand command3 = new DummyCommand();
    command3.dependencies = new Subsystem[] { subsystemB };
    command3.doneAfter    = 1;
    DummyCommand command4 = new DummyCommand();
    command4.dependencies = new Subsystem[] { subsystemA, subsystemC };
    command4.doneAfter    = 1;

    // Add all commands to scheduler
    CommandScheduler.getInstance().scheduleCommand(command1);
    CommandScheduler.getInstance().scheduleCommand(command2);
    CommandScheduler.getInstance().scheduleCommand(command3);

    assertFalse(command1.isDone());
    assertFalse(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());

    CommandScheduler.getInstance().tick();

    assertFalse(command1.isDone());
    assertFalse(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());

    CommandScheduler.getInstance().tick();

    assertTrue(command1.isDone());
    assertFalse(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());

    CommandScheduler.getInstance().tick();

    assertTrue(command1.isDone());
    assertTrue(command2.isDone());
    assertFalse(command3.isDone());
    assertFalse(command4.isDone());

    CommandScheduler.getInstance().tick();

    assertTrue(command1.isDone());
    assertTrue(command2.isDone());
    assertTrue(command3.isDone());
    assertTrue(command4.isDone());
  }

}
