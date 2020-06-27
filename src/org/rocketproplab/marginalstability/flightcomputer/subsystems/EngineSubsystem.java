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
		for(int index = 0; index < Settings.ENGINE_ON_VALVE_STATES.length; index++) {
			valves.setValve(index, Settings.ENGINE_ON_VALVE_STATES[index]);
		}
		this.notifyEngineActivation();
	}
	
	private void notifyEngineActivation() {
		for(EngineEventListener listener : this.listeners) {
			listener.onEngineActivation();
		}
	}
	
	public void registerEngineListener(EngineEventListener listener) {
		this.listeners.add(listener);
	}
}
