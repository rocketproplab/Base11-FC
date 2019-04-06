# Base 11 Flight Computer

This is the repository which houses the code for RPL's flight computer.

## Getting Started

### Building

The build system is currently integrated with the Eclipse development
environment. As a result to work on the project first clone the project into
an existing workspace and then create a new project of the same name as the
newly cloned folder. Remember to add the JUnit library and you should be good
to go.

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
