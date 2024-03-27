[![Build, Push and Deploy](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/ci-cd.yml/badge.svg?branch=main)](https://github.com/ZHAW-PM4-Compass/compass/actions/workflows/ci-cd.yml)
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
docker run 3000:3000 -p 8080:8080 --name compass compass
```
