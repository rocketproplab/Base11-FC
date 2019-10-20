package org.rocketproplab.marginalstability.flightcomputer.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
  
}
