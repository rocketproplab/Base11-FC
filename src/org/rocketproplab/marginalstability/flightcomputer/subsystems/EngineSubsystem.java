package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import java.util.HashSet;
import java.util.Set;

import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.events.EngineEventListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.Valves;

public class EngineSubsystem {
	private Valves valves;
	private Set<EngineEventListener> listeners;
	
	EngineSubsystem(Valves valves){
		this.valves	= valves;
		this.listeners = new HashSet<>();
	}
	public void activateEngine() {
		this.setValveBasedOnSettings(Settings.ENGINE_ON_VALVE_STATES);
		this.notifyEngineActivation();
	}
	
	public void deactivateEngine() {
		this.setValveBasedOnSettings(Settings.ENGINE_ABORT_VALVE_STATES);
		this.notifyEngineDeactivation();
	}
	
	private void setValveBasedOnSettings(boolean[] EngineValveStates) {
		for (int index = 0; index < EngineValveStates.length; index++) {
			valves.setValve(index, EngineValveStates[index]);
		}
	}
	
	private void notifyEngineActivation() {
		for(EngineEventListener listener : this.listeners) {
			listener.onEngineActivation();
		}
	}
	
	private void notifyEngineDeactivation() {
		for(EngineEventListener listener : this.listeners) {
			listener.onEngineShutdown();
		}
	}
	
	public void registerEngineListener(EngineEventListener listener) {
		this.listeners.add(listener);
	}
	
}
