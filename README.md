# Base 11 Flight Computer

This is the repository which houses the code for RPL's flight computer.

## Getting Started

### Building

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
