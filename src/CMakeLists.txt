cmake_minimum_required(VERSION 3.2)
project(Base11FC)

file(GLOB SOURCE_FILES *.c comm/*.c)

add_executable(Base11FC ${SOURCE_FILES})
add_library(Base11FC_lib STATIC ${SOURCE_FILES})

set(LIBWIREINGPI_DIR ${PROJECT_SOURCE_DIR}/../WiringPi/wiringPi)

target_link_libraries(Base11FC
  ${LIBWIREINGPI_DIR}/libwiringPi.so.2.46
)

install(TARGETS Base11FC DESTINATION ${BASE11_FC_INSTALL_BIN_DIR})
