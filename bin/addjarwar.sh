#!/bin/sh

cp ../workspace/acmeair-auth-service-tc7/build/libs/acmeair-auth-service-tc7-0.1.0-SNAPSHOT.war ../auth-service/
cp ../workspace/acmeair-auth-service-tc7/build/libs/acmeair-auth-service-tc7-0.1.0-SNAPSHOT.war ../auth-service-liberty/
cp ../workspace/acmeair-webapp-tc7/build/libs/acmeair-webapp-tc7-0.1.0-SNAPSHOT.war ../webapp/
cp ../workspace/acmeair-webapp-tc7/build/libs/acmeair-webapp-tc7-0.1.0-SNAPSHOT.war ../webapp-liberty/
cp ../workspace/acmeair-loader/build/distributions/acmeair-loader-0.1.0-SNAPSHOT.zip ../loader/
cp ../workspace/acmeair-services-astyanax/src/main/resources/acmeair-cql.txt ../loader/
