# Paperless mobile application

## Goal

Create a new smartphone frontend for Paperless with Ionic Angular and Capacitor.

The existing Angular web frontend must not be wrapped or copied. The mobile application uses the existing Paperless REST API.

## Features

The mobile application should support:

- showing all uploaded documents
- uploading a PDF document
- showing document details, tags and the generated summary
- searching documents by their OCR content
- renaming and deleting a document
- adding and removing tags
- filtering documents by tag

## Technical decisions

- Framework: Ionic Angular
- Native runtime: Capacitor
- Project folder: `mobile-app/`
- Backend: existing Paperless REST API
- The API base URL must be configurable for browser, emulator and smartphone.
- The mobile application does not access PostgreSQL, MinIO, RabbitMQ or Elasticsearch directly.

## Rules

- Build the mobile frontend as a new application.
- Do not copy components, HTML or CSS from `frontend/`.
- Do not rebuild backend logic in the mobile application.
- Do not change the backend unless a missing API function makes it necessary.
- Do not store secrets in the mobile application.
- Keep the code simple enough to explain during the code review.

## Implementation order

1. Issue #136: Create the Ionic Angular application, add the API configuration and verify that it starts.
2. Issue #137: Add the document list and detail page with tags and summary.
3. Issue #138: Add PDF upload.
4. Issue #139: Add rename and delete.
5. Issue #140: Add tag editing and tag filtering.
6. Issue #141: Add OCR-content search.
7. Issue #142: Run the build, test the REST calls and verify the application with Capacitor in a smartphone emulator.

Implement and review one step at a time.

## Acceptance criteria

- The application was created as a new Ionic project.
- No existing web component or web CSS was copied.
- The application communicates with the existing Paperless REST API.
- A user can upload a PDF and see it in the document list.
- A user can open a document and see its metadata, tags and summary when available.
- A user can search for a word from the OCR text.
- Loading, empty and error states are visible.
- The available tests and production build pass.
- The application can be demonstrated in a smartphone emulator or on a real smartphone.

## Not part of the first version

- direct access to RabbitMQ, MinIO or Elasticsearch
- offline synchronization
- user authentication, because the current backend does not provide it
- push notifications

