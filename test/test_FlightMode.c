#include "FlightMode.c"
#include "Coroutines.h"

void resetFlightMode(){
  memset(&flightState, 0, sizeof ( FlightModeState ));
}

void test_engineStateUpdatesFlightMode(){
  resetFlightMode();
  enum FlightMode flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Sitting);

  EventRaise(ENGINE_ON);
  updateFlightMode();
  flightMode = getFlightMode();

  CU_ASSERT_EQUAL(flightMode, Burn);

  EventRaise(ENGINE_OFF);
  flightModeVelocityUpdate(100+APOGEE_SPEED, 0, 0);
  updateFlightMode();
  flightMode = getFlightMode();

  CU_ASSERT_EQUAL(flightMode, Coasting);
}

void test_velocityTriggersApogee(){
  resetFlightMode();
  enum FlightMode flightMode = getFlightMode();

  EventRaise(ENGINE_ON);
  updateFlightMode();
  flightModeVelocityUpdate(100+APOGEE_SPEED, 0, 0);
  EventRaise(ENGINE_OFF);
  updateFlightMode();

  flightModeVelocityUpdate(100+APOGEE_SPEED, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Coasting);

  flightModeVelocityUpdate(10+APOGEE_SPEED, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Coasting);

  flightModeVelocityUpdate(APOGEE_SPEED/2, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Apogee);
}

void test_velocityIncreastingTriggersFalling(){
  resetFlightMode();
  enum FlightMode flightMode = getFlightMode();

  EventRaise(ENGINE_ON);
  updateFlightMode();
  flightModeVelocityUpdate(100+APOGEE_SPEED, 0, 0);
  EventRaise(ENGINE_OFF);
  updateFlightMode();

  flightModeVelocityUpdate(100+APOGEE_SPEED, 0, 0);
  updateFlightMode();

  flightModeVelocityUpdate(APOGEE_SPEED/2, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Apogee);

  flightModeVelocityUpdate(APOGEE_SPEED*1.5, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Apogee);

  flightModeVelocityUpdate(-APOGEE_SPEED/2, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Apogee);

  flightModeVelocityUpdate(-APOGEE_SPEED*1.5, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Falling);
}

void test_zeroVelocityIndicatesLanded(){
  resetFlightMode();
  enum FlightMode flightMode = getFlightMode();

  EventRaise(ENGINE_ON);
  updateFlightMode();
  flightModeVelocityUpdate(100+APOGEE_SPEED, 0, 0);
  EventRaise(ENGINE_OFF);
  updateFlightMode();

  flightModeVelocityUpdate(100+APOGEE_SPEED, 0, 0);
  updateFlightMode();

  flightModeVelocityUpdate(APOGEE_SPEED/2, 0, 0);
  updateFlightMode();

  flightModeVelocityUpdate(NOISE_SPEED, 0, 0);
  updateFlightMode();

  flightModeVelocityUpdate(-APOGEE_SPEED*1.5, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Falling);

  flightModeVelocityUpdate(NOISE_SPEED, 0, 0);
  updateFlightMode();

  flightMode = getFlightMode();
  CU_ASSERT_EQUAL(flightMode, Landed);
}

int test_FlightMode(){
  CU_pSuite pSuite = NULL;

  /* initialize the CUnit test registry */
  if(CUE_SUCCESS != CU_initialize_registry())
    return CU_get_error();

  /* add a suite to the registry */
  pSuite = CU_add_suite("FlightMode Suite", NULL, NULL);
  if(NULL == pSuite){
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* add the tests to the suite */
  if(NULL == CU_add_test(pSuite, "Test Flight Mode Engine",
                 test_engineStateUpdatesFlightMode)){
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* add the tests to the suite */
  if(NULL == CU_add_test(pSuite, "Test Velocity Apogee",
                 test_velocityTriggersApogee)){
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* add the tests to the suite */
  if(NULL == CU_add_test(pSuite, "Test Velocity Falling",
                 test_velocityIncreastingTriggersFalling)){
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* add the tests to the suite */
  if(NULL == CU_add_test(pSuite, "Test Velocity Zero is Landed",
                 test_zeroVelocityIndicatesLanded)){
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* Run all tests using the CUnit Basic interface */
  CU_basic_set_mode(CU_BRM_VERBOSE);
  CU_basic_run_tests();
  CU_cleanup_registry();
  return CU_get_error();
}
