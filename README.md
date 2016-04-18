## 3 Microservices playing Pong

<img alt="Pong" align="right" style="margin-left: 25px" src="https://raw.githubusercontent.com/rhuss/ping-pong-peng/pong-gif/pong.gif">

This demo brings good old [Pong](https://en.wikipedia.org/wiki/Pong) in to a Microservices world.
Hopefully everbody remembers ;-)

The actors:

* **ping** : A *spring-boot* player using okhttp to play against a JAX-RS service
* **pong** : A *wildfly-swarm* opponent using JAX-RS
* **peng** : A *spring-boot* opponent using JAX-RS

The purpose of this project is to showthe gradual way from plain Java Microservices over Docker images 
to a full Kubernetes orchestration. Each step is in a separate Git branch:

* [1-java]() : Plain Java Mircroservices *ping* and *pong*, created with [spring-boot]() and [wildfly-swarm]() respectively. 
* [2-docker]() : Dockerization of these services with [fabric8io/docker-maven-plugin]()
* [3-peng]() : Introducing *peng* as an alternative to *pong*.
* [4-kubernetes]() : Adding Kuberentes deployment descriptors (*todo: A [fabric8io/fabric8-mave-plugin] for creating these resource descriptors from within the build)
* [5-rolling-update]() : New version of *ping* in order to demonstrate a rolling update
* 6-elastic-search : Adding [elastic-search]() and [grafana]() to store the results of a game and provide some dashbard. (**TODO**) 

More usage instructions and better documentations are coming later .... 

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