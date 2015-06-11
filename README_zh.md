Acmeair-netflix-docker
======================
本说明假定的运行环境为基于Ubuntu trusty boot2docker之上的Docker 1.0.0版(经验证Docker 1.6.2也可)。 

## 系统拓扑图

![topology](images/topology.png)

## 配置
### 允许通过TCP套接字远程API访问Docker守护进程
修改Docker守护进程的启动配置 (`/etc/default/docker`) 如下。在boot2docker上该选项是默认开启的。

```bash
# Use DOCKER_OPTS to modify the daemon startup options.
#DOCKER_OPTS="-dns 8.8.8.8 -dns 8.8.4.4"
DOCKER_OPTS="-H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock"
```

### Docker客户端命令
如需以`sudo` 运行`docker`命令，请修改`bin/env.sh`中的`docker_cmd`变量。

```bash
docker_cmd="sudo docker"
```

### Docker守护进程使用的网桥名称
如果您的Docker守护进程使用的网桥名不是`docker0`，请修改`bin/env.sh`中的`bridge_name`变量。

```bash
bridge_name=docker0
```

## 构建映像
在构建并使用映像前，您必须同意其许可证。

```bash
cd bin
./acceptlicenses.sh
./buildimages.sh
```

在此过程中将生成一组SSH公私钥对，对应的文件是 `bin/id_rsa` 和 `bin/id_rsa.pub`。 私钥可用于通过SSH登录容器实例。如果您希望使用别的公私钥对，请将其复制到`bin`目录，并同样命名为`id_rsa`和`id_rsa.pub`。

## 可启动的容器最小集
`startminimum.sh`命令用于启动一组最小集的容器。该命令将启动SkyDNS、SkyDock、1个Cassandra (cassandra1)、数据加载器(data loader)、Eureka服务器(服务注册中心)、 Zuul(负载均衡器)、Microscaler以及Microscaler代理程序。 将有2个自动扩缩组(ASG)被创建：其一组为认证服务，另一组为Web应用。每个ASG设置为初始容量有1个实例。Microscaler负责启动其中的认证服务和Web应用实例。请留几分钟等候命令结束。

```bash
cd bin
./startminimum.sh
```

## 切换应用服务器
认证服务和Web应用的默认应用服务器是IBM WebSphere Application Server Liberty profile (WLP)。您也可以选择使用Tomcat。选项是`bin/env.sh`中的`appserver`变量。

```bash
# "wlp" for WAS Liberty profile or "tc" for Tomcat
appserver=tc
```

删除和创建自动扩缩组(ASG) 及其加载配置，以运行Tomcat上的认证服务和Web应用。

```bash
cd bin
./deleteasg.sh
./configureasg.sh
./startasg.sh
```

## 增加更多的容器
认证服务和Web应用由Microscaler管理。如果您需要更多实例，请修改自动扩缩组(ASG)的配置。

### Cassandra

```bash
./addcassandra.sh
```

## 停止所有容器并清理环境

```bash
./stopall.sh
```

## 显示容器的IP地址

```bash
./showipaddrs.sh
```

## 登录容器
使用SSH登录容器。除了SkyDNS和SkyDock之外，几乎所有容器都运行有SSH服务。

```bash
ssh -i bin/id_rsa root@172.17.0.5
```

## 快速测试
### Zuul 和Web应用

```bash
./testwebapp.sh
```

或

```bash
./testwebapp.sh 172.17.0.6
```

### 认证服务

```bash
./testauth.sh
```

或

```bash
./testauth.sh 172.17.0.9
```

### Cassandra

```bash
./showcassandrastatus.sh

./showcustomertable.sh
```

## 检查域名解析

```bash
dig @172.17.42.1 +short zuul.*.local.flyacmeair.net
dig @172.17.42.1 +short eureka.*.local.flyacmeair.net
dig @172.17.42.1 +short cassandra1.*.local.flyacmeair.net
dig @172.17.42.1 +short webapp1.*.local.flyacmeair.net
dig @172.17.42.1 +short auth1.*.local.flyacmeair.net
```

## 问题？
请确认您的docker版本与下表一致。有些问题可能是因为您的版本不符而导致的。

