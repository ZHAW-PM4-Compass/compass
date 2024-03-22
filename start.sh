#!/bin/bash

# This Script will start front and backend inside the docker container

# go to /backend and run grade run
cd /backend
gradle run &

# go to /frontend and run npm run start
cd /frontend
npm run start &

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?