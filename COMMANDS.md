# WatchMe - Command Reference

Quick reference for building, running, and testing the app.

---

## 🏗️ Build Commands

### Build the app (debug)
```bash
./gradlew assembleDebug
```

### Build the app (release)
```bash
./gradlew assembleRelease
```

### Clean build
```bash
./gradlew clean build
```

### Check dependencies
```bash
./gradlew dependencies
```

---

## 📱 Installation Commands

### Install on connected device (debug)
```bash
./gradlew installDebug
```

### Install on connected device (release)
```bash
./gradlew installRelease
```

### Uninstall from device
```bash
./gradlew uninstallAll
```

### Build and install in one command
```bash
./gradlew installDebug
```

---

## 🔍 Debugging Commands

### Check for compilation errors
```bash
./gradlew compileDebugKotlinAndroid
```

### Run lint checks
```bash
./gradlew lint
```

### Generate lint report
```bash
./gradlew lintDebug
# Report: build/reports/lint-results-debug.html
```

### View build configuration
```bash
./gradlew projects
```

---

## 📊 Testing Commands

### Run unit tests
```bash
./gradlew test
```

### Run Android instrumented tests
```bash
./gradlew connectedAndroidTest
```

### Test specific variant
```bash
./gradlew testDebugUnitTest
```

---

## 🔧 Android Device Commands (adb)

### List connected devices
```bash
adb devices
```

### Install APK manually
```bash
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### Uninstall app
```bash
adb uninstall lt.vitalijus.watchme
```

### View app logs
```bash
adb logcat | grep "WatchMe"
```

### View ExoPlayer logs
```bash
adb logcat | grep "ExoPlayer"
```

### View analytics logs
```bash
adb logcat | grep "Analytics"
```

### Clear app data
```bash
adb shell pm clear lt.vitalijus.watchme
```

### Start app activity
```bash
adb shell am start -n lt.vitalijus.watchme/.MainActivity
```

### Force stop app
```bash
adb shell am force-stop lt.vitalijus.watchme
```

---

## 📺 Android TV Emulator Commands

### List available AVDs
```bash
emulator -list-avds
```

### Start Android TV emulator
```bash
emulator -avd Android_TV_1080p_API_34
```

### Install on TV emulator
```bash
adb -e install composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

---

## 🚀 Quick Demo Setup

### 1. Build and install in one go
```bash
./gradlew installDebug && adb shell am start -n lt.vitalijus.watchme/.MainActivity
```

### 2. Watch logs while running
```bash
adb logcat -c && adb logcat | grep -E "WatchMe|ExoPlayer|Analytics"
```

### 3. Restart app (useful for testing)
```bash
adb shell am force-stop lt.vitalijus.watchme && adb shell am start -n lt.vitalijus.watchme/.MainActivity
```

---

## 🐛 Debugging Specific Issues

### Video playback issues
```bash
adb logcat | grep -E "ExoPlayer|MediaCodec|DRM"
```

### DRM issues
```bash
adb logcat | grep -E "DRM|Widevine|License"
```

### Analytics issues
```bash
adb logcat | grep "Analytics"
```

### LAR issues
```bash
adb logcat | grep "LAR"
```

### Network issues
```bash
adb logcat | grep -E "HTTP|Network|Connection"
```

---

## 📦 APK Information

### Get APK path
```bash
./gradlew assembleDebug --quiet
echo "APK: composeApp/build/outputs/apk/debug/composeApp-debug.apk"
```

### Check APK size
```bash
ls -lh composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### Analyze APK
```bash
# In Android Studio: Build > Analyze APK...
# Or use bundletool:
bundletool dump manifest --bundle=composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

---

## 🔄 Gradle Daemon

### Stop Gradle daemon
```bash
./gradlew --stop
```

### Build without daemon (slower, but fresh)
```bash
./gradlew assembleDebug --no-daemon
```

---

## 📊 Performance Profiling

### Build with profiling enabled
```bash
./gradlew assembleDebug -Pandroid.enableAdditionalTestOutput=true
```

