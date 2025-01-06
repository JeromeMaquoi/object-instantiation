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

echo "mvn clean package"
mvn clean package

cd ./target || return
echo "java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar ${input_source_code} ${output_source_code}"
java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar "$input_source_code" "$output_source_code" "$input_repo_path"

# Copy be.unamur.snail.register.SendUtils to the output folder
echo "mkdir -p ${output_source_code}/be/unamur/snail/register"
mkdir -p "${output_source_code}/be/unamur/snail/register"
echo "cp -r ${REGISTER_PATH} ${output_source_code}/be/unamur/snail/register"
cp -r "$REGISTER_PATH" "${output_source_code}/be/unamur/snail/register/"

# Execute spring-boot tests with the transformation
cd "$output_repo_path" || return
rm -rf .gradle/
export JAVA_HOME=/usr/lib/jvm/java-19-openjdk-amd64
echo "./gradlew clean spring-boot-project:spring-boot:test"
./gradlew clean spring-boot-project:spring-boot:test --rerun-tasks

#export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
#mvn clean test
