package org.rocketproplab.marginalstability.flightcomputer;

import org.apache.commons.cli.*;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.*;

import java.util.logging.Logger;

public class FlightComputer {
    /**
     * CLI argument names and descriptions
     */
    private static final String REAL_SENSORS = "real-sensors",
            REAL_SENSORS_DESC = "use real sensors";

    private static FlightComputer instance;

    /**
     * Creates a FlightComputer instance, which can only be created once.
     *
     * @param args arguments to configure FightComputer settings
     * @return new FlightComputer instance
     */
    public static FlightComputer create(String[] args) {
        if (instance != null) {
            throw new RuntimeException("FlightComputer has already been created.");
        }
        instance = new FlightComputer(args);
        return instance;
    }

    /**
     * Convenient method to create a FlightComputer instance without any arguments.
     *
     * @return new FlightComputer instance
     */
    public static FlightComputer create() {
        return create(new String[0]);
    }

    /**
     * Time used by all objects created in the FC.
     */
    private final Time time;

    /**
     * Looper used by objects created in the FC, e.g. subsystems.
     */
    private final Looper looper;

    private PacketRouter packetRouter = new PacketRouter();
    private Telemetry telemetry = new Telemetry(Logger.getLogger("Telemetry"), packetRouter);

    /**
     * Providers to provide singleton objects to higher level management objects.
     */
    private SensorProvider sensorProvider;

    /**
     * Private FlightComputer constructor to avoid multiple initializations.
     *
     * @param args arguments to configure FlightComputer settings
     */
    private FlightComputer(String[] args) {
        this.time = new Time();
        this.looper = new Looper(this.time);
        initWithArgs(args);
    }

    /**
     * Initialize input/output devices and other settings based on arguments.
     *
     * @param args arguments for configuration
     */
    private void initWithArgs(String[] args) {
        CommandLine cmd = parseArgs(args);

        // use real/fake sensors
        boolean useRealSensors = cmd.hasOption(REAL_SENSORS);
        sensorProvider = SensorProvider.create(useRealSensors);
    }

    /**
     * Defines command line options and possible arguments.
     * Also prints help and exits if invalid arguments are detected.
     *
     * @param args arguments to parse
     * @return command line options for configuration
     */
    private CommandLine parseArgs(String[] args) {
        Options options = new Options();

        Option sensorType = new Option(null, REAL_SENSORS, false, REAL_SENSORS_DESC);
        options.addOption(sensorType);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Base11-FC", options);
            System.exit(1);
        }
        return null;
    }

    /**
     * Allows subsystems to register events and callbacks with the looper.
     *
     * @param subsystem subsystem to register
     */
    public void registerSubsystem(Subsystem subsystem) {
        subsystem.prepare(this.looper);
    }

    /**
     * After all input/output devices and settings are initialized, higher level objects can be created.
     * Use objects provided by the providers, and simply inject all objects needed to create any higher level object.
     */
    public void initHighLevelObjects() {

        // PacketRouter
        PacketRouter packetRouter = new PacketRouter();

        // Telemetry
        Telemetry telemetry = new Telemetry(Logger.getLogger("Telemetry"), packetRouter);

        telemetry.logInfo(Info.INIT_SUBSYSTEMS_START);

        // ParachuteSubsystem
        registerSubsystem(new ParachuteSubsystem(null, null, null, null));

        // SensorSubsystem
        SensorSubsystem sensorSubsystem = new SensorSubsystem(this.time);
        // add sensors
        telemetry.logInfo(Info.DONE_CREATING_SENSORS);
        registerSubsystem(sensorSubsystem);

        // SCMCommandSubsystem
        registerSubsystem(new SCMCommandSubsystem()); // TODO: should listen to PacketRouter

        // ValveStateSubsystem
        packetRouter.addListener(new ValveStateSubsystem(packetRouter), SCMPacket.class,
                PacketSources.EngineControllerUnit);

        telemetry.logInfo(Info.FINISH_SUBSYSTEM_START);
    }

    public void tick() {
        this.looper.tick((tag, from, exception) -> {
            try {
                this.telemetry.reportError(Errors.TOP_LEVEL_EXCEPTION);
            } catch (Exception e) {
                System.err.println("Unable to log errors!");
                e.printStackTrace();
            }
        });
    }

    /**
     * Builder class to create FlightComputer objects with more customization.
     */
    public static class Builder {
        private Telemetry telemetry;
        private final FlightComputer fc;

        /**
         * Creates a new Builder instance, which can be used to customize the FC.
         *
         * @param args arguments to configure FlightComputer settings.
         */
        public Builder(String[] args) {
            fc = FlightComputer.create(args);
        }

        /**
         * Convenient method to create a Builder instance without any arguments.
         */
        public Builder() {
            this(new String[0]);
        }

        /**
         * Set custom telemetry.
         *
         * @param telemetry customized telemetry
         * @return this Builder
         */
        public Builder withTelemetry(Telemetry telemetry) {
            this.telemetry = telemetry;
            return this;
        }

        /**
         * Customizations set programmatically in the Builder will override defaults and arguments.
         * @return customized FlightComputer
         */
        public FlightComputer build() {
            if (telemetry != null) fc.telemetry = this.telemetry;
            return fc;
        }
    }
}
