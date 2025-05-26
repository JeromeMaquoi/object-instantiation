#!/bin/bash

cd "$output_repo_path" || exit

if [ ! -d "output-ck" ] || [ "$(ls -1A "$output_repo_path/output-ck/" | wc -l)" -eq 0 ]; then
    echo -e "Running CK for $output_repo_path"
    # Change the CK version in function of the analyzed project
    if [[ "$output_repo_path" == *"jabref" ]] || [[ "$output_repo_path" == *"spoon" ]]; then
        echo -e "Using CK with JDK 17"
        ck_jar="ck-0.7.1-SNAPSHOT-jar-with-dependencies-jdk17.jar"
    else
        echo -e "Using normal CK"
        ck_jar="ck-0.7.1-SNAPSHOT-jar-with-dependencies-normal.jar"
    fi
    mkdir "output-ck"
    java -jar "$plugin_path/$ck_jar" "$output_repo_path" False 0 True "$output_repo_path/output-ck/"
else
    echo "Directory 'output-ck' already exists or is not empty in $output_repo_path"
fi
echo -e "\n"

