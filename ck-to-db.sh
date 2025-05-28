#!/bin/bash

# PRE: sentinel-backend needs to be running for this script to succeed

set -euo pipefail
source "./logger.sh"
trap 'log_error "Error in ${BASH_SOURCE[0]} on line ${LINENO} (exit code: $?)" >&2; exit 1' ERR

project_path=$1
ck_input_path=$2
ast_elem_api_url=$3

cd "$project_path" || exit

log_info "Compile the \"ck-to-db\" project"
# Compile project that reads CK data
run_quiet "ck-to-db" ./mvnw clean verify

# Execute project
cd ./target || return
log_info "Executing \"ck-to-db\" on the analyzed project, and putting all the data into the DB"
java -jar ck-to-db-1.0-SNAPSHOT-jar-with-dependencies.jar "$ck_input_path" "$ast_elem_api_url"

log_success "Storage of all the data in the db finished!"
