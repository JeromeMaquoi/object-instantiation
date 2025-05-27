#!/bin/bash
set -euo pipefail
source "./logger.sh"
trap 'log_error "Error in ${BASH_SOURCE[0]} on line ${LINENO}: ${BASH_COMMAND} (exit code: $?)" >&2; exit 1' ERR

PROJECT_NAME=$1
PROJECT_DIR=$2

cd "$PROJECT_DIR" || return

if [ "$PROJECT_NAME" == "spring-boot" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-19-openjdk-amd64
  rm -rf .gradle/
  log_info "./gradlew clean spring-boot-project:spring-boot:test"
#  ./gradlew clean spring-boot-project:spring-boot:test --rerun-tasks
#  ./gradlew clean spring-boot-project:spring-boot:test --rerun-tasks --tests org.springframework.boot.logging.log4j2.ColorConverterTests
  ./gradlew clean spring-boot-project:spring-boot:test --rerun-tasks --tests org.springframework.boot.ApplicationEnvironmentTests.propertyResolverIsOptimizedForConfigurationProperties

elif [ "$PROJECT_NAME" == "spoon" ]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  mvn clean test
else
  # TODO handle other projects execution
  log_warn "Test execution not implemented for project '$PROJECT_NAME'"
  exit 1
fi

