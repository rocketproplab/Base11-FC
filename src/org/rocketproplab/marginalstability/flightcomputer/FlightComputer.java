package org.rocketproplab.marginalstability.flightcomputer;

import org.apache.commons.cli.*;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketRouter;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketSources;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.*;

import java.util.logging.Logger;

public class FlightComputer {
    private static FlightComputer instance;

    public static FlightComputer create(String[] args) {
        if (instance != null) {
            throw new RuntimeException("FlightComputer has already been created.");
        }
        instance = new FlightComputer(args);
        return instance;
    }

    public static FlightComputer create() {
        return create(new String[0]);
    }

    private final Time time;
    private final Looper looper;

    private PacketRouter packetRouter = new PacketRouter();
    private Telemetry telemetry = new Telemetry(Logger.getLogger("Telemetry"), packetRouter);
    private SensorProvider sensorProvider;

    // TODO: all the arguments
    private FlightComputer(String[] args) {
        this.time = new Time();
        this.looper = new Looper(this.time);
        initWithArgs(args);
    }

    private void initWithArgs(String[] args) {
        // TODO: initialize all input/output devices based on arguments
        CommandLine cmd = parseArgs(args);

        // use real/fake sensors
        boolean useRealSensors = cmd.hasOption("real-sensors");
        sensorProvider = SensorProvider.create(useRealSensors);
    }

    private CommandLine parseArgs(String[] args) {
        Options options = new Options();

        Option sensorType = new Option(null, "real-sensors", false, "use real sensors");
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

    public void registerSubsystem(Subsystem subsystem) {
        subsystem.prepare(this.looper);
    }

    public void initHighLevelObjects() {
        // TODO: after all input/output devices are initialized, create higher level objects using these basic devices
        // TODO: use local variables; simply inject all the objects needed to create any higher level object

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

    public static class Builder {
        private Telemetry telemetry;
        private final FlightComputer fc;

        public Builder(String[] args) {
            fc = FlightComputer.create(args);
        }

        public Builder() {
            this(new String[0]);
        }

        public Builder withTelemetry(Telemetry telemetry) {
            this.telemetry = telemetry;
            return this;
        }

        public FlightComputer build() {
            if (telemetry != null) fc.telemetry = this.telemetry;
            return fc;
        }
    }
}
