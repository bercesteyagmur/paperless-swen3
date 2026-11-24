---
name: paperless-mobile-app
description: Implement one selected step of the new Paperless Ionic mobile application using the existing REST API.
---

# Paperless mobile application

Read `docs/mobile/SPEC.md` and inspect the existing REST controllers before changing code.

Read the selected GitHub issue and its parent issue #127 when GitHub access is available. If the issue cannot be accessed, ask for its description instead of guessing its requirements.

Implement only the selected step from the specification or issue.

Work inside `mobile-app/`. Do not change the backend unless the user explicitly requests a separate backend change.

Create a new mobile implementation. Do not copy components, templates or CSS from `frontend/`.

Use the existing REST API and keep the API base URL in one configurable location.

Keep the implementation small and understandable. Avoid unnecessary libraries and abstractions.

Handle the loading, empty, success and error states that belong to the selected feature.

Do not add secrets. Do not commit or push unless the user asks.

After the implementation:

1. Run the relevant scripts from `mobile-app/package.json`.
2. Run the production build.
3. Manually verify the feature when possible.
4. Show and explain the changed files and the request flow.
5. Report anything that could not be verified.

