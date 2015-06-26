#!/bin/sh

. ./env.sh

weave run --with-dns \
-it \
$dns_search \
--name loader -h loader.loader.local.flyacmeair.net \
acmeair/loader 