```bash
$ docker version
Client version: 1.0.0
Client API version: 1.12
Go version (client): go1.2.1
Git commit (client): 63fe64c
Server version: 1.0.0
Server API version: 1.12
Go version (server): go1.2.1
Git commit (server): 63fe64c
```

PS: 以下版本经试验也可正常运行。

```bash
$ docker version
Client version: 1.6.2
Client API version: 1.18
Go version (client): go1.4.2
Git commit (client): 7c8fca2
OS/Arch (client): linux/amd64
Server version: 1.6.2
Server API version: 1.18
Go version (server): go1.4.2
Git commit (server): 7c8fca2
OS/Arch (server): linux/amd64
```

可能没有开启TCP套接字。检查Docker守护进程的配置选项。

```bash
$ ps -ef | grep docker
root     22320     1  0 14:06 ?        00:01:00 /usr/bin/docker -d -H tcp://0.0.0.0:2735 -H unix://var/run/docker.sock
```

您的防火墙可能阻塞了容器与Docker守护进程之间的通信。请检查您的防火墙规则。

SkyDock可能没有正确工作。请尝试重启`skydock`。SkyDock在启动时会登记所有正在运行中的容器。您无需重启其它容器。

```bash
docker restart skydock
```

Docker映像可能与您假定的不同。请用以下命令清理本地映像。注意：该命令会停止全部容器并删除所有的容器和映像。

```bash
docker rm -f `docker ps -qa`
docker rmi `docker images -q`
```

## 软件版本
|Image|Name|Version|Format|Source|
|-----|----|------|------|-------|
|asgard|Asgard|latest (dockerlocal branch)|binary|https://acmeair.ci.cloudbees.com/job/asgard-etiport/|
|asgard|MongoDB|2.4.9|binary|Ubuntu repository|
|auth-service|NetflixOSS Acme Air|latest (astyanax branch)|binary|https://acmeair.ci.cloudbees.com/job/acmeair-netflix-astyanax/|
|base|Oracle Java|7|binary|https://launchpad.net/~webupd8team/+archive/java/|
|base|ruby|1.9.3|binary|Ubuntu repository|
|base|sshd|6.6|binary|Ubuntu repository|
|base|supervisor|3.0|binary|Ubuntu repository|
|base|Ubuntu Linux|14.04|binary|[Docker Index](https://index.docker.io/)|
|cassandra|Cassandra|2.0.7|binary|http://cassandra.apache.org/|
|eureka|Eureka server|1.1.132|binary|Maven Central Repository|
|ibmjava|IBM Java|7.0 SR5|binary|https://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/jre/index.yml|
|liberty|IBM WebSphere Application Server Liberty profile|8.5.5.2|binary|https://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/wasdev/downloads/wlp/index.yml|
|loader|Acme Air loader|latest (astyanax branch)|binary|https://acmeair.ci.cloudbees.com/job/acmeair-netflix-astyanax/|
|microscaler|Microscaler CLI|latest|source|https://github.com/EmergingTechnologyInstitute/microscaler/|
|microscaler|Microscaler|latest|source|https://github.com/EmergingTechnologyInstitute/microscaler/|
|microscaler|gnatsd|latest|source|https://github.com/apcera/gnatsd/|
|microscaler|Go|1.2.1|binary|Ubuntu repository|
|microscaler|MongoDB|2.4.9|binary|Ubuntu repository|
|microscaler|Redis|2.8.4|binary|Ubuntu repository|
|microscaler-agent|Microscaler Agent|latest|source|https://github.com/EmergingTechnologyInstitute/microscaler/|
|skydns|SkyDNS|latest|binary|[Docker Index](https://index.docker.io/)|
|skydock|SkyDock|latest|binary|[Docker Index](https://index.docker.io/)|
|tomcat|Tomcat|7.0.54|binary|http://tomcat.apache.org/|
|webapp|Acme Air|latest (astyanax branch)|binary|https://acmeair.ci.cloudbees.com/job/acmeair-netflix-astyanax/|
|zuul|Zuul|1.0.21|binary|Maven Central Repository|
