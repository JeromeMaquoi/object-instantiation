#!/bin/bash
set -euo pipefail
source "./logger.sh"
trap 'log_error "Error in ${BASH_SOURCE[0]} on line ${LINENO}: ${BASH_COMMAND} (exit code: $?)" >&2; exit 1' ERR

project_path=$1
ck_input_path=$2
ast_elem_api_url=$3

cd "$project_path" || exit

log_info "Beginning to store CK data..."
# Compile project that reads CK data
./mvnw clean verify

# Execute project
cd ./target || return
java -jar ck-to-db-1.0-SNAPSHOT-jar-with-dependencies.jar "$ck_input_path" "$ast_elem_api_url"

log_success "Storage of CK data finished!"
