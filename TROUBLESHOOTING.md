# Troubleshooting Guide

## 🔧 Common Issues and Solutions

### Live Stream Not Playing

**Issue:** "Live Stream" videos don't play or show errors

**Reality Check:** 
True live streams (NASA, news channels) frequently change URLs or go offline. For a **stable demo**, we use:
- Looping streams (simulate live)
- Reliable test streams (Apple, Mux)

**Solutions:**

1. **Current Working Streams**
   The app now uses tested, reliable streams:

   ```kotlin
   // Option 1: Unified Streaming demo (current)
   videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
   
   // Option 2: Akamai test stream
   videoUrl = "https://moctobpltc-i.akamaihd.net/hls/live/571329/eight/playlist.m3u8"
   
   // Option 3: Apple's test stream
   videoUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"
   
   // Option 4: BBC test stream
   videoUrl = "https://rdmedia.bbc.co.uk/testcard/vod/manifests/avc-ctv-full.m3u8"
   ```

2. **Update in VideoContent.kt**
   Location: `composeApp/src/androidMain/kotlin/lt/vitalijus/watchme/model/VideoContent.kt`
   
   Find the "Live Stream Simulation" entry (around line 87) and replace the `videoUrl`.

3. **Check Network Connection**
   Make sure your device/emulator has internet access:
   ```bash
   adb shell ping -c 3 google.com
   ```

4. **Check Logcat for Errors**
   ```bash
   adb logcat | grep -E "ExoPlayer|HTTP|Network"
   ```

**Note for Interview:**
Live streams can be unreliable. If it doesn't work during demo, explain:
*"This simulates a live stream, but public test streams aren't always available. In production, TV2 Play would use their own CDN endpoints which are monitored 24/7."*

---

## Video Playback Issues

### Specific Video Not Working (Fixed)

**Issue:** Sintel video doesn't play or thumbnails don't load

**Status:** ✅ FIXED in latest version

**What was fixed:**
- Updated Sintel to use reliable Google Cloud Storage URL
- Changed all thumbnails to use Google's public sample images
- Replaced broken Wikipedia/CDN thumbnail links

**New reliable sources:**
- Video streams: Google Cloud Storage, Apple, Unified Streaming
- Thumbnails: Google TV sample images (guaranteed uptime)

**If still seeing issues:**
```bash
# Rebuild
./gradlew clean installDebug

# Check network
adb shell ping -c 3 commondatastorage.googleapis.com
```

### DRM Video Won't Play

**Issue:** "DRM Protected Content" shows error

**Possible Causes:**
- Device doesn't support Widevine L1/L2
- Network issue reaching DRM license server
- Widevine test server is down

**Solutions:**

1. **Check Device DRM Support**
   ```bash
   adb shell dumpsys media.drm
   ```

2. **Try Non-DRM Videos First**
   Test with "Big Buck Bunny" or "Sintel" to ensure basic playback works.

3. **Check Logs**
   ```bash
   adb logcat | grep -E "DRM|Widevine|License"
   ```

**Interview Talking Point:**
*"DRM implementation requires device support. In production, we'd detect device capabilities and only offer DRM-protected quality levels to compatible devices."*

---

### Video Buffering Issues

**Issue:** Excessive buffering or stuttering

**Solutions:**

1. **Check Network Speed**
   Test on Wi-Fi instead of cellular if possible.

2. **Clear App Data**
   ```bash
   adb shell pm clear lt.vitalijus.watchme
   ```

3. **Check Device Resources**
   Close other apps to free up memory.

**Interview Point:**
*"ExoPlayer has configurable buffer strategies. We can tune for different scenarios - longer buffers for stability or shorter for live latency."*

---

## Build Issues

### Dependencies Not Resolving

**Issue:** Gradle sync fails

**Solutions:**

1. **Refresh Dependencies**
   ```bash
   ./gradlew --refresh-dependencies
   ```

2. **Clean and Rebuild**
   ```bash
   ./gradlew clean build
   ```

3. **Check Internet Connection**
   Ensure Maven Central and Google repositories are accessible.

