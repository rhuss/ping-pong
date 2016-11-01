#!/bin/bash

for i in 0 1 2 3
do
  echo "================== Building on n${i} ========================"
  export DOCKER_HOST=tcp://n${i}:2375
  unset DOCKER_TLS_VERIFY
  cd ping
  mvn -Ddmp.from="hypriot/rpi-java" clean install docker:build
  cd ..
done