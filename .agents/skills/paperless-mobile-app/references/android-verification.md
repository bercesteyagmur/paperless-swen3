# Android verification

Use this checklist for #142 or when a change needs to be checked on Android. The acceptance criteria live in `docs/mobile/SPEC.md`.

1. From the repository root, run `bash .agents/skills/paperless-mobile-app/scripts/build-apk.sh` in the Flet environment. Save the reported APK to your chosen submission folder when preparing the hand-in. The script uses a new temporary output directory for each build.
2. Install and open the APK on an Android emulator or device. Configure the Paperless API URL in the app. On the standard Android emulator, use `10.0.2.2` to reach a server running on the host computer. On a physical device, use a server address reachable from that device.
3. Load the document list, upload a test PDF and open the resulting detail view. Record the APK path, device and result separately for the build, installation and upload.
4. When checking responsive layout, try a narrow and a wider emulator screen. Check that the search and upload controls stay usable, and that long filenames and summaries wrap without horizontal scrolling.

If a device or backend is unavailable, mark the affected checks as pending.
