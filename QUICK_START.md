# WatchMe - TV2 Play Interview Demo App

## 🚀 Quick Start

### What This App Demonstrates
✅ **Video Streaming** (HLS & DASH formats)  
✅ **Widevine DRM** (Digital Rights Management)  
✅ **LAR** (Linear Ad Replacement)  
✅ **Video Analytics** (Comprehensive tracking)  
✅ **Android TV Support** (Leanback launcher)

---

## 📱 3 Screens

1. **Browse Screen** - Video catalog with content grid
2. **Player Screen** - ExoPlayer with DRM and LAR
3. **Analytics Dashboard** - Real-time streaming metrics

---

## 🎬 Demo Videos Included

### Standard Streaming
- **Big Buck Bunny** (HLS with LAR ads)
- **Elephants Dream** (DASH format)
- **Sintel** (High quality HLS)

### Advanced Features
- **DRM Protected Content** (Widevine encryption)
- **Live Stream Simulation** (Looping HLS stream with LAR)
- **Multi-bitrate Test** (Adaptive streaming)

---

## 🔑 Key Technologies

### ExoPlayer (Media3)
Industry-standard Android video player used by YouTube, Netflix, etc.

### Widevine DRM
Google's DRM solution protecting premium content:
- L1: Hardware-backed (HD/4K)
- L2: Software-based (SD)
- L3: Basic protection

### LAR (Linear Ad Replacement)
Simulates SCTE-35 ad insertion markers:
- Ad pods at specific timestamps
- Multiple ads per break
- Ad overlay indicators

### Analytics Tracking
Events tracked:
- Video start/play/pause/complete
- Buffering start/end
- Quality changes
- Ad impressions
- DRM initialization
- Playback errors

---

## 🏗️ Project Structure

```
composeApp/src/androidMain/kotlin/lt/vitalijus/watchme/
├── model/
│   └── VideoContent.kt        # Video data & DRM config
├── analytics/
│   └── VideoAnalytics.kt      # Analytics tracking
├── streaming/
│   └── LinearAdReplacementManager.kt  # LAR implementation
├── ui/
│   ├── BrowseScreen.kt        # Content catalog
│   ├── PlayerScreen.kt        # Video player
│   └── AnalyticsScreen.kt     # Dashboard
└── navigation/
    └── Navigation.kt          # App navigation
```

---

## 🎤 Interview Talking Points

### DRM Implementation
**File:** `ui/PlayerScreen.kt`
- Widevine license acquisition
- Secure content decryption
- DRM session management

### LAR Technology
**File:** `streaming/LinearAdReplacementManager.kt`
- SCTE-35 marker simulation
- Ad pod management
- Position-based ad triggering

### Analytics Architecture
**File:** `analytics/VideoAnalytics.kt`
- Event-driven tracking
- Real-time metrics
- Production-ready patterns

---

## 💡 Key Interview Questions You Can Answer

**Q: How does adaptive bitrate streaming work?**
A: ExoPlayer automatically switches quality based on network conditions using HLS/DASH manifests that contain multiple bitrate variants.

**Q: What's the difference between HLS and DASH?**
A: HLS is Apple's proprietary format (`.m3u8`), DASH is open ISO standard (`.mpd`). DASH has better multi-DRM support, HLS has better Apple device support.

**Q: How do you protect video content?**
A: Using DRM (Widevine for Android) which encrypts content and requires license acquisition from a DRM server. L1 Widevine uses hardware-backed secure path.

**Q: What is LAR and why does TV2 need it?**
A: Linear Ad Replacement dynamically replaces ads in live streams using SCTE-35 markers. Critical for monetizing live TV on streaming platforms.

**Q: What video analytics matter most?**
A: Startup time, rebuffering ratio, completion rate, error rate, and bitrate distribution.

---

## 🔧 Build & Run

```bash
# Sync dependencies
./gradlew build

# Install on device
./gradlew installDebug

# Run on Android TV emulator
# 1. Create Android TV (1080p) AVD in Android Studio
# 2. Run the app
# 3. Use D-pad for navigation
```

---

## 📊 How to Demo

1. **Start Browse Screen**
   - Show video grid with badges (DRM, LAR)
   - Click Analytics icon to show dashboard

2. **Play DRM Content**
   - Select "DRM Protected Content (Widevine)"
   - Point out Widevine initialization in logs
   - Show technical details card

3. **Experience LAR**
   - Play "Big Buck Bunny" 
   - Fast forward to ~2 minutes
   - Ad overlay appears showing LAR in action

4. **Check Analytics**
   - Navigate to Analytics Dashboard
   - Show tracked metrics and events
   - Explain production implications

---

## 🎯 What This Proves

✅ **Technical Skills:** Can implement complex streaming features  
✅ **Domain Knowledge:** Understands DRM, LAR, video formats  
✅ **Production Mindset:** Analytics, monitoring, error handling  
✅ **Learning Ability:** Built this from scratch with no prior experience  
✅ **Code Quality:** Clean architecture, proper patterns

---

## 📚 Further Reading

- Full technical guide: `INTERVIEW_GUIDE.md`
- ExoPlayer docs: https://developer.android.com/guide/topics/media/exoplayer
- Widevine info: https://www.widevine.com/

---

Good luck with your TV2 Play interview! 🚀
