#/bin/sh

. ./env.sh
echo "Launching weave ..."
weave launch -iprange $iprange $iplist
weave launch-dns $dnsrange --domain="local.flyacmeair.net."
sleep 15
echo "Starting cassandra ..."
./addcassandra.sh
sleep 60
echo "Loading data ..."
./runloader.sh
sleep 5
echo "Starting Eureka ..."
./starteureka.sh
sleep 5
echo "Starting zuul ..."
./startzuul.sh
sleep 5
echo "Starting web app ..."
./addwebapp.sh
sleep 5
echo "Starting auth service ..."
./addauthsvc.sh
