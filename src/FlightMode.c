#include "FlightMode.h"
#include "Coroutines.h"
#define APOGEE_SPEED 10
#define NOISE_SPEED 0.5

typedef struct FlightModeState {
  enum FlightMode currentMode;
  double verticalVelocity;
} FlightModeState;

static FlightModeState flightState = {0};

void checkForApogee();
void checkFalling();
void checkLanded();
void updateFlightMode();

/**
 * gets the current flight mode for the rocket
 * @return the flight mode which the rocket is in
 */
enum FlightMode getFlightMode(){
  return flightState.currentMode;
}

/**
 * The task funtion to run all the computation necessary for the flight mode
 */
void FlightModeTask(){
  while(1){
    updateFlightMode();
    NextTask();
  }
}

/**
 * Initializes the flight mode subsystem
 */
void InitFlightMode(){}

/**
 * Waits until the given flight mode has been reached.
 * @param flightMode the flight mode to wait for
 */
void waitForFlightMode(enum FlightMode flightMode){}

/**
 * Runs the update and recalculates the flightmode for one step
 */
void updateFlightMode(){
  if(EventCatch(ENGINE_ON)){
    flightState.currentMode = Burn;
  }
  if(EventCatch(ENGINE_OFF)){
    flightState.currentMode = Coasting;
  }

  if(flightState.currentMode == Coasting){
    checkForApogee();
  }

  if(flightState.currentMode == Apogee){
    checkFalling();
  }

  if(flightState.currentMode == Falling){
    checkLanded();
  }
}

/**
 * Checks if the velocity is in requriements for the rocket to be in apogee.
 * That means that the velocity is less than the APOGEE_SPEED.
 */
void checkForApogee(){
  double vel    = flightState.verticalVelocity;
  double absVel = ( vel < 0 ) ? -vel : vel;
  if(absVel <= APOGEE_SPEED){
    flightState.currentMode = Apogee;
  }
}

/**
 * Checks if the velocity is in requriements for the rocket to be falling.
 * That means that the velocity is greather than the APOGEE_SPEED.
 */
void checkFalling(){
  if(flightState.verticalVelocity < -APOGEE_SPEED){
    flightState.currentMode = Falling;
  }
}

/**
 * Checks if the rocket has landed by making sure the veloicty is below a given
 * noise threshold.
 */
void checkLanded(){
  double vel    = flightState.verticalVelocity;
  double absVel = ( vel < 0 ) ? -vel : vel;
  if(absVel <= NOISE_SPEED){
    flightState.currentMode = Landed;
  }
}

/**
 * Called when there is new infromation regarding the velocity of the rocket.
 * @param vertical the vertical speed of the rocket in m/s
 * @param heading the heading 0 is due north 180 is south
 * @param groundSpeed the speed in m/s across the ground
 */
void flightModeVelocityUpdate(double vertical, double heading,
                              double groundSpeed){
  flightState.verticalVelocity = vertical;
}
