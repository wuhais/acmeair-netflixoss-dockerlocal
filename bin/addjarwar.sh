#!/bin/sh

cp /root/acmeair-netflix/workspace/acmeair-auth-service-tc7/build/libs/acmeair-auth-service-tc7-0.1.0-SNAPSHOT.war /root/acmeair-netflix/auth-service/
cp /root/acmeair-netflix/workspace/acmeair-auth-service-tc7/build/libs/acmeair-auth-service-tc7-0.1.0-SNAPSHOT.war /root/acmeair-netflix/auth-service-liberty/
cp /root/acmeair-netflix/workspace/acmeair-webapp-tc7/build/libs/acmeair-webapp-tc7-0.1.0-SNAPSHOT.war /root/acmeair-netflix/webapp/
cp /root/acmeair-netflix/workspace/acmeair-webapp-tc7/build/libs/acmeair-webapp-tc7-0.1.0-SNAPSHOT.war /root/acmeair-netflix/webapp-liberty/
cp /root/acmeair-netflix/workspace/acmeair-loader/build/distributions/acmeair-loader-0.1.0-SNAPSHOT.zip /root/acmeair-netflix/loader/
cp /root/acmeair-netflix/workspace/acmeair-services-astyanax/src/main/resources/acmeair-cql.txt /root/acmeair-netflix/loader/

