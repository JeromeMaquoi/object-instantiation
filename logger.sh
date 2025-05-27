#!/bin/bash

timestamp() {
  date '+%Y-%m-%d %H:%M:%S'
}
log_info()    { echo -e "\033[1;37m[$(timestamp)]\033[0m \033[1;34m[INFO]\033[0m $*"; }
log_warn()    { echo -e "\033[1;33m[$(timestamp)]\033[0m \033[WARN]\033[0m $*"; }
log_error()   { echo -e "\033[1;31m[$(timestamp)]\033[0m \033[ERROR]\033[0m $*" >&2; }
log_success() { echo -e "\033[1;37m[$(timestamp)]\033[0m \033[1;32m[SUCCESS]\033[0m $*"; }

run_quiet() {
  local log_label="$1"; shift
  local logfile="logs/${log_label}.log"

  mkdir -p logs

  log_info "Running: $*"
  if "$@" > "$logfile" 2>&1; then
    log_success "Command succeeded: $*"
  else
    log_error "Command failed: $*"
    log_error "Log from $logfile:"
    echo "-------------------------"
    cat "$logfile"
    echo "-------------------------"
    exit 1
  fi
}