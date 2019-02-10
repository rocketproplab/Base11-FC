#!/bin/bash
if [ ! -f build/ ]; then    #check if the build directory exists
  mkdir -p build            #if not create it and run cmake
  cd build
  cmake ..
else
  cd build                  #if it does exist just change into it
fi
make                        #make to rebuild incase something has changed
if [ $? -eq 0 ]; then
  ./test/Base11FC_tests     #run tests if we built sucessfully
fi
while inotifywait -q -r -e close_write ../src/ ../test/ #check for src update
do
  make                      #when found build
  if [ $? -eq 0 ]; then     #and if the build is succesfull run the tests
    ./test/Base11FC_tests
  fi
done
