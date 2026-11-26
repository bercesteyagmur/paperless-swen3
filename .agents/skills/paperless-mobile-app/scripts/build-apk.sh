#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../../../.." && pwd)"
app_dir="$repo_root/mobile-app"

if [[ ! -d "$app_dir" ]]; then
  echo "The mobile-app folder is missing. Set up the Flet project first." >&2
  exit 1
fi

if ! command -v flet >/dev/null; then
  echo "Flet is missing. Activate the mobile project's Python environment first." >&2
  exit 1
fi

build_dir="$(mktemp -d "${TMPDIR:-/tmp}/paperless-apk.XXXXXXXX")"
cd -- "$app_dir"
flet build apk --output "$build_dir/apk"

shopt -s nullglob
apks=("$build_dir"/apk/*.apk)
if (( ${#apks[@]} == 0 )); then
  echo "The build did not produce an APK in $build_dir/apk." >&2
  exit 1
fi

printf 'APK: %s\n' "${apks[@]}"
