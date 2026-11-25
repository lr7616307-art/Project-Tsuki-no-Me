# Project Tsuki no Me

Android 10 (API 29) app for **armeabi-v7a** devices (32-bit ARMv7).

After unlock, plays one randomly chosen imported video as a full-screen overlay over the current app (if that app is allowed). When the video ends, control returns exactly where the user was — no navigation, no focus steal, no typing interruption.

## Features

- **Unlock overlay** — random video after unlock, limited by time window, videos-per-hour, and pause timer
- **App selection** — only selected apps, or all except selected; optional home-screen playback
- **Import** — select individual videos, pick a folder, or scan the whole device (MediaStore, no file copy)
- **Caller video** (optional) — replace the incoming-call screen with a video when this app is set as the Phone app (default dialer / InCallService). Red Decline / green Answer control the real call
- **In-app player** — Back 5s, Play/Pause, Forward 5s when opening a video from the library list (not on unlock overlay or call UI)
- No network; light battery use; missing files skipped and removed from the list

## Requirements

- minSdk 29, targetSdk 29
- Permissions: Display over other apps, Usage access, Storage / media
- For caller video: set this app as the system **Phone app** (RoleManager.ROLE_DIALER)

## Build

```bash
# Android SDK + JDK 17 required
./gradlew assembleDebug
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

Package: `me.tsukinome.project`  
Version: 1.1.0 (versionCode 2)

## Android 10 notes

- Unlock detection uses a foreground service + dynamic `USER_PRESENT` receiver (manifest receivers for that action are restricted).
- Overlay uses `TYPE_APPLICATION_OVERLAY` with `FLAG_NOT_FOCUSABLE` so the keyboard and notification shade stay usable.
- Replacing the incoming-call UI requires being the default dialer; a normal overlay cannot reliably answer/decline system calls on API 29.

## License

Personal project. Use and modify as you like.
