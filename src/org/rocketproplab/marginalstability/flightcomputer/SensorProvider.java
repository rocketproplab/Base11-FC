package org.rocketproplab.marginalstability.flightcomputer;

import org.rocketproplab.marginalstability.flightcomputer.hal.LPS22HD;

/**
 * A provider to provide singleton sensor instances.
 *
 * @author Chi Chow
 */
public abstract class SensorProvider {

    abstract LPS22HD getPressureSensor();

    private static SensorProvider instance;

    /**
     * Create singleton SensorProvider.
     *
     * @param useRealSensors whether to use real sensors or simulators
     * @return new SensorProvider instance
     */
    public static SensorProvider create(boolean useRealSensors) {
        if (instance != null) {
            throw new RuntimeException("SensorProvider has already been created.");
        }
        if (useRealSensors) {
            instance = new RealSensorProvider();
        } else {
            instance = new SimulatorProvider();
        }
        return instance;
    }

    /**
     * SensorProvider that returns simulators
     */
    private static class SimulatorProvider extends SensorProvider {
        @Override
        public LPS22HD getPressureSensor() {
            // TODO: implement methods to get different simulators
            throw new RuntimeException("Not implemented!");
        }
    }

    /**
     * SensorProvider that returns real sensors
     */
    private static class RealSensorProvider extends SensorProvider {
        @Override
        public LPS22HD getPressureSensor() {
            // TODO: implement methods to get different real sensors
            throw new RuntimeException("Not implemented!");
        }
    }
}
