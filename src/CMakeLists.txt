cmake_minimum_required(VERSION 3.2)
project(Base11FC)

set(SOURCE_FILES main.c)

add_executable(Base11FC ${SOURCE_FILES})

install(TARGETS Base11FC DESTINATION ${BASE11_FC_INSTALL_BIN_DIR})
