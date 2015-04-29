what is this?

acmeair* - acmeair projects that came from acmeair github trunk
acmeair-auth-service - the customersession methods from customerservice moved to a separate REST service
acmeair-webapp - changed calls to customersession methods to be REST calls instead of local java calls

how to build?

get a 8.6.0.1 objectgrid.jar and install into maven under the referenced artifact id/version
.\gradlew clean war
if you want to work in eclipse do .\gradlew cleanEclipse eclipse, and then import the projects

how to run?

start with WXS 860 and copy gettingstarted example to some other directory and use the *.xml from acmeair-services-wxs\src\main\resources for grid/map definition

To load the database:
.\gradlew run

copy acmeair-webapp\build\libs\*.war to one tomcat instance
copy acmeair-auth-service\build\libs\*.war to another tomcat instance (not it can't be the same as WXS XIO fails when tow apps both connect to WXS), the code currently assumes the webapp starts on port 8080 and the auth service on port 9080

connect to http://localhost:portoffirstomcatinstance/acmeair-webapp-0.1.0-SNAPSHOT/
run the workload as normal

build netflix's hysterix-dashboard from github
cd hystrix-dashboard
..\gradlew jettyRun

load http://localhost:7979/hystrix-dashboard
add the following link to monitoring in that webapp
http://localhost:portoffirstomcatinstance/acmeair-webapp-0.1.0-SNAPSHOT/hystrix.stream

"load test" to create metrics in hystrix:
for /L %n in (1,0,10) do curl -X POST -d "login=uid0@email.com&password=password" http://localhost:8080/acmeair-webapp-0.1.0-SNAPSHOT/rest/api/login

----

Now on EC2

Startup up acmeair-eureka instance
- I have this set to auto start tomcat on boot
- (1) Need to get the private ip of this instance after it starts to adjust all of the references in acmeair-web and acmeair-auth-service instances
- Once it boots, make sure http://ec2-X-X-X-X.compute-1.amazonaws.com/eureka is accessible

Startup acmeair-wxs instance
- ssh into instance, sudo su, screen
- in one screen, cd /opt/ObjectGrid/acmeair-netflixtech/, ./runcat.sh
- in one screen, cd /opt/ObjectGrid/acmeair-netflixtech/, export M2_REPO=/root/.m2/repository/, ./runcontainer.sh c0
- to load data into WXS, in one screen
-- export M2_REPO=/root/.m2/repository/
-- cd /opt/acmeair-netflixtech/
-- edit the ip address to match this instances private IP in acmeair-services-wxs/src/main/resources/spring-config-acmeair-data-wxs-direct-notx.xml
-- edit the ip address to match this instances private IP in acmeair-services-wxs/src/main/resources/spring-config-acmeair-data-wxs-direct.xml
-- ./gradlew run (this will repackage the acmeair-services-wxs jar and then run the loader program)
- in that last screen check to see if things loaded correctly, cd /opt/ObjectGrid/acmeair-netflixtech/, ../bin/xscmd.sh -c showMapSizes
- (2) Need to get this private ip of this instance after it starts to adjust all of the references in acmeair-web and acmeair-auth-service instances

Change acmeair-dynamic-hostnames-lookup-server private ips
- until we get Eureka deployed HA and behind an elastic IP
- ssh into this instance and edit /var/www/acmeair-env
- the below two instances download this file from the static IP of this instance (assuming it never gets shut down) to know where Eureka and the WXS catalog servers are 

Startup acmeair-auth-service-XX instances
- the below is now not needed with the Asgard images, just start asgard and size up the existing ASG
- ssh into instance, sudo su
-- cd /opt/apache-tomcat-7.0.37/webapps/
-- rm -rf acmeair-auth-service-0.1.0-SNAPSHOT
-- unzip acmeair-auth-service-0.1.0-SNAPSHOT.war *.properties
-- edit WEB-INF/classes/acmeair-auth-service.properties
--- fix eureka url to match private dns name from (1)
-- zip acmeair-auth-service-0.1.0-SNAPSHOT.war WEB-INF/classes/acmeair-auth-service.properties
-- unzip acmeair-auth-service-0.1.0-SNAPSHOT.war WEB-INF/lib/acmeair-services-wxs-0.1.0-SNAPSHOT.jar
-- cd WEB-INF/lib
-- unzip acmeair-services-wxs-0.1.0-SNAPSHOT.jar spring*.xml
-- edit spring-config-acmeair-data-wxs-direct.xml
--- fix the gridConnectString to match private ip from (2)
-- zip acmeair-services-wxs-0.1.0-SNAPSHOT.jar spring-config-acmeair-data-wxs-direct.xml
-- cd ../..
-- zip acmeair-auth-service-0.1.0-SNAPSHOT.war WEB-INF/lib/acmeair-services-wxs-0.1.0-SNAPSHOT.jar
-- rm -rf WEB-INF
-- cd ..
-- bin/startup.sh
- see if instance is working by loading http://ec2-X-X-X-X.compute-1.amazonaws.com:8080/acmeair-auth-service-0.1.0-SNAPSHOT/
-- loading this url should force karyon to start and eureka registration, so check eureka console to see if it showing up
-- clicking the link in the url (to hit the REST service) will cause the WXS connection to occur, so check log files to see if it connected ok

Startup acmeair-webapp-XX instances
- the below is now not needed with the Asgard images, just start asgard and size up the existing ASG
- ssh into instance, sudo su
-- cd /opt/apache-tomcat-7.0.37/webapps/
-- rm -rf acmeair-webapp-0.1.0-SNAPSHOT
-- unzip acmeair-webapp-0.1.0-SNAPSHOT.war *.properties
-- edit WEB-INF/classes/acmeair-webapp.properties
--- fix eureka url to match private dns name from (1)
-- zip acmeair-webapp-0.1.0-SNAPSHOT.war WEB-INF/classes/acmeair-webapp.properties
-- unzip acmeair-webapp-0.1.0-SNAPSHOT.war WEB-INF/lib/acmeair-services-wxs-0.1.0-SNAPSHOT.jar
-- cd WEB-INF/lib
-- unzip acmeair-services-wxs-0.1.0-SNAPSHOT.jar spring*.xml
-- edit spring-config-acmeair-data-wxs-direct.xml
--- fix the gridConnectString to match private ip from (2)
-- zip acmeair-services-wxs-0.1.0-SNAPSHOT.jar spring-config-acmeair-data-wxs-direct.xml
-- cd ../..
-- zip acmeair-webapp-0.1.0-SNAPSHOT.war WEB-INF/lib/acmeair-services-wxs-0.1.0-SNAPSHOT.jar
-- rm -rf WEB-INF
-- cd ..
-- bin/startup.sh
- see if instance is working by loading http://ec2-X-X-X-X.compute-1.amazonaws.com:8080/acmeair-webapp-0.1.0-SNAPSHOT/
-- loading this url should force karyon to start and eureka registration, so check eureka console to see if it showing up
-- using the app will cause the WXS connection to occur, so check log files to see if it connected ok
-- see if app works ok
- (3) Need to get this public and private ip of this instance after it starts to add to hystrix and to adjust all of the references in Jmeter jmx files

Startup acmeair-driver instance
- ssh into instance, sudo su
-- cd /opt/acmeair-netflixtech/acmeair-driver/src/main/scripts
-- remove old logs by rm *1.*
-- edit AcmeAir.jmx
--- ensure the CONTEXT_ROOT is set appropriately (/acmeair-webapp-0.1.0-SNAPSHOT)
--- ensure the HTTPSampler.port is set appropriately (8080)
-- edit hosts.cvs
--- ensure the IP address of the web app is set appropriately from (3)
-- %ACMEAIR_SRCDIR%/bin/jmeter -n -t AcmeAir.jmx -j AcmeAir1.log -l AcmeAir1.jtl

Get hystrix dashboard going
- get and compile Netflix OSS Hystrix project
- then run the local hystrix dashboard
-- .\gradlew :hystrix-dashboard:jettyRun
- load the dashboard
-- http://localhost:7979/hystrix-dashboard
-- add the hystrix dashboard of the acmeair-webapp application using it's public ip from (3)
--- http://ec2-X-X-X-X.compute-1.amazonaws.com:8080/acmeair-webapp-0.1.0-SNAPSHOT/hystrix.stream


===

Now, update hosts on dynamic-hostnames-lookup-server
use asgard to startup auth service instances as a cluster/asg
need to copy approach on acmeair-web
need to understand when eureka registration happens as it might take manually hitting a url on each auth service before they register