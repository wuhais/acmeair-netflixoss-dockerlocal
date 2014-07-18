#!/bin/sh

export CASSANDRA_ENDPOINT=$1

/opt/cassandra/bin/cqlsh -f /opt/acmeair-loader/bin/acmeair-cql.txt ${CASSANDRA_ENDPOINT}

cd /opt/acmeair-loader/bin
export JAVA_OPTS=-Dcom.acmeair.cassandra.contactpoint=${CASSANDRA_ENDPOINT}
./acmeair-loader
