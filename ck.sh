#!/bin/bash
set -euo pipefail
source "./logger.sh"
trap 'log_error "Error in ${BASH_SOURCE[0]} on line ${LINENO} (exit code: $?)" >&2; exit 1' ERR

output_repo_path=$1
plugin_path=$2

cd "$output_repo_path"

if [ ! -d "output-ck" ] || [ "$(ls -1A "$output_repo_path/output-ck/" | wc -l)" -eq 0 ]; then
    log_info -e "Running CK for $output_repo_path"
    # Change the CK version in function of the analyzed project
    if [[ "$output_repo_path" == *"jabref" ]] || [[ "$output_repo_path" == *"spoon" ]]; then
        log_info -e "Using CK with JDK 17"
        ck_jar="ck-0.7.1-SNAPSHOT-jar-with-dependencies-jdk17.jar"
    else
        log_info -e "Using normal CK"
        ck_jar="ck-0.7.1-SNAPSHOT-jar-with-dependencies-normal.jar"
    fi
    mkdir "output-ck"
    java -jar "$plugin_path/$ck_jar" "$output_repo_path" False 0 True "$output_repo_path/output-ck/"
else
    log_info "Directory 'output-ck' already exists or is not empty in $output_repo_path"
fi

