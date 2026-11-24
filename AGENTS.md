# Paperless project instructions

Preserve existing comments, spacing and unrelated changes.

Do not commit or push unless the user explicitly asks for it.

## Mobile application

For tasks concerning the smartphone application:

- Read `docs/mobile/SPEC.md` before implementing a feature.
- Use the `paperless-mobile-app` skill.
- Create and modify mobile code only in `mobile-app/` unless a backend change is explicitly requested.
- Build the mobile frontend as a new Ionic Angular application.
- Do not copy components, templates or CSS from `frontend/`.
- Use the existing Paperless REST API.
- Implement one selected step at a time and keep it understandable for a student code review.
- Run the relevant tests and production build before finishing a feature.

