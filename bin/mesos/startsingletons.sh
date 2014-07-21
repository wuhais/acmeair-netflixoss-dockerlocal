#!/bin/sh

# Start cassandra
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d '{"id": "mesos-acmeair-cassandra", "cmd": "docker run aspyker/mesos-acmeair-cassandra", "instances": 1, "mem": 128, "cpus": 0.25}'

# Start loader
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d '{"id": "mesos-acmeair-loader", "cmd": "docker run aspyker/mesos-acmeair-loader", "instances": 1, "mem": 128, "cpus": 0.25}'

# Start eureka
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" \
    localhost:8080/v2/apps \
    -d '{"id": "mesos-acmeair-eureka", "cmd": "docker run aspyker/mesos-acmeair-eureka", "instances": 1, "mem": 128, "cpus": 0.25}'
