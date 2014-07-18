#!/bin/bash
sed -i "s/CASSANDRAHOSTNAME/${CASSANDRAHOSTNAME}/" /opt/tomcat/webapps/ROOT/WEB-INF/classes/ACMEAIR_WEBAPP-docker.properties
sed -i "s/EUREKAHOSTNAME/${EUREKAHOSTNAME}/" /opt/tomcat/webapps/ROOT/WEB-INF/classes/ACMEAIR_WEBAPP-docker.properties
/opt/tomcat/bin/catalina.sh run
