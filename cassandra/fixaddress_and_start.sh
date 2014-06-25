#!/bin/bash
CASS_HOSTNAME=`hostname --ip-address`
sed -i "s/\(- seeds: \"\).*\"/\1${CASS_HOSTNAME}\"/" /opt/cassandra/conf/cassandra.yaml
/opt/cassandra/bin/cassandra -f
