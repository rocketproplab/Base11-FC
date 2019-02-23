#ifndef FLIGHT_MODE_GAURD
#define FLIGHT_MODE_GAURD

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
 * @param flightMode the flight mode to wait for
 */
void waitForFlightMode(enum FlightMode flightMode);

/**
 * Called when there is new infromation regarding the velocity of the rocket.
 * @param vertical the vertical speed of the rocket in m/s
 * @param heading the heading 0 is due north 180 is south
 * @param groundSpeed the speed in m/s across the ground
 */
void flightModeVelocityUpdate(double vertical, double heading,
                              double groundSpeed);

#endif
