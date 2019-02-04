cmake_minimum_required(VERSION 3.2)

set(CMAKE_BUILD_TYPE Debug)

set(CMAKE_INSTALL_PREFIX ${PROJECT_SOURCE_DIR})

set(BASE11_FC_INSTALL_INCLUDE_DIR ${PROJECT_SOURCE_DIR}/include)
set(BASE11_FC_INSTALL_BIN_DIR ${PROJECT_SOURCE_DIR}/bin)
set(BASE11_FC_INSTALL_LIB_DIR ${PROJECT_SOURCE_DIR}/lib)

set(BASE11_FC_HEADERS_DIR ${PROJECT_SOURCE_DIR}/src)

include_directories(${BASE11_FC_INSTALL_INCLUDE_DIR})
include_directories(${BASE11_FC_HEADERS_DIR})

add_subdirectory(src)
add_subdirectory(test)

enable_testing()
add_test (NAME all COMMAND Base11FC_tests)
