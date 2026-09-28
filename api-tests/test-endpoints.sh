#!/usr/bin/env bash

set -euo pipefail

PAPERLESS_BASE_URL="${PAPERLESS_BASE_URL:-http://localhost:8081}"
PAPERLESS_SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PAPERLESS_SAMPLE_FILE="$PAPERLESS_SCRIPT_DIR/sample-document.txt"

echo "Running endpoint tests against $PAPERLESS_BASE_URL"
echo

PAPERLESS_UPLOAD_RESPONSE=$(curl -fsS \
  -X POST \
  -F "file=@$PAPERLESS_SAMPLE_FILE;type=text/plain" \
  "$PAPERLESS_BASE_URL/api/files")

PAPERLESS_FILE_ID=$(printf '%s' "$PAPERLESS_UPLOAD_RESPONSE" | python3 -c 'import json, sys; print(json.load(sys.stdin)["id"])')

echo "[PASS] POST /api/files - uploaded file $PAPERLESS_FILE_ID"

curl -fsS -o /dev/null "$PAPERLESS_BASE_URL/api/files"
echo "[PASS] GET /api/files"

curl -fsS -o /dev/null "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID"
echo "[PASS] GET /api/files/$PAPERLESS_FILE_ID"

curl -fsS \
  -X PATCH \
  -H "Content-Type: application/json" \
  -d '{"originalFileName":"renamed-document.txt"}' \
  -o /dev/null \
  "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID"
echo "[PASS] PATCH /api/files/$PAPERLESS_FILE_ID"

curl -fsS \
  -X DELETE \
  -o /dev/null \
  "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID"
echo "[PASS] DELETE /api/files/$PAPERLESS_FILE_ID"

PAPERLESS_STATUS=$(curl -sS \
  -o /dev/null \
  -w "%{http_code}" \
  "$PAPERLESS_BASE_URL/api/files/$PAPERLESS_FILE_ID")

if [[ "$PAPERLESS_STATUS" != "404" ]]; then
  echo "Expected status 404 after deleting the file but got $PAPERLESS_STATUS" >&2
  exit 1
fi

echo "[PASS] Deleted file returns HTTP 404"
echo
echo "All endpoint tests passed"
