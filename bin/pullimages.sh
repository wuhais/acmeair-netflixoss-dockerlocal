#!/bin/sh
docker pull 9.12.234.186:5000/acmeair/microscaler        
docker tag 9.12.234.186:5000/acmeair/microscaler acmeair/microscaler
docker pull 9.12.234.186:5000/acmeair/microscaler-agent   
docker tag 9.12.234.186:5000/acmeair/microscaler-agent acmeair/microscaler-agent 
docker pull 9.12.234.186:5000/acmeair/webapp-liberty 
docker tag 9.12.234.186:5000/acmeair/webapp-liberty acmeair/webapp-liberty     
docker pull 9.12.234.186:5000/acmeair/auth-service-liberty
docker tag 9.12.234.186:5000/acmeair/auth-service-liberty acmeair/auth-service-liberty
docker pull 9.12.234.186:5000/acmeair/asgard    
docker tag 9.12.234.186:5000/acmeair/asgard  acmeair/asgard         
docker pull 9.12.234.186:5000/acmeair/webapp        
docker tag 9.12.234.186:5000/acmeair/webapp acmeair/webapp      
docker pull 9.12.234.186:5000/acmeair/auth-service  
docker tag 9.12.234.186:5000/acmeair/auth-service  acmeair/auth-service     
docker pull 9.12.234.186:5000/acmeair/eureka    
docker tag 9.12.234.186:5000/acmeair/eureka acmeair/eureka         
docker pull 9.12.234.186:5000/acmeair/zuul  
docker tag 9.12.234.186:5000/acmeair/zuul  acmeair/zuul             
docker pull 9.12.234.186:5000/acmeair/tomcat    
docker tag 9.12.234.186:5000/acmeair/tomcat acmeair/tomcat          
docker pull 9.12.234.186:5000/acmeair/loader
docker tag 9.12.234.186:5000/acmeair/loader acmeair/loader              
docker pull 9.12.234.186:5000/acmeair/cassandra 
docker tag 9.12.234.186:5000/acmeair/cassandra acmeair/cassandra          
docker pull 9.12.234.186:5000/acmeair/base
docker tag 9.12.234.186:5000/acmeair/base acmeair/base                
docker pull 9.12.234.186:5000/acmeair/liberty   
docker tag 9.12.234.186:5000/acmeair/liberty acmeair/liberty          
docker pull 9.12.234.186:5000/acmeair/ibmjava 
docker tag 9.12.234.186:5000/acmeair/ibmjava acmeair/ibmjava             
docker pull 9.12.234.186:5000/acmeair/pwgen 
docker tag 9.12.234.186:5000/acmeair/pwgen acmeair/pwgen              
docker pull 9.12.234.186:5000/ubuntu    
docker tag 9.12.234.186:5000/ubuntu ubuntu                   
docker pull 9.12.234.186:5000/crosbymichael/skydock
docker tag 9.12.234.186:5000/crosbymichael/skydock crosbymichael/skydock   
docker pull 9.12.234.186:5000/crosbymichael/skydns
docker tag 9.12.234.186:5000/crosbymichael/skydns crosbymichael/skydns
