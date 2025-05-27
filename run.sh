#!/bin/bash
set -euo pipefail
source "./logger.sh"
trap 'log_error "Error in ${BASH_SOURCE[0]} on line ${LINENO} (exit code: $?)" >&2; exit 1' ERR

log_info "Loading environment variables"
export $(grep -v '^#' .env | xargs -d '\n')

input_repo_path="${REPO_PATH}${PROJECT_NAME}"
input_source_code="${input_repo_path}${SRC_FOLDER_PATH}/src/main/java/"

output_repo_path="${REPO_PATH}${PROJECT_NAME}_output"
output_source_code="${output_repo_path}${SRC_FOLDER_PATH}/src/main/java/"

# Remove old output folder
log_info "Removing ${output_repo_path}..."
rm -rf "$output_repo_path"

# Copy the project into a new output folder where the transformed classes will be put
log_info "Copying the analyzed project into a new output folder where the transformed classes will be put"
cp -r "$input_repo_path" "$output_repo_path"

# Create the .jar of object-instantiation project
log_info "Creating jar for \"object-instantiation\" project"
run_quiet "object-instantiation compilation" mvn clean verify

# Execute the .jar on the analyzed project
cd ./target
log_info "Executing the .jar of \"object-instantiation\" on the analyzed project"
java -jar object-instantiation-1.0-SNAPSHOT-jar-with-dependencies.jar "$input_source_code" "$output_source_code" "$input_repo_path" || true

# Execute CK on the project
cd ..
bash ./ck.sh "$output_repo_path" "$PWD/plugins"

# Put all CK data into the db
bash ./ck-to-db.sh "$CK_TO_DB_PROJECT_PATH" "$output_repo_path/output-ck/method.csv" "$AST_ELEM_API_URL"

# Copy be.unamur.snail.register package to the output folder
log_info "Creating new directory for the be.unamur.snail.register package"
mkdir -p "${output_source_code}/be/unamur/snail/register"
log_info "Copying all files from be.unamur.snail.register package into the new directory"
cp -r "$REGISTER_PATH" "${output_source_code}/be/unamur/snail/register/"

# Execute tests of the analyzed project within the transformed code
bash ./run-tests.sh "$PROJECT_NAME" "$output_repo_path"

