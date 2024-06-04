#!/bin/bash

mvn clean package
cd ./target || return
java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar /home/jerome/Documents/Assistant/Recherche/joular-scripts/test-sentinel-copy/spring-boot/spring-boot-project/spring-boot/src/