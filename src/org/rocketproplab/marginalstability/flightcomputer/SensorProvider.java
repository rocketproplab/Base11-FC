package org.rocketproplab.marginalstability.flightcomputer;

public abstract class SensorProvider {

    abstract void getSensor();

    private static SensorProvider instance;

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

    // TODO: add all necessary sensors
    private static class SimulatorProvider extends SensorProvider {
        @Override
        public void getSensor() {
            // TODO: implement methods to get different simulators
        }
    }

    private static class RealSensorProvider extends SensorProvider {
        @Override
        public void getSensor() {
            // TODO: implement methods to get different real sensors
        }
    }
}
