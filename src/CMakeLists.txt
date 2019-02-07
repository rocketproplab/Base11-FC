cmake_minimum_required(VERSION 3.2)
project(Base11FC)

set(SOURCE_FILES main.c)

add_executable(Base11FC ${SOURCE_FILES})

set(LIBSERIALPORT_DIR ${PROJECT_SOURCE_DIR}/../libserialport)

target_link_libraries(Base11FC
  ${LIBSERIALPORT_DIR}/serialport.o
  ${LIBSERIALPORT_DIR}/linux.o
  ${LIBSERIALPORT_DIR}/linux_termios.o
)

install(TARGETS Base11FC DESTINATION ${BASE11_FC_INSTALL_BIN_DIR})
