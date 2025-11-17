#!/bin/bash
# Simple healthcheck for WebLogic Admin Console
URL=${1:-http://localhost:7001/console/login/LoginForm.jsp}
if curl -fsS "$URL" >/dev/null; then
  echo "OK"
  exit 0
else
  echo "FAIL"
  exit 1
fi
