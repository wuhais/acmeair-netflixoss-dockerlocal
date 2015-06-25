#!/bin/sh

ping -c 10 cassandra1.cassandra.local.flyacmeair.net
/opt/cassandra/bin/cqlsh -f /opt/acmeair-loader/bin/acmeair-cql.txt cassandra1.cassandra.local.flyacmeair.net
cd /opt/acmeair-loader/bin
./acmeair-loader
