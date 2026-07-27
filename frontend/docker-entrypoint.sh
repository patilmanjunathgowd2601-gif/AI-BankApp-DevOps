#!/bin/sh
# Injects runtime environment variables (e.g. API_BASE_URL) into a small
# window._env_ script so the same built image can be reused across
# dev/staging/prod without rebuilding - a common 12-factor pattern for SPAs.
set -e
ENV_FILE=/usr/share/nginx/html/env-config.js
echo "window._env_ = { API_BASE_URL: \"${API_BASE_URL:-}\" };" > "$ENV_FILE"
