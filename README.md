# Base 11 Flight Computer

This is the repository which houses the code for RPL's flight computer.

## Building

To build you must install cmake and cunit.

Then create a build directory, run cmake and run make.

```bash
$ mkdir build
$ cd build
$ cmake ..
$ make
```

After compiling there will be two folders in build, src and test. In src the
program executable Base11FC is the Flight Computer program. In test there is
the test executable Base11FC_tests which when run will run all the tests.
