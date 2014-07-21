#!/bin/sh

CASSANDRAHOSTNAME=$1
EUREKAHOSTNAME=$2

# Start auth-service
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d "{\"id\": \"mesos-acmeair-auth-service\", \"cmd\": \"docker run -e \\\"CASSANDRAHOSTNAME=${CASSANDRAHOSTNAME}\\\" -e \\\"EUREKAHOSTNAME=${EUREKAHOSTNAME}\\\" aspyker/mesos-acmeair-auth-service\", \"instances\": 2, \"mem\": 128, \"cpus\": 0.25}"

# Start webapp
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d "{\"id\": \"mesos-acmeair-webapp\", \"cmd\": \"docker run -e \\\"CASSANDRAHOSTNAME=${CASSANDRAHOSTNAME}\\\" -e \\\"EUREKAHOSTNAME=${EUREKAHOSTNAME}\\\" aspyker/mesos-acmeair-webapp\", \"instances\": 2, \"mem\": 128, \"cpus\": 0.25}"

# Start zuul
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d "{\"id\": \"mesos-acmeair-zuul\", \"cmd\": \"docker run -name zuul -p=80:80 -e \\\"EUREKAHOSTNAME=${EUREKAHOSTNAME}\\\" aspyker/mesos-acmeair-zuul\", \"instances\": 1, \"mem\": 128, \"cpus\": 0.25}"
