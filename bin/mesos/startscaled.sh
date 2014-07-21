#!/bin/sh

CASSANDRAHOSTNAME=$1
EUREKAHOSTNAME=$2

# Start auth-service
echo curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d "{\"id\": \"mesos-acmeair-auth-service\", \"cmd\": \"docker run -e \"CASSANDRAHOSTNAME=${CASSANDRAHOSTNAME}\" -e \"EUREKAHOSTNAME=${EUREKAHOSTNAME}\" aspyker/mesos-acmeair-auth-service\", \"instances\": 1, \"mem\": 128, \"cpus\": 0.25}"

# Start webapp
echo curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d "{\"id\": \"mesos-acmeair-webapp\", \"cmd\": \"docker run -e \"CASSANDRAHOSTNAME=${CASSANDRAHOSTNAME}\" -e \"EUREKAHOSTNAME=${EUREKAHOSTNAME}\" aspyker/mesos-acmeair-webapp\", \"instances\": 1, \"mem\": 128, \"cpus\": 0.25}"

# Start zuul
echo curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d "{\"id\": \"mesos-acmeair-zuul\", \"cmd\": \"docker run -e \"EUREKAHOSTNAME=${EUREKAHOSTNAME}\" aspyker/mesos-acmeair-auth-service\", \"instances\": 1, \"mem\": 128, \"cpus\": 0.25}"
