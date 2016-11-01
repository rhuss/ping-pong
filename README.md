## Microservices playing Pong

<img alt="Pong" align="right" style="margin-left: 25px" src="https://raw.githubusercontent.com/rhuss/ping-pong/pong-gif/pong.gif">

This demo brings good old [Pong](https://en.wikipedia.org/wiki/Pong) to a Microservices world.
Hopefully everyone still remembers ;-)

The actors:

* **ping** : A *spring-boot* player using okhttp to play against a JAX-RS service
* **pong** : A *wildfly-swarm* opponent using JAX-RS
* **pong v2** : A *spring-boot* opponent using JAX-RS

The purpose of this project is to show the gradual process from converting a plain Java Microservices over Docker images to a full Kubernetes orchestration setup.

Each step is contained in a separate Git branch:

* [1-java](https://github.com/rhuss/ping-pong/tree/1-java) : Plain Java Mircroservices *ping* and *pong*, created with [spring-boot](http://projects.spring.io/spring-boot/) and [wildfly-swarm](http://wildfly-swarm.io/) respectively.
* [2-docker](https://github.com/rhuss/ping-pong/tree/2-docker) : Dockerization of these services with [fabric8io/docker-maven-plugin](https://github.com/fabric8io/docker-maven-plugin)
* [3-kubernetes](https://github.com/rhuss/ping-pong/tree/3-kubernetes) : Adding Kubernetes deployment descriptors 
* [3.5-fmp](https://github.com/rhuss/ping-pong/tree/3.5-fmp) : Variation of the above but using the [fabric8io/fabric8-maven-plugin](https://github.com/fabric8io/fabric8-maven-plugin) for creating these resource descriptors from within the build.
* [4-ping-update](https://github.com/rhuss/ping-pong/tree/4-ping-update) : New version of *ping* in order to demonstrate a rolling update
* [5-pong-update](https://github.com/rhuss/ping-pong/tree/5-pong-update) : Introducing *pong* version 2 and showing the rolling update of a service
* [6-elasticsearch-logging](https://github.com/rhuss/ping-pong/tree/6-elasticsearch-logging) : Add a modified ELK stack to the fabric8-maven-configuration and adapted the ping application to use fluentd to log the result of game match.

### 1-java: Using local Java

#### ping vs. pong

```
cd pong
mvn wildfly-swarm:run &
cd ../ping
mvn spring-boot:run
```

### 2-docker: Using Docker

#### ping vs. pong

```
mvn clean install docker:build
mvn -N -Ddocker.follow docker:start
```

### 3-kubernetes: Usin plain Kuberentes descriptors 

```
cd pong/src/main/kubernetes
kubectl create -f rc.yaml
kubectl create -f service.yaml
```

```
cd ping/src/main/kubernetes
kubectl create -f rc.yaml
```

### 3.5-fmp: 

Use the fabric8-maven-plugin to create the descriptors

```
cd ping
mvn fabric8:deploy
cd ../pong
mvn fabric8:deploy
```

### 4-ping-update: Rolling update the client 

The client gets update to use a direct DNS SRV lookup to find its service as well changing the log output. The new image is `rhuss/ping:2`

```
cd ping/src/main/kuberentes
sh rolling-update-v2.sh
# Rollback:
sh rolling-update-v1.sh
```

### 5-pong-update: Rolling update the server 

Switch the server from wildfly-swarm to spring-boot. The new image is `rhuss/pong:2`

```
cd ping/src/main/kuberentes
sh rolling-update-v2.sh
# Rollback:
sh rolling-update-v1.sh
```

### 6-elasticsearch-logging: Add elastic search logging to the app

* New image `rhuss/ping:3` which uses a fluentd logger for writing out the result to fluentd
* Modify the ping application to a pod with the application and fluentd as a sidecar.
* Descriptors for elasticsearch and kibana applications

```
mvn clean install
cd dist
mvn fabric8:resource
kubectl create -f target/classes/META-INF/fabric8/kubernetes.yml
```