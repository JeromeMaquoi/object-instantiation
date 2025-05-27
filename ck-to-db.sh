#!/bin/bash

cd "$project_path" || exit

echo "Beginning to store CK data..."
# Compile project that reads CK data
if ./mvnw clean verify; then
  # Execute project
  cd ./target
  java -jar ck-to-db-1.0-SNAPSHOT-jar-with-dependencies.jar "$ck_input_path" "$ast_elem_api_url"
  echo "Storage of CK data finished!"
else
  echo "mvn clean verify of \"ck-to-db\" has failed"
  exit 1
fi
