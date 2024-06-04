#!/bin/bash

mvn clean package
cd ./target || return
java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar