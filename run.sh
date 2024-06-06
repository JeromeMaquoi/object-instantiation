#!/bin/bash

repo_path="/home/jerome/Documents/Assistant/Recherche/joular-scripts/test-sentinel-copy/test/"

input_repo_path="${repo_path}/spring-boot"
input_source_code="${repo_path}/spring-boot/spring-boot-project/spring-boot/src/main/java/"

output_repo_path="${repo_path}/spring-boot output"
output_source_code="${output_repo_path}/spring-boot-project/spring-boot/src/main/java/"

# Remove old output folder
echo "Removing ${output_repo_path}..."
rm -rf "$output_repo_path"

# Copy the project into a new output folder where the transformed classes will be put
echo "Copying ${input_repo_path} into ${output_repo_path}"
cp -r "$input_repo_path" "$output_repo_path"

mvn clean package
cd ./target || return
java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar "$input_source_code" "$output_source_code"

# Copy be.unamur.snail.register.RegisterUtils to the output folder
register_path="/home/jerome/Documents/Assistant/Recherche/joular-scripts/object-instantiation/src/main/java/be/unamur/snail/register/."
mkdir -p "${output_source_code}/be/unamur/snail/register"
cp -r "$register_path" "${output_source_code}/be/unamur/snail/register/"

# Execute spring-boot tests with the transformation
#cd "$output_repo_path" || return
#pwd
#export JAVA_HOME=/usr/lib/jvm/java-19-openjdk-amd64
#./gradlew clean spring-boot-project:spring-boot:test
