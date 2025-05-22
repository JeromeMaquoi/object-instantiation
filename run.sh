#!/bin/bash

export $(grep -v '^#' .env | xargs -d '\n')

input_repo_path="${REPO_PATH}${PROJECT_NAME}"
input_source_code="${input_repo_path}${SRC_FOLDER_PATH}/src/main/java/"

output_repo_path="${REPO_PATH}${PROJECT_NAME}_output"
output_source_code="${output_repo_path}${SRC_FOLDER_PATH}/src/main/java/"

# Remove old output folder
echo "Removing ${output_repo_path}..."
echo "rm -rf ${output_repo_path}"
rm -rf "$output_repo_path"

# Copy the project into a new output folder where the transformed classes will be put
echo "Copying ${input_repo_path} into ${output_repo_path}"
echo "cp -r ${input_repo_path} ${output_repo_path}"
cp -r "$input_repo_path" "$output_repo_path"

# Create the .jar of object-instantiation project
echo "mvn clean verify"
if mvn clean verify; then
  # Execute the .jar on the analyzed project
  cd ./target || return
  echo "java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar ${input_source_code} ${output_source_code}"
  java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar "$input_source_code" "$output_source_code" "$input_repo_path"

  # Copy be.unamur.snail.register package to the output folder
  echo "mkdir -p ${output_source_code}/be/unamur/snail/register"
  mkdir -p "${output_source_code}/be/unamur/snail/register"
  echo "cp -r ${REGISTER_PATH} ${output_source_code}/be/unamur/snail/register"
  cp -r "$REGISTER_PATH" "${output_source_code}/be/unamur/snail/register/"

  # Execute tests of the analyzed project within the transformed code
  cd "$output_repo_path" || return

  if [ "$PROJECT_NAME" == "spring-boot" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-19-openjdk-amd64
    rm -rf .gradle/
    echo "./gradlew clean spring-boot-project:spring-boot:test"
  #  ./gradlew clean spring-boot-project:spring-boot:test --rerun-tasks
  #  ./gradlew clean spring-boot-project:spring-boot:test --rerun-tasks --tests org.springframework.boot.logging.log4j2.ColorConverterTests
    ./gradlew clean spring-boot-project:spring-boot:test --rerun-tasks --tests org.springframework.boot.ApplicationEnvironmentTests
  fi

  if [ "$PROJECT_NAME" == "spoon" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
    mvn clean test
  fi

  # TODO handle other projects execution

else
  echo "mvn clean verify has failed"
fi
