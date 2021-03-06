package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.junit.Before;
import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Time;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.commands.Command;
import org.rocketproplab.marginalstability.flightcomputer.commands.FramedSCMCommandFactory;
import org.rocketproplab.marginalstability.flightcomputer.commands.SCMCommandFactory;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;

import static org.junit.Assert.*;

public class TestSCMCommandSubsystem {
  private static class TestCommand implements Command {
    private boolean commandExecuted = false;

    @Override
    public boolean isDone() {
      return commandExecuted;
    }

    @Override
    public void execute() {
      commandExecuted = true;
    }

    @Override
    public void start() {
      // nothing
    }

    @Override
    public void end() {
      // nothing
    }

    @Override
    public Subsystem[] getDependencies() {
      return new Subsystem[0];
    }
  }

  private static class TestSCMCommandFactory implements SCMCommandFactory {
    private TestCommand command;

    @Override
    public Command getCommandBySCM(SCMPacketType scmPacketType) {
      if (command == null) {
        command = new TestCommand();
      }
      return command;
    }
  }

  private static class TestFramedSCMCommandFactory implements FramedSCMCommandFactory {
    private TestCommand command;

    @Override
    public Command getCommandByFramedSCM(String framedSCMData) {
      if (command == null) {
        command = new TestCommand();
      }
      return command;
    }
  }

  private Looper              looper;
  private SCMCommandSubsystem scmCommandSubsystem;

  @Before
  public void init() {
    this.looper = new Looper(new Time());
    this.scmCommandSubsystem = new SCMCommandSubsystem();
    this.scmCommandSubsystem.prepare(looper);
  }

  @Test
  public void extractFramedSCMData() {
    String data = "1234567890|ABC|";
    String framedSCM = "AA|" + data;

    String extracted = SCMCommandSubsystem.extractFramedSCMData(framedSCM);
    assertEquals(data, extracted);
  }

  @Test
  public void scheduleCommandWithSCM() {
    SCMPacketType type = SCMPacketType.V0;
    SCMCommandFactory factory = new TestSCMCommandFactory();
    SCMPacket packet = new SCMPacket(type, "ABCDE");

    scmCommandSubsystem.registerSCMCommand(type, factory);
    scmCommandSubsystem.onPacket(null, packet);
    looper.tick();

    assertTrue(factory.getCommandBySCM(type).isDone());
  }

  @Test
  public void scheduleCommandWithFramedSCM() {
    String framedSCMData = "1234567890|ABC|";
    String framedSCMPacket = "AA|" + framedSCMData;
    FramedSCMCommandFactory factory = new TestFramedSCMCommandFactory();

    scmCommandSubsystem.registerFramedSCMCommand(framedSCMData, factory);
    scmCommandSubsystem.processFramedPacket(framedSCMPacket);
    looper.tick();

    assertTrue(factory.getCommandByFramedSCM(framedSCMData).isDone());
//    fail();
  }
}