# Base 11 Flight Computer

This is the repository which houses the code for RPL's flight computer.

## Getting Started

### Building

To build you must install cmake and cunit. This can be done on Ubuntu by running
```bash
sudo apt install libcunit1-dev cmake
```

After installing cmake and cunit Initialize the wiringPi submodule and build
wiringPi if you can not install wiringPi on your system. Ie if you want to build
on a non Raspberry Pi system.

```bash
git submodule init WiringPi
git submodule update WiringPi
cd WiringPi/wiringPi
make
cd ../..
```

Then you need to build libuev which can be done by
```bash
git submodule init libuev
git submodule update libuev
cd libuev
./autogen.sh
./configure
make
make check
make DESTDIR=`pwd`/.out/ install-strip
```

Then create a build directory, run cmake and run make.

```bash
mkdir build
cd build
cmake ..
make
```

After compiling there will be two folders in build, src and test. In src the
program executable Base11FC is the Flight Computer program. In test there is
the test executable Base11FC_tests which when run will run all the tests.

If you did not install wiringPi then you must add WiringPi/wiringPi/ to your
LD_LIBRARY_PATH which tells Linux where the wiringPi library is.

After building once you can use the watchBuild.sh script to watch for changes
and autobuild and run the tests. watchBuild.sh also automatically allows Linux
to find libwiringPi.so so you do not have to change LD_LIBRARY_PATH if you only
use watchBuild.sh.

### Contributing

We are following the [integration manager workflow]. Therefor you need to fork
the repository and clone your fork following the default procedure.

One you cloned the repo you should add the blessed repository as a remote. To
do this run the following commands.

```bash
$ git remote add blessed https://github.com/rocketproplab/Base11-FC.git
```

You should never push directly to blessed, instead push to your forked repo
and then make pull requests with blessed. Have someone who is not you review
the pull request.



[integration manager workflow]: https://git-scm.com/book/en/v2/Distributed-Git-Distributed-Workflows
