# API test scripts

The folder contains a Postman Collection and a CURL script for all current file endpoints

## Start the application

```bash
docker compose up --build
```

## Postman

1. Import `Paperless.postman_collection.json` into Postman
2. Open the `Upload file` request and go to `Body` -> `form-data`
3. In the `file` row, click `Select Files` and select `api-tests/sample-document.txt` from this project
4. Run the collection from top to bottom. The upload request automatically saves the returned `fileId` for the following requests

## CURL

Run the script while the application and database are running:

```bash
./api-tests/test-endpoints.sh
```

The script calls every current file endpoint and checks that the deleted file returns HTTP 404
