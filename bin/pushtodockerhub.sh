#!/bin/sh
docker tag acmeair/microscaler haishwu/acmeair-microscaler:1.0.0
docker push haishwu/acmeair-microscaler:1.0.0
docker tag acmeair/microscaler-agent haishwu/acmeair-microscaler-agent:1.0.0
docker push haishwu/acmeair-microscaler-agent:1.0.0
docker tag acmeair/webapp-liberty haishwu/acmeair-webapp-liberty:1.0.0
docker push haishwu/acmeair-webapp-liberty:1.0.0    
docker tag acmeair/auth-service-liberty haishwu/acmeair-auth-service-liberty:1.0.0
docker push haishwu/acmeair-auth-service-liberty:1.0.0
docker tag acmeair/asgard haishwu/acmeair-asgard:1.0.0
docker push haishwu/acmeair-asgard:1.0.0       
docker tag acmeair/webapp haishwu/acmeair-webapp:1.0.0
docker push haishwu/acmeair-webapp:1.0.0    
docker tag acmeair/auth-service haishwu/acmeair-auth-service:1.0.0
docker push haishwu/acmeair-auth-service:1.0.0     
docker tag acmeair/eureka haishwu/acmeair-eureka:1.0.0
docker push haishwu/acmeair-eureka:1.0.0     
docker tag acmeair/zuul haishwu/acmeair-zuul:1.0.0            
docker push haishwu/acmeair-zuul:1.0.0
docker tag acmeair/tomcat haishwu/acmeair-tomcat:1.0.0         
docker push haishwu/acmeair-tomcat:1.0.0
docker tag acmeair/loader haishwu/acmeair-loader:1.0.0             
docker push haishwu/acmeair-loader:1.0.0
docker tag acmeair/cassandra haishwu/acmeair-cassandra:1.0.0          
docker push haishwu/acmeair-cassandra:1.0.0
docker tag acmeair/base haishwu/acmeair-base:1.0.0
docker push haishwu/acmeair-base:1.0.0               
docker tag acmeair/liberty haishwu/acmeair-liberty:1.0.0         
docker push haishwu/acmeair-liberty:1.0.0
docker tag acmeair/ibmjava haishwu/acmeair-ibmjava:1.0.0             
docker push haishwu/acmeair-ibmjava:1.0.0
docker tag acmeair/pwgen haishwu/acmeair-pwgen:1.0.0
docker push haishwu/acmeair-pwgen:1.0.0             
docker tag ubuntu haishwu/ubuntu
docker push haishwu/ubuntu:1.0.0
docker tag skydock haishwu/skydock
docker push haishwu/skydock
