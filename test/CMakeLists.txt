cmake_minimum_required(VERSION 3.2)
project(Base11FC_tests)

include_directories(${BASE11_FC_HEADERS_DIR} ${PROJECT_SOURCE_DIR}/test/)

set(SOURCE_FILES test_main.c)

add_executable(Base11FC_tests ${SOURCE_FILES})

link_directories(/usr/lib/ ${LIBWIREINGPI_DIR}/)

set_target_properties (Base11FC_tests PROPERTIES LINK_FLAGS "-Wall")

set(LIBWIREINGPI_DIR ${PROJECT_SOURCE_DIR}/../WiringPi/wiringPi)

target_link_libraries(Base11FC_tests
  Base11FC_lib
  -L${LIBWIREINGPI_DIR}/
  -lwiringPi
  -lcunit
  -lpthread
  -lm
  -lrt
  -lcrypt
)
