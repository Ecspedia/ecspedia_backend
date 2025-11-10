#!/usr/bin/env bash
set -euo pipefail

ENV_FILE="${1:-.env}"

if [ ! -f "$ENV_FILE" ]; then
  echo "Unable to load environment: '$ENV_FILE' not found." >&2
  exit 1
fi

# Enable automatic export for sourced variables so they propagate to the current shell.
set -a
source "$ENV_FILE"
set +a

echo "Loaded environment variables from $ENV_FILE"
