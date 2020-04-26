### 端口信息（开发）
#### 1.环境端口
|端口|服务|描述|
|:---:|:---:|:---|
|2181|zookeeper|提供服务|
|2888|zookeeper|集群内通讯|
|3888|zookeeper|选举leader|
|6379|redis|服务端口|
|8161|activeMQ|后台管理端口|
|61616|activeMQ|服务端口|
---
#### 2.项目端口
|端口|服务|描述|
|:---:|:---:|:---|
|8080|web|运行端口|
|8180|admin|运行端口|
|8181|user|运行端口|
|8182|file|运行端口|
|8183|upload|运行端口|
|8280|email|运行端口|
|18180|admin|Dubbo开放端口|
|18181|user|Dubbo开放端口|
|18182|file|Dubbo开放端口|
|18183|upload|Dubbo开放端口|
|28183|upload|Jetty端口|
|18280|email|Dubbo开放端口|
---
#### 3.工具预留端口
|端口|服务|描述|
|:---:|:---:|:---|
|8081|ZKUI|zookeeper管理工具|
|8082|dubbo-admin|dubbo管理工具|