---
name: paperless-mobile-app
description: "Implement or verify one Paperless mobile issue in the Flet app, including Android APK checks for #142."
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

1. Run the available checks for the changed feature and verify it against the selected issue.
2. For #142, follow [Android verification](references/android-verification.md). Use [build-apk.sh](scripts/build-apk.sh) to build the APK and report its path.
3. If a check fails, use the error to fix the cause and rerun the check. If Android or the backend is unavailable, report which checks ran and which are still open.
4. Explain the changed files, request flow and test results before moving to another issue.

