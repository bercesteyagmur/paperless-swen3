#!/usr/bin/env bash

set -euo pipefail

PAPERLESS_BASE_URL="${PAPERLESS_BASE_URL:-http://localhost:8081}"
PAPERLESS_DB_CONTAINER="${PAPERLESS_DB_CONTAINER:-paperless-db}"
PAPERLESS_SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PAPERLESS_SAMPLE_FILE="$PAPERLESS_SCRIPT_DIR/sample-document.txt"

# create test user
PAPERLESS_USER_ID=$(docker exec "$PAPERLESS_DB_CONTAINER" psql -qAt \
  -U paperless \
  -d paperless \
  -c "INSERT INTO users (username, password) VALUES ('curl-test-user', 'test-password') ON CONFLICT (username) DO UPDATE SET password = EXCLUDED.password RETURNING id;")

echo "Using test user $PAPERLESS_USER_ID"

# upload sample file
PAPERLESS_UPLOAD_RESPONSE=$(curl -fsS \
  -X POST \
  -F "file=@$PAPERLESS_SAMPLE_FILE;type=text/plain" \
  "$PAPERLESS_BASE_URL/api/files?userId=$PAPERLESS_USER_ID")

# get uploaded file ID
PAPERLESS_FILE_ID=$(printf '%s' "$PAPERLESS_UPLOAD_RESPONSE" | python3 -c 'import json, sys; print(json.load(sys.stdin)["id"])')

echo "Uploaded file $PAPERLESS_FILE_ID"

# get all files
curl -fsS "$PAPERLESS_BASE_URL/api/files"
echo

# get uploaded file by ID
curl -fsS "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID"
echo

# rename uploaded file
curl -fsS \
  -X PATCH \
  -H "Content-Type: application/json" \
  -d '{"originalFileName":"renamed-document.txt"}' \
  "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID"
echo

# delete uploaded file
curl -fsS \
  -X DELETE \
  -o /dev/null \
  "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID"


# check if file was deleted, should be 404 not found
PAPERLESS_STATUS=$(curl -sS \
  -o /dev/null \
  -w "%{http_code}" \
  "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID")

if [[ "$PAPERLESS_STATUS" != "404" ]]; then
  echo "Expected status 404 after deleting the file but got $PAPERLESS_STATUS" >&2
  exit 1
fi

echo "All endpoint tests passed"
