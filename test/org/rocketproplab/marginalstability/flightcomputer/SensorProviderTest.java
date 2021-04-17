package org.rocketproplab.marginalstability.flightcomputer;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;


public class SensorProviderTest {
    @After
    public void afterEach() throws Exception {
        Field sensorInstance = SensorProvider.class.getDeclaredField("instance");
        sensorInstance.setAccessible(true);
        sensorInstance.set(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void switchToRealSensors() {
        // TODO: replace with real sensor checks when RealSensorProvider is implemented
        SensorProvider sensorProvider = SensorProvider.create(true);
        sensorProvider.getPressureSensor();
    }

    @Test(expected = RuntimeException.class)
    public void switchToSimulators() {
        // TODO: replace with simulator checks when SimulatorProvider is implemented
        SensorProvider sensorProvider = SensorProvider.create(true);
        sensorProvider.getPressureSensor();
    }
}