### Capture performance trace
```bash
adb shell am start -n lt.vitalijus.watchme/.MainActivity --start-profiler trace.prof
adb shell am broadcast -a lt.vitalijus.watchme.STOP_PROFILER
adb pull /data/local/tmp/trace.prof
```

---

## 🎯 Interview Demo Script

### Full demo setup (run before presenting)
```bash
# 1. Clean build
./gradlew clean

# 2. Build debug APK
./gradlew assembleDebug

# 3. Install on device
./gradlew installDebug

# 4. Start logcat in separate terminal
adb logcat -c && adb logcat | grep -E "Analytics|LAR|DRM" > demo-logs.txt &

# 5. Launch app
adb shell am start -n lt.vitalijus.watchme/.MainActivity

echo "✅ Demo ready!"
```

### During demo - Quick restart
```bash
adb shell am force-stop lt.vitalijus.watchme && sleep 1 && adb shell am start -n lt.vitalijus.watchme/.MainActivity
```

---

## 🔍 Troubleshooting

### Build fails with dependency issues
```bash
./gradlew --refresh-dependencies
./gradlew clean build
```

### Android Studio sync issues
```bash
./gradlew --stop
# Then in Android Studio: File > Invalidate Caches > Invalidate and Restart
```

### Device not recognized
```bash
adb kill-server
adb start-server
adb devices
```

### App crashes on launch
```bash
# Check crash logs
adb logcat -d | grep -i "fatal\|exception"

# Clear data and reinstall
adb shell pm clear lt.vitalijus.watchme
./gradlew installDebug
```

---

## 📝 Useful Aliases (Optional)

Add these to your `.bashrc` or `.zshrc`:

```bash
# WatchMe shortcuts
alias wm-build="cd ~/AndroidStudioProjects/WatchMe && ./gradlew assembleDebug"
alias wm-install="cd ~/AndroidStudioProjects/WatchMe && ./gradlew installDebug"
alias wm-run="adb shell am start -n lt.vitalijus.watchme/.MainActivity"
alias wm-logs="adb logcat | grep -E 'WatchMe|ExoPlayer|Analytics|LAR|DRM'"
alias wm-restart="adb shell am force-stop lt.vitalijus.watchme && adb shell am start -n lt.vitalijus.watchme/.MainActivity"

# Quick demo
alias wm-demo="cd ~/AndroidStudioProjects/WatchMe && ./gradlew installDebug && wm-run"
```

Then use:
```bash
wm-demo  # Build, install, and run
wm-logs  # Watch relevant logs
```

---

## 📱 Device Testing

### Test on different screen sizes
```bash
# Phone
adb shell wm size 1080x1920

# Tablet
adb shell wm size 1600x2560

# TV
adb shell wm size 1920x1080

# Reset to default
adb shell wm size reset
```

### Test on different densities
```bash
# High DPI
adb shell wm density 480

# Medium DPI
adb shell wm density 320

# Reset
adb shell wm density reset
```

---

## 🎬 Quick Video Testing

### Test specific video by ID
```bash
# Open app to specific video (would need deep link implementation)
adb shell am start -W -a android.intent.action.VIEW \
  -d "watchme://video/1" \
  lt.vitalijus.watchme
```

---

## ✅ Pre-Interview Checklist Commands

Run these before your interview to ensure everything works:

```bash
# 1. Clean build
./gradlew clean build

# 2. Install on device
./gradlew installDebug

# 3. Test app launch
adb shell am start -n lt.vitalijus.watchme/.MainActivity

# 4. Check for any crash logs
adb logcat -d | grep -i exception

# 5. Verify video playback (manual test required)

# 6. Check analytics tracking (manual test required)

# 7. Verify Android TV compatibility (if available)

echo "✅ All checks complete! Ready for interview."
```

---

## 🚀 Quick Reference Card

**Build & Install:**
```bash
./gradlew installDebug
```

**Run:**
```bash
adb shell am start -n lt.vitalijus.watchme/.MainActivity
```

**Logs:**
```bash
adb logcat | grep -E "Analytics|LAR|DRM"
```

**Restart:**
```bash
adb shell am force-stop lt.vitalijus.watchme && adb shell am start -n lt.vitalijus.watchme/.MainActivity
```

---

**Keep this file handy during your demo! 📋**
