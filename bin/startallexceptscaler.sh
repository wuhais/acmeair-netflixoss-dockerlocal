#!/bin/sh

. ./env.sh

echo "Starting Cassandra"
./addcassandra.sh

echo "Starting Eureka"
./starteureka.sh

echo "Starting Zuul"
./startzuul.sh

echo "Starting Skydns and Skydock"
./startdns.sh
echo "Start data loader"
sleep 15
max_retry=5
for n in `seq 0 1 $max_retry`
do
  if [ $n -eq $max_retry ]; then
    exit 1
  fi
  ./runloader.sh
  if [ $? -eq 0 ]; then
    break;
  fi
  sleep 5
done

echo "Starting authsvc"
./addauthsvc.sh

echo "Starting webapp"
./addwebapp.sh

max_retry=3
for n in `seq 0 1 $max_retry`
do
  if [ $n -eq $max_retry ]; then
    exit 1
  fi
  successDnsNum=`./testdns.sh |awk '{print $1 }'|wc -l`
  if [ $successDnsNum -eq 5 ]; then
    break;
  else
    $docker_cmd ps -a|grep crosbymichael/skydns|awk '{print $1}'|xargs $docker_cmd restart
    $docker_cmd ps -a|grep crosbymichael/skydock|awk '{print $1}'|xargs $docker_cmd restart
  fi
  sleep 5
done
 
