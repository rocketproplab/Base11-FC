package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.Settings;
import org.rocketproplab.marginalstability.flightcomputer.events.EngineEventListener;
import org.rocketproplab.marginalstability.flightcomputer.hal.Valves;

public class TestEngineSubsystem {
	private class MockValves implements Valves{
		public boolean[] mockvalvestates = new boolean[8];
		@Override
		public void setValve(int index, boolean active) {
			mockvalvestates[index] = active;
		} 
	}
	
	private class MockEngineEventListener implements EngineEventListener {
		public int timesOnEngineActivationCalled = 0;
		public int timesOnEngineShutdownCalled = 0;
		@Override
		public void onEngineActivation() {
			timesOnEngineActivationCalled++;
		}

		@Override
		public void onEngineShutdown() {
			timesOnEngineShutdownCalled++;
			
		}

		@Override
		public void onEngineData(EngineDataType type, double value) {
			
		}
		
	} 
	
	@Test
	public void activateEngineSetValves() {
		Settings.ENGINE_ON_VALVE_STATES = new boolean[] {true, true, true, true, true};
		MockValves mockvalve = new MockValves();
		EngineSubsystem enginesubsystem = new EngineSubsystem(mockvalve);
		enginesubsystem.activateEngine();
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[0],mockvalve.mockvalvestates[0]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[1],mockvalve.mockvalvestates[1]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[2],mockvalve.mockvalvestates[2]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[3],mockvalve.mockvalvestates[3]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[4],mockvalve.mockvalvestates[4]);
	}
	
	@Test
	public void activateEngineSetValvesToFalse() {
		Settings.ENGINE_ON_VALVE_STATES = new boolean[] {true, false, true, true, true};
		MockValves mockvalve = new MockValves();
		EngineSubsystem enginesubsystem = new EngineSubsystem(mockvalve);
		enginesubsystem.activateEngine();
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[0],mockvalve.mockvalvestates[0]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[1],mockvalve.mockvalvestates[1]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[2],mockvalve.mockvalvestates[2]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[3],mockvalve.mockvalvestates[3]);
		assertEquals(Settings.ENGINE_ON_VALVE_STATES[4],mockvalve.mockvalvestates[4]);
	}
	
	
	
	@Test
	public void activateEngineEventEmitted() {
		MockValves mockvalve = new MockValves();
		EngineSubsystem enginesubsystem = new EngineSubsystem(mockvalve);
		MockEngineEventListener mocklistener = new MockEngineEventListener();
		enginesubsystem.registerEngineListener(mocklistener);
		enginesubsystem.activateEngine();	
		assertEquals(1, mocklistener.timesOnEngineActivationCalled);
		assertEquals(0, mocklistener.timesOnEngineShutdownCalled);
		
	}
	
	@Test
	public void deactivateSetValves() {
		Settings.ENGINE_ABORT_VALVE_STATES = new boolean[] {true, true, true, true, true};
		MockValves mockvalve = new MockValves();
		EngineSubsystem enginesubsystem = new EngineSubsystem(mockvalve);
		enginesubsystem.deactivateEngine();
		assertEquals(Settings.ENGINE_ABORT_VALVE_STATES[0],mockvalve.mockvalvestates[0]);
		assertEquals(Settings.ENGINE_ABORT_VALVE_STATES[1],mockvalve.mockvalvestates[1]);
		assertEquals(Settings.ENGINE_ABORT_VALVE_STATES[2],mockvalve.mockvalvestates[2]);
		assertEquals(Settings.ENGINE_ABORT_VALVE_STATES[3],mockvalve.mockvalvestates[3]);
		assertEquals(Settings.ENGINE_ABORT_VALVE_STATES[4],mockvalve.mockvalvestates[4]);
	}
	
	@Test
	public void deactivateEngineEventEmitted() {
		MockValves mockvalve = new MockValves();
		EngineSubsystem enginesubsystem = new EngineSubsystem(mockvalve);
		MockEngineEventListener mocklistener = new MockEngineEventListener();
		enginesubsystem.registerEngineListener(mocklistener);
		enginesubsystem.deactivateEngine();	
		assertEquals(1, mocklistener.timesOnEngineShutdownCalled);
		assertEquals(0, mocklistener.timesOnEngineActivationCalled);
		
	}
	
	
}
