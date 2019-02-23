#include "FlightMode.h"

typedef struct FlightModeState {

} FlightModeState;

/**
 * gets the current flight mode for the rocket
 * @return the flight mode which the rocket is in
 */
enum FlightMode getFlightMode(){
  return Sitting;
}

/**
 * The task funtion to run all the computation necessary for the flight mode
 */
void FlightModeTask(){

}

/**
 * Initializes the flight mode subsystem
 */
void InitFlightMode(){

}

/**
 * Waits until the given flight mode has been reached.
 * @param FlightMode the flight mode to wait for
 */
void waitForFlightMode(enum FlightMode flightMode){

}
