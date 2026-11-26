# Paperless mobile app

## Run locally

From this directory, run:

```sh
python3 -m venv .venv
source .venv/bin/activate
python -m pip install '.[dev]'
PAPERLESS_API_URL=http://localhost flet run --web
```

Start the Paperless backend before opening the app. The browser should show the document list. Select a filename to open its detail screen.

## Paperless API address

The default in `src/config.py` points to `http://10.0.2.2`, which reaches the host computer from the standard Android emulator. To prepare a build for a physical phone, set the default to a Paperless server address the phone can reach before building. For local development, use the `PAPERLESS_API_URL` environment variable as shown above. Use the server address without the `/api` suffix.

The app loads the list from `GET /api/files` and document details from `GET /api/files/{id}`.

Use **Upload PDF** to select a PDF and send it to `POST /api/files`. On success, the document list refreshes.

## Build and install the Android APK

The first Android build may install Flutter, JDK 17 and additional Android SDK tools. Android Studio can manage the SDK and emulator.

With the Flet environment active, run this from the repository root:

```sh
bash .agents/skills/paperless-mobile-app/scripts/build-apk.sh
```

Start an Android emulator, then install the APK using the path printed by the script:

```sh
adb install -r /path/to/paperless.apk
```

If `adb` is not in your path on macOS, use `~/Library/Android/sdk/platform-tools/adb` instead.

Start Paperless on the host computer and open the app on the emulator. The default emulator address reaches Paperless through nginx on port 80. Check that the document list loads, open a document and upload a test PDF. For the layout check, repeat the list and detail checks at a narrow and a wider screen size.