4. **Invalidate Caches** (Android Studio)
   File → Invalidate Caches → Invalidate and Restart

---

### Compilation Errors

**Issue:** Kotlin compilation fails

**Solutions:**

1. **Check Kotlin Version**
   Ensure using Kotlin 2.3.0 (check `libs.versions.toml`)

2. **Sync Gradle Files**
   ```bash
   ./gradlew --stop
   ./gradlew build
   ```

3. **Check for Missing Imports**
   Most common: Material Icons need to be imported explicitly.

---

## Runtime Issues

### Video Restarts on Configuration Changes (Fixed)

**Issue:** Video starts from beginning when folding/unfolding device or rotating screen

**Status:** ✅ FIXED in latest version

**What was fixed:**
- Added `rememberSaveable` to persist playback position across configuration changes
- Saved playback state (position and play/pause state)
- Automatically restores position when Activity is recreated
- Works for: fold/unfold, rotation, split-screen, picture-in-picture

**How it works now:**
1. User watches video at position 2:30
2. User folds the device (Activity destroyed)
3. Device unfolds (Activity recreated)
4. Video resumes at 2:30, maintaining play state

**Technical details:**
```kotlin
// Saves across configuration changes
var savedPosition by rememberSaveable { mutableStateOf(0L) }
var wasPlaying by rememberSaveable { mutableStateOf(true) }

// Restores on recreation
player.seekTo(initialPosition)
player.playWhenReady = playWhenReady
```

**Testing:**
```bash
# Rebuild
./gradlew installDebug

# Test on Fold emulator:
# 1. Play video for 30 seconds
# 2. Fold the device (Closed state)
# 3. Unfold the device (Open state)
# 4. Video should resume from where you left off
```

### App Crashes on Launch

**Solutions:**

1. **Check Logcat**
   ```bash
   adb logcat -d | grep -E "FATAL|AndroidRuntime"
   ```

2. **Verify Manifest Permissions**
   Ensure INTERNET permission is declared.

3. **Reinstall**
   ```bash
   ./gradlew uninstallAll
   ./gradlew installDebug
   ```

---

### Analytics Not Tracking

**Issue:** Analytics Dashboard shows no events

**Cause:** Events are tracked but UI isn't updating.

**Solutions:**

1. **Play a Video First**
   Analytics only track when videos are played.

2. **Check Logs**
   ```bash
   adb logcat | grep "Analytics"
   ```
   You should see "📊 Analytics Event:" messages.

3. **Restart App**
   Analytics state persists in memory only.

### Analytics Issues (Fixed)

**Issue:** Total play time shows 0 or most watched shows video ID instead of name

**Status:** ✅ FIXED in latest version

**What was fixed:**
- Total play time now accumulates when videos are paused (not just completed)
- Most watched video now shows the video title instead of ID
- Play time tracking improved with timestamp tracking

**If still seeing issues:**
1. Rebuild: `./gradlew clean installDebug`
2. Clear app data: `adb shell pm clear lt.vitalijus.watchme`
3. Play videos again to generate new analytics

---

### LAR Ads Not Showing

**Issue:** Ad overlays don't appear during playback

**Cause:** Ad timing or video selection issue.

**Solutions:**

1. **Use Correct Video**
   Only "Big Buck Bunny" and "Live Stream Simulation" have LAR enabled.

2. **Skip to Ad Position**
   LAR ads appear at:
   - ~2 minutes (120 seconds)
   - ~5 minutes (300 seconds)
   - ~8 minutes (480 seconds)

3. **Check Logs**
   ```bash
   adb logcat | grep "LAR"
   ```
   Look for "LAR: Starting ad pod" messages.

### LAR Ad Issues (Fixed)

**Issue 1:** Frame hangs/stutters when ad appears  
**Issue 2:** Ad overlay doesn't disappear after seeking/scrubbing

**Status:** ✅ FIXED in latest version

**What was fixed:**
- Reduced ad checking frequency from 100ms to 250ms (prevents frame drops)
- Added proper seek detection - ad disappears when you scrub outside ad range
- Improved ad range checking logic
- Added `isInAdRange()` helper for cleaner state management

