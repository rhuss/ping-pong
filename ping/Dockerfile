ARG from=java:8-jre-alpine
FROM ${from}

ARG version=1

ADD target/ping-${version}.jar /maven/ping.jar
CMD java -jar /maven/ping.jar
