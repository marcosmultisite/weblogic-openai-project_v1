#!/bin/bash
# Readiness probe: check admin server and the application endpoint
ADMIN_URL=${1:-http://localhost:7001/console/login/LoginForm.jsp}
APP_URL=${2:-http://localhost:7001/weblogic-openai/}

if curl -fsS "$ADMIN_URL" >/dev/null && curl -fsS "$APP_URL" >/dev/null; then
  echo "READY"
  exit 0
else
  echo "NOT_READY"
  exit 1
fi
