package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

import java.util.Collections;

public class UpdateSettingCommand implements Command {
  private final String setting;

  UpdateSettingCommand(String setting) {
    this.setting = setting;
  }

  public static UpdateSettingCommand getUpdateSettingCommand(String setting) {
    return new UpdateSettingCommand(setting);
  }

  @Override
  public boolean isDone() {
    return true;
  }

  @Override
  public void execute() {
    Settings.readSettingsFromConfig(Collections.singletonList(setting));
  }

  @Override
  public void start() {
  }

  @Override
  public void end() {
  }

  @Override
  public Subsystem[] getDependencies() {
    return new Subsystem[0];
  }

}
