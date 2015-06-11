#!/bin/sh

dig @172.17.42.1 +short zuul.*.local.flyacmeair.net
dig @172.17.42.1 +short eureka.*.local.flyacmeair.net
dig @172.17.42.1 +short cassandra1.*.local.flyacmeair.net
dig @172.17.42.1 +short webapp1.*.local.flyacmeair.net
dig @172.17.42.1 +short auth1.*.local.flyacmeair.net
