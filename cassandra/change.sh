#!/bin/sh


umount /etc/hosts
weaveip=$(ifconfig ethwe | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}')
dockerip=$(ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}')

num=`ifconfig|grep ethwe|wc -l`

if [ $num -eq 1 ]; then
  sed -i 's/$dockerip/$weaveip/' /etc/hosts
  /usr/bin/supervisord -n
else
  /usr/bin/supervisord -n
fi

