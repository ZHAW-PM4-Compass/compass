#!/bin/bash

# Start the backend in the background
java -jar ./backend/app.jar &

# Start the frontend
npm start --prefix ./