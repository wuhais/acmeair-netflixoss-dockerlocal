## NetflixOSS Acme Air Sample and Benchmark

This application shows an implementation of a fictitious airline called "Acme Air".  The application was built with the some key business requirements: the ability to scale to billions of web API calls per day, the need to develop and deploy the application in public clouds (as opposed to dedicated pre-allocated infrastructure), and the need to support multiple channels for user interaction (with mobile enablement first and browser/Web 2.0 second).  In addition to the original Acme Air application, the application has been changed to leverage the Netflix OSS cloud platform.

## The source code

This is the source code fetched from https://github.com/aspyker/acmeair-netflix/tree/astyanax

## What has been changed in this application for Netflix OSS

This application is based upon the original Acme Air Sample and Benchmark, but has been changed in the following ways to demostrate the Netflix OSS cloud platform:

* Split monolythic implementation into micro-services based architecture (added authentication micro service)
* Added Karyon based bootstrapping in order to expose the management console and automatic registration into Eureka
* Implemented Archaius property based configuration for key dependencies and service registration
* Reimplemented REST calls to use Ribbon (with Eureka Client) for automatic service invocation load balancing
* Wrapped REST calls with Hystrix to provide for fast failure and tolerance of failure in back end services
* (Coming soon) Implemented Cassandra based data service implementation and accessed via Asytanax
* Created [AMI's](http://ispyker.blogspot.com/2013/09/acme-air-netflixoss-amis-for-your.html) for easy deployment

## Repository Contents

Source:

- **acmeair-auth-service** The core code for the authentication micro service
- **acmeair-auth-service-tc7** Deployment artifacts for the Tomcat version of the auth service
- **acmeair-auth-service-wlp** Deployment arfifacts for the WebSphere Liberty Profile version of the auth service
- **acmeair-common** The Java entities used throughout the application
- **acmeair-driver** The workload driver script and supporting classes and resources
- **acmeair-driver-control** A lightweight framework for automating nmon/jmeter performance runs
- **acmeair-loader** A tool to load the Java implementation data store
- **acmeair-services** The Java data services interface definitions
- **acmeair-services-wxs** A WebSphere eXtreme Scale data service implementation
- **acmeair-webapp** The core code for the Web 2.0 application front end web app and associated Java REST services
- **acmeair-webapp-tc7** Deployment artifacts for the Tomcat version of the front end web app
- **acmeair-webapp-wlp** Deployment arfifacts for the WebSphere Liberty Profile version of the front end web app
- **build.gradle** The build system using gradle

## How to get started

* See the [wiki](https://github.com/aspyker/acmeair-netflix/wiki)


## Ask Questions

Questions about the Acme Air Open Source Project can be directed to our Google Groups.  Please indicate that you are working with the Netflix OSS version.

* Acme Air Users: [https://groups.google.com/forum/?fromgroups#!forum/acmeair-users](https://groups.google.com/forum/?fromgroups#!forum/acmeair-users)

## Submit a bug report

We use github issues to report and handle bug reports.

## OSS Contributions

We accept contributions via pull requests.

Please fill in the [appropriate CLA](https://github.com/aspyker/acmeair-netflix/tree/master/CLAs) in order to contribute.
