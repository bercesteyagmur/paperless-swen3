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
