# Paperless mobile app

## Run locally

From this directory, run:

```sh
python3 -m venv .venv
source .venv/bin/activate
python -m pip install '.[dev]'
PAPERLESS_API_URL=http://localhost flet run --web
```

The browser should show the Paperless start screen and the configured server address.

## Paperless API address

The default in `src/config.py` points to `http://10.0.2.2`, which reaches the host computer from the standard Android emulator. To prepare a build for a physical phone, set the default to a Paperless server address the phone can reach before building. For local development, use the `PAPERLESS_API_URL` environment variable as shown above. Use the server address without the `/api` suffix.

The start screen shows this address. Document requests are added with the document list.
