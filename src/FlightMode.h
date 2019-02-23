/**
 *  The state of the rocket
 *  Sitting   - On the launch pad but no ignition yet
 *  Burn      - The main engien is on and we are moving!
 *  Coasting  - The main engien has been turned off and we are coasting upwards
 *  Apogee    - We are close to apogee (settings determin how close)
 *  Falling   - After apogee we are going down
 *  Landed    - We have hit the ground
 */
enum FlightMode {Sitting, Burn, Coasting, Apogee, Falling, Landed};

/**
 * gets the current flight mode for the rocket
 * @return the flight mode which the rocket is in
 */
enum FlightMode getFlightMode();

/**
 * The task funtion to run all the computation necessary for the flight mode
 */
void FlightModeTask();

/**
 * Initializes the flight mode subsystem
 */
void InitFlightMode();

/**
 * Waits until the given flight mode has been reached.
 * @param FlightMode the flight mode to wait for
 */
void waitForFlightMode(enum FlightMode);
