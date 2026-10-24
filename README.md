# Simple Calendar (Android)

A small offline Android calendar app.

## Features
- Monthly calendar with previous/next month navigation
- Jump back to today's date
- Select any date
- Add, edit, and clear a note for a date
- Notes are stored locally on the device
- Small dot marks dates that have a note

## Build an APK using GitHub (beginner-friendly)

1. Create a GitHub account and create a **new repository** named `SimpleCalendar`.
2. On the repository page, choose **Add file → Upload files**.
3. Upload the contents of this project folder (upload the files and folders inside it, not the ZIP itself).
4. Commit the files to the `main` branch.
5. Open the repository's **Actions** tab. The workflow named **Build Android APK** should run automatically.
6. Wait for the run to finish with a green check mark. Open that run and find **Artifacts** at the bottom.
7. Download `SimpleCalendar-debug-apk`. Extract the downloaded ZIP; inside is `app-debug.apk`.
8. Send the APK to your Android phone and install it. Android may ask you to allow installation from that source.

## Notes
- This is a debug APK for personal testing, not a Play Store release.
- No internet permission, account, or server is required by the app.
- The project is configured for Android Gradle Plugin 8.7.3 and Gradle 8.9.
