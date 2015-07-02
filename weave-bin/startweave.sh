#/bin/sh

. ./env.sh

weave launch -iprange $iprange $iplist
weave launch-dns $dnsrange --domain="local.flyacmeair.net."
