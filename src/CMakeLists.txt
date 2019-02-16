cmake_minimum_required(VERSION 3.2)
project(Base11FC)

file(GLOB SOURCE_FILES *.c comm/*.c)

add_executable(Base11FC ${SOURCE_FILES})
add_library(Base11FC_lib STATIC ${SOURCE_FILES})

set(LIBWIREINGPI_DIR ${PROJECT_SOURCE_DIR}/../WiringPi/wiringPi)
if(EXISTS ${LIBWIREINGPI_DIR}/libwiringPi.so.2.46 AND NOT EXISTS ${LIBWIREINGPI_DIR}/libwiringPi.so)
  file(RENAME ${LIBWIREINGPI_DIR}/libwiringPi.so.2.46 ${LIBWIREINGPI_DIR}/libwiringPi.so)
endif()

target_link_libraries(Base11FC
  -L${LIBWIREINGPI_DIR}/
  -lwiringPi
  -lpthread
  -lm
  -lrt
  -lcrypt
)

install(TARGETS Base11FC DESTINATION ${BASE11_FC_INSTALL_BIN_DIR})
