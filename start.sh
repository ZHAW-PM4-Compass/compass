#!/bin/bash

# Start the backend in the background
java -jar ./backend/app.jar &

# Start the frontend
npm start --prefix ./ &

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?