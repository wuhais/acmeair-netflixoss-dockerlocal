#!/bin/sh

. ./env.sh

weave run --with-dns \
$dns_search \
-it -p 80:80 \
--name zuul -h zuul.zuul.local.flyacmeair.net \
acmeair/zuul
