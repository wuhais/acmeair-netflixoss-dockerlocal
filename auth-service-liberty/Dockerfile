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
FROM acmeair/liberty

RUN mkdir -p /opt/ibm/wlp/usr/servers/defaultServer/apps/auth-service.war
ADD acmeair-auth-service-tc7-0.1.0-SNAPSHOT.war /opt/ibm/wlp/usr/servers/defaultServer/apps/auth-service.war/
RUN cd /opt/ibm/wlp/usr/servers/defaultServer/apps/auth-service.war/ &&\
  jar xf acmeair-auth-service-tc7-0.1.0-SNAPSHOT.war &&\
  rm *.war

ADD ACMEAIR_AUTH_SERVICE-docker.properties /opt/ibm/wlp/usr/servers/defaultServer/apps/auth-service.war/WEB-INF/classes/ACMEAIR_AUTH_SERVICE-docker.properties
ADD config.properties /opt/ibm/wlp/usr/servers/defaultServer/apps/auth-service.war/WEB-INF/classes/config.properties

ADD server.xml /opt/ibm/wlp/usr/servers/defaultServer/server.xml
ADD jvm.options /opt/ibm/wlp/usr/servers/defaultServer/jvm.options

EXPOSE 80

CMD ["/usr/bin/supervisord", "-n"]

