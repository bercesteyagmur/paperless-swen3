import os


API_BASE_URL = os.getenv("PAPERLESS_API_URL", "http://10.0.2.2").rstrip("/")
