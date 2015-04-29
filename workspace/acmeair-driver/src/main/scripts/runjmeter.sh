#!/bin/bash
nohup /opt/apache-jmeter-2.9/bin/jmeter -n -t AcmeAir.jmx -j AcmeAir1.log -l AcmeAir1.jtl >AcmeAir1.stdout 2>AcmeAir1.stderr &
