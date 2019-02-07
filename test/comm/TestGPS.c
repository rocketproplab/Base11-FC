#include "comm/GPS.c"
#include <CUnit/Basic.h>
#include <stdbool.h>
#include <stdio.h>
#include "../TestConstants.h"

void test_decodeNEMA(){
  GPSInfo info   = {0};
  GPSDebug debug = {0};
  char* nema     =
    "$GPGGA,172814.0,3723.46587704,N,12202.26957864,W,2,6,1.2,18.893,M,-25.669,M,2.0,0031*4F\0";

  decodeNEMA(nema, &info, &debug);
  CU_ASSERT_DOUBLE_EQUAL(172814, info.t_b, EPSILON);
  CU_ASSERT_DOUBLE_EQUAL(3723.46587704, info.lat, EPSILON);
  CU_ASSERT_DOUBLE_EQUAL(12202.26957864, info.lon, EPSILON);
  CU_ASSERT_DOUBLE_EQUAL(18.893, info.alt, EPSILON);
  CU_ASSERT_EQUAL(6, debug.sVCount);

  nema = "$GPGGA,420,-32,N,7,W,2,12,1.2,100000,M,-25.669,M,2.0,0031*4F\0";

  decodeNEMA(nema, &info, &debug);
  CU_ASSERT_DOUBLE_EQUAL(420, info.t_b, EPSILON);
  CU_ASSERT_DOUBLE_EQUAL(-32, info.lat, EPSILON);
  CU_ASSERT_DOUBLE_EQUAL(7, info.lon, EPSILON);
  CU_ASSERT_DOUBLE_EQUAL(100000, info.alt, EPSILON);
  CU_ASSERT_EQUAL(12, debug.sVCount);
}


int testGPS(){
  CU_pSuite pSuite = NULL;

  /* initialize the CUnit test registry */
  if(CUE_SUCCESS != CU_initialize_registry())
    return CU_get_error();

  /* add a suite to the registry */
  pSuite = CU_add_suite("GPS Suite", NULL, NULL);
  if(NULL == pSuite){
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* add the tests to the suite */
  if(NULL == CU_add_test(pSuite, "Test Decode NEMA", test_decodeNEMA)){
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* Run all tests using the CUnit Basic interface */
  CU_basic_set_mode(CU_BRM_VERBOSE);
  CU_basic_run_tests();
  CU_cleanup_registry();
  return CU_get_error();

}
