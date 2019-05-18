# Requirements
* Java 1.8
* Docker 1.13.0+
* Docker compose 1.10.0+

# Installation
1. Open terminal in project folder
2. Run `docker-compose up`
3. Install project dependencies by running `gradlew` (Linux) or `gradlew.bat` (Windows)
4. Run the application

# Windows Home Edition
If you use Windows Home Edition, you will not be able to install the latest docker. In this case install Docker Toolbox for Windows. 
You may need to changed the mongo host to docker VM host in configuration file.

The latest version of Docker Toolbox can be downloaded here: https://docs.docker.com/toolbox/overview/