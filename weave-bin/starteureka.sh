#!/bin/sh

. ./env.sh

weave run --with-dns \
$dns_search \
-it -P \
--name eureka -h eureka.eureka.local.flyacmeair.net \
acmeair/eureka
