#!/bin/bash
sed -i "s/EUREKAHOSTNAME/${EUREKAHOSTNAME}/" /opt/tomcat/webapps/ROOT/WEB-INF/classes/zuul-docker.properties
/opt/tomcat/bin/catalina.sh run
