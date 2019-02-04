cmake_minimum_required(VERSION 3.2)
project(Base11FC_tests)

include_directories(${BASE11_FC_HEADERS_DIR})

set(SOURCE_FILES test_main.c)

add_executable(Base11FC_tests ${SOURCE_FILES})

link_directories(/usr/lib/)

set_target_properties (Base11FC_tests PROPERTIES LINK_FLAGS "-Wall")

target_link_libraries(Base11FC_tests -lcunit)
