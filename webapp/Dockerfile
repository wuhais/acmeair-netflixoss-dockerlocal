################################################################################
# Copyright (c) 2014 IBM Corp.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
################################################################################
FROM acmeair/tomcat

RUN cd /opt/tomcat/webapps &&\
  mkdir ROOT &&\
  cd ROOT &&\
  wget -qO app.war https://acmeair.ci.cloudbees.com/job/acmeair-netflix-astyanax/lastSuccessfulBuild/artifact/acmeair-webapp-tc7/build/libs/acmeair-webapp-tc7-0.1.0-SNAPSHOT.war &&\
  jar xf app.war &&\
  rm WEB-INF/lib/ribbon-eureka-*.jar &&\
  rm app.war

# this is a temp patch (for now) that tells ribbon to use ipAddr instead of hostname
ADD ribbon-eureka-0.3.13.jar /opt/tomcat/webapps/ROOT/WEB-INF/lib/ribbon-eureka-0.3.13.jar
ADD ACMEAIR_WEBAPP-docker.properties /opt/tomcat/webapps/ROOT/WEB-INF/classes/ACMEAIR_WEBAPP-docker.properties
ADD config.properties /opt/tomcat/webapps/ROOT/WEB-INF/classes/config.properties
ADD fixaddress_and_start.sh /opt/tomcat/fixaddress_and_start.sh
# overwrite the basic tomcat start script for now to ensure address fixing script is called
ADD tomcat.conf /etc/supervisor/conf.d/tomcat.conf

ENV CATALINA_OPTS -Xmx48m
ENV SERVICE_NAME webapp

CMD ["/usr/bin/supervisord", "-n"]

