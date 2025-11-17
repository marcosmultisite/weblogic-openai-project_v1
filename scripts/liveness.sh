#!/bin/bash
# Liveness probe: only check the admin port responsiveness
ADMIN_PORT=${1:-7001}
if nc -z localhost $ADMIN_PORT; then
  echo "ALIVE"
  exit 0
else
  echo "DEAD"
  exit 1
fi
