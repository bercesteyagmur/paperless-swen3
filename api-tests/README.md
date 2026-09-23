# API test scripts

The folder contains a Postman Collection and a CURL script for all current file endpoints

## Start the application

```bash
docker compose up --build
```

## Postman

1. Import `Paperless.postman_collection.json` into Postman
2. Create a test user and get its ID:

```bash
docker exec -i paperless-db psql -U paperless -d paperless -c "INSERT INTO users (username, password) VALUES ('postman-test-user', 'test-password') ON CONFLICT (username) DO UPDATE SET password = EXCLUDED.password RETURNING id;"
```

3. Set the returned ID as the `userId` collection variable
4. Open the `Upload file` request and go to `Body` -> `form-data`
5. In the `file` row, click `Select Files` and select `api-tests/sample-document.txt` from this project
6. Run the collection from top to bottom. The upload request automatically saves the returned `fileId` for the following requests

## CURL

Run the script while the application and database are running:

```bash
./api-tests/test-endpoints.sh
```

The script creates its own test user, calls every current file endpoint and checks that the deleted file returns HTTP 404
