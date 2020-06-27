package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.subsystems.EngineSubsystem;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

public class LaunchCommand implements Command{
	private EngineSubsystem enginesubsystem;
	
	LaunchCommand(EngineSubsystem enginesubsystem){
		this.enginesubsystem = enginesubsystem;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public void execute() {
		enginesubsystem.activateEngine();
	}

	@Override
	public void start() {
		
	}

	@Override
	public void end() {
		
	}

	@Override
	public Subsystem[] getDependencies() {
		return new Subsystem [0];
	}

}
