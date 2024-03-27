[![Build, Push and Deploy](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/deployment.yml/badge.svg)](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/deployment.yml)
# Compass 🧭
Compass is a web application for the Stadtmuur organization, which allows the participants to record their working hours, track their mood, track exceptional incidents, create daily reports and visualize this information.

For more information navigate to our [wiki](https://github.com/ZHAW-PM4-Compass/compass/wiki).

## Local Setup
... 


# Docker Commands
### Build Image
```console
docker build -t compass .
```
### Run Image
```console
docker run -e CLERK_SECRET_KEY=<CLERK_SECRET_KEY> -p 3000:3000 -p 8080:8080 --name compass compass
```
