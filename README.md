## Microservices playing Pong

<img alt="Pong" align="right" style="margin-left: 25px" src="https://raw.githubusercontent.com/rhuss/ping-pong-peng/pong-gif/pong.gif">

This demo brings good old [Pong](https://en.wikipedia.org/wiki/Pong) to a Microservices world.
Hopefully everyone still remembers ;-)

The actors:

* **ping** : A *spring-boot* player using okhttp to play against a JAX-RS service
* **pong** : A *wildfly-swarm* opponent using JAX-RS
* **pong v2** : A *spring-boot* opponent using JAX-RS

The purpose of this project is to show the gradual process from converting a plain Java Microservices over Docker images to a full Kubernetes orchestration setup. 

Each step is contained in a separate Git branch:

* [1-java](https://github.com/rhuss/ping-pong-peng/tree/1-java) : Plain Java Mircroservices *ping* and *pong*, created with [spring-boot](http://projects.spring.io/spring-boot/) and [wildfly-swarm](http://wildfly-swarm.io/) respectively. 
* [2-docker](https://github.com/rhuss/ping-pong-peng/tree/2-docker) : Dockerization of these services with [fabric8io/docker-maven-plugin](https://github.com/fabric8io/docker-maven-plugin)
* [3-kubernetes](https://github.com/rhuss/ping-pong-peng/tree/3-kubernetes) : Adding Kubernetes deployment descriptors (*todo: A [fabric8io/fabric8-maven-plugin](https://github.com/fabric8io/fabric8-maven-plugin) for creating these resource descriptors from within the build*)
* [4-ping-update](https://github.com/rhuss/ping-pong-peng/tree/4-ping-update) : New version of *ping* in order to demonstrate a rolling update
* [5-pong-update](https://github.com/rhuss/ping-pong-peng/tree/5-pong-update) : Introducing *pong* version 2 and showing the rolling update of a service

### Using local Java

#### ping vs. pong

```
cd pong
mvn wildfly-swarm:run &
cd ../ping
mvn spring-boot:run
```

#### ping vs. peng

```
cd peng
mvn spring-boot:run &
cd ../ping
mvn -DOPPONENT=peng spring-boot:run
```

### Using docker

#### ping vs. pong

```
mvn clean install docker:build
mvn -N -Ddocker.follow docker:start
```

#### ping vs. peng

```
mvn clean install docker:build
mvn -Ppeng -N -Ddocker.follow docker:start
```
