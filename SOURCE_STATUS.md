# Source status

This repository was created from a reconstructed snapshot of **Project Tsuki no Me** after a sandbox session reset.

## Present on `main`

- Gradle project (`settings.gradle.kts`, root + app `build.gradle.kts`, `gradle.properties`)
- `AndroidManifest.xml` (unlock overlay + default-dialer / InCallService wiring)
- Core modules: `Rules`, `SettingsStore`, `VideoLibrary`, `PickedVideos`, `CallBridge`, `CallerVideo`, `TsukiCallService`, `BootReceiver`, `PlayerActivity`, `DialActivity`
- Call / player / dial layouts and icons
- Themes, colors, strings

## Not yet restored (needed for a full build)

These existed in the working 1.0.0 / in-progress 1.1.0 tree and still need to be re-added:

- `MainActivity`, `SettingsActivity`, `AppsActivity`, `SplashActivity`
- `TsukiWatchService`, `OverlayPlayer`, `ForegroundApps`, `VideoScanner`, `ScanRunner`
- `CallActivity`, `MoonPhaseView`, `PullScanLayout`
- Remaining layouts: `activity_main`, `activity_settings`, `activity_apps`, `activity_splash`, list item layouts

Package: `me.tsukinome.project`  
Version target: 1.1.0 (versionCode 2)  
minSdk 29 / targetSdk 29 / armeabi-v7a

If you still have the installed APK or a local copy of the earlier source, that is the authoritative 1.0.0 build; this repo holds the reconstructed 1.1.0-oriented modules.