**How it works now:**
- Ad overlay appears when playback position enters ad range
- Ad overlay disappears when:
  - Position moves past ad duration naturally
  - User seeks/scrubs before or after the ad range
  - User leaves the player screen

**If still seeing issues:**
```bash
# Rebuild with fixes
./gradlew clean installDebug

# Test by:
# 1. Play "Big Buck Bunny"
# 2. Skip to 2:00 (ad should appear)
# 3. Seek to 4:00 (ad should disappear immediately)
# 4. Seek back to 2:00 (ad should reappear)
```

---

## Android TV Issues

### App Not Appearing on TV

**Solutions:**

1. **Check Leanback Intent Filter**
   Verify `AndroidManifest.xml` has:
   ```xml
   <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
   ```

2. **Install on TV Emulator**
   ```bash
   adb -e install composeApp/build/outputs/apk/debug/composeApp-debug.apk
   ```

3. **Check TV Compatibility**
   Ensure touchscreen is marked as not required in manifest.

---

### D-pad Navigation Not Working

**Cause:** Compose components not focusable by default.

**Solution:**
Components need explicit focus handling. Check `BrowseScreen.kt` for examples.

---

## Network Issues

### "Unable to Connect" Errors

**Solutions:**

1. **Check Cleartext Traffic**
   `AndroidManifest.xml` should have:
   ```xml
   android:usesCleartextTraffic="true"
   ```
   (Note: For HTTPS URLs this shouldn't be needed)

2. **Test URL Directly**
   ```bash
   curl -I "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
   ```

3. **Try Different Video**
   If one video fails, test others to isolate the issue.

---

## Interview Demo Backup Plan

### If Live Demo Fails

**Have Ready:**
1. **Screenshots** of working app
2. **Code walkthrough** as alternative
3. **Explanation** of what would happen
4. **Log output** showing it worked before

**What to Say:**
*"I've thoroughly tested this, but live demos can be unpredictable. Let me walk you through the code instead and show you the architecture..."*

Then pivot to discussing:
- Implementation choices
- Production considerations
- How you'd handle this in production
- Your problem-solving approach

**Remember:** Interviewers understand technical issues happen. How you handle it matters more!

---

## Getting Help

### Debug Process

1. **Check Logs**
   ```bash
   adb logcat | grep -E "WatchMe|ExoPlayer|Analytics|LAR|DRM"
   ```

2. **Verify Build**
   ```bash
   ./gradlew clean assembleDebug
   ```

3. **Test Systematically**
   - Can you open the app? ✓
   - Can you navigate screens? ✓
   - Can any video play? ✓
   - Which specific feature fails? ✓

4. **Isolate the Issue**
   - Test on different device
   - Try different network
   - Use different video

---

## Pre-Interview Checklist

### Test Everything (30 min before)

- [ ] Build succeeds: `./gradlew assembleDebug`
- [ ] App installs: `./gradlew installDebug`
- [ ] App launches
- [ ] Browse screen loads
- [ ] At least 2 videos play (any 2)
- [ ] Analytics dashboard shows data
- [ ] Navigation works

### Backup Plans Ready

- [ ] Code is clean and well-commented
- [ ] Can explain implementation without demo
- [ ] Know which videos are most reliable
- [ ] Have screenshots as backup
- [ ] Practiced explaining architecture

**Remember:** You've built something impressive. One feature not working perfectly doesn't diminish that! 💪

---

## Quick Fixes During Interview

### If a video won't play:
*"Let me try a different one - public test streams can be unreliable. In production, we'd use TV2's CDN."* → Play Big Buck Bunny

### If analytics seems stuck:
*"Let me restart the app to refresh the state."* → Force stop and relaunch

### If LAR ads don't appear:
*"The ad markers are at 2, 5, and 8 minutes. Let me show you the code that handles detection instead."* → Open LinearAdReplacementManager.kt

---

**Stay calm, stay confident! You've got this! 🚀**
