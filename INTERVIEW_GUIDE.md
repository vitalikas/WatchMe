# TV2 Play Interview Demo App - Technical Guide

## 🎯 Overview

This Android app demonstrates key streaming technologies required for the **TV2 Play Developer** position:
- ✅ Video streaming (HLS & DASH)
- ✅ DRM implementation (Widevine)
- ✅ Linear Ad Replacement (LAR)
- ✅ Video Analytics
- ✅ Android TV support

---

## 📱 App Structure (3 Screens)

### 1. **Browse/Catalog Screen**
- Grid layout of video content
- Shows DRM-protected and ad-enabled content badges
- Android TV compatible navigation
- Displays video metadata (duration, category)

**Key Technologies:**
- Jetpack Compose with Material3
- Lazy Grid for performance
- Coil for image loading
- Responsive layout for TV and mobile

### 2. **Video Player Screen**
- Full-featured video playback with ExoPlayer
- DRM-protected content support
- LAR ad insertion indicators
- Real-time analytics tracking
- Quality metrics display

**Key Technologies:**
- ExoPlayer (Media3)
- Widevine DRM
- Adaptive bitrate streaming
- Custom player controls

### 3. **Analytics Dashboard**
- Real-time streaming metrics
- Event log tracking
- Performance monitoring
- QoS (Quality of Service) metrics

**Key Metrics Tracked:**
- Videos watched
- Total play time
- Buffering events
- Ad impressions
- DRM initializations
- Error rates

---

## 🔑 Core Features Explained

### 1. Video Streaming Technologies

#### **HLS (HTTP Live Streaming)**
```kotlin
// Example HLS stream in SampleContent.kt
videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
```

**What it is:** Apple's HTTP-based adaptive streaming protocol
**Why TV2 uses it:** 
- Industry standard for live streaming
- Excellent browser/mobile support
- Adaptive bitrate switching
- Low latency delivery

#### **DASH (Dynamic Adaptive Streaming over HTTP)**
```kotlin
// Example DASH stream
videoUrl = "https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd"
```

**What it is:** ISO standard for adaptive streaming
**Why TV2 uses it:**
- Open standard (not proprietary)
- Better multi-DRM support
- More efficient encoding options

### 2. DRM Implementation (Widevine)

**Location:** `model/VideoContent.kt`, `ui/PlayerScreen.kt`

```kotlin
data class DrmConfig(
    val licenseUrl: String,
    val scheme: DrmScheme = DrmScheme.WIDEVINE
)
```

**What is DRM?**
Digital Rights Management protects premium content from piracy.

**Widevine Levels:**
- **L1:** Hardware-backed, HD/4K content (most secure)
- **L2:** Software-based, SD content
- **L3:** Basic protection

**Implementation in PlayerScreen.kt:**
```kotlin
val drmCallback = HttpMediaDrmCallback(
    video.drmConfig.licenseUrl,
    httpDataSourceFactory
)

val drmSessionManager = DefaultDrmSessionManager.Builder()
    .setUuidAndExoMediaDrmProvider(
        C.WIDEVINE_UUID,
        FrameworkMediaDrm.DEFAULT_PROVIDER
    )
    .build(drmCallback)
```

**Key Concepts:**
- License acquisition from DRM server
- Content encryption/decryption
- Device attestation
- Secure playback path

### 3. LAR (Linear Ad Replacement)

**Location:** `streaming/LinearAdReplacementManager.kt`

**What is LAR?**
Technology that replaces ads in linear/live streams with targeted advertisements in real-time.

**How it works:**
1. **SCTE-35 Markers:** Embedded signals in the stream indicating ad breaks
2. **Ad Decision Server:** Determines which ads to show
3. **Dynamic Replacement:** Swaps content seamlessly during playback

**Implementation:**
```kotlin
fun checkForAdBreak(currentPosition: Long, videoId: String, hasAds: Boolean): AdPod? {
    return sampleAdPods.find { pod ->
        currentPosition >= pod.startPosition && 
        currentPosition < (pod.startPosition + pod.duration)
    }
}
```

**Key Features:**
- Predefined ad pods (simulates SCTE-35)
- Multiple ads per pod
- Position-based triggering
- Ad overlay indicators

**Production Considerations:**
- Server-Side Ad Insertion (SSAI)
- Client-Side Ad Insertion (CSAI)
- Ad tracking pixels
- Skip/block protection

### 4. Video Analytics

**Location:** `analytics/VideoAnalytics.kt`

**What's Tracked:**
```kotlin
sealed class AnalyticsEvent {
    VideoStarted      // User initiates playback
    VideoPlayed       // Playback in progress
    VideoPaused       // User pauses
    VideoCompleted    // Full video watched
    BufferingStarted  // Network issues
    BufferingEnded    // Buffer resolved
    QualityChanged    // ABR adjustment
    AdShown           // LAR ad impression
    DrmInitialized    // DRM setup
    ErrorOccurred     // Playback errors
}
```

**Why This Matters for TV2:**
- **Content Performance:** Which shows are popular?
- **QoS Monitoring:** Are users experiencing buffering?
- **Ad Monetization:** Are ads being delivered?
- **Technical Health:** Error rates, DRM issues
- **User Engagement:** Watch time, completion rates

**Production Implementation:**
- Send to Firebase Analytics
- Custom backend endpoints
- Real-time dashboards (Grafana, Tableau)
- A/B testing integration

---

## 🏗️ Architecture & Best Practices

### Tech Stack
- **Kotlin:** Primary language
- **Jetpack Compose:** Modern UI
- **Media3 (ExoPlayer):** Video playback
- **Coroutines & Flow:** Async operations
- **Navigation Compose:** Screen navigation
- **Material3:** Design system

### Code Organization
```
composeApp/src/androidMain/kotlin/
├── model/              # Data models
│   └── VideoContent.kt
├── analytics/          # Analytics tracking
│   └── VideoAnalytics.kt
├── streaming/          # LAR implementation
│   └── LinearAdReplacementManager.kt
├── ui/                 # Screens
│   ├── BrowseScreen.kt
│   ├── PlayerScreen.kt
│   └── AnalyticsScreen.kt
└── navigation/         # App navigation
    └── Navigation.kt
```

### Key Design Patterns
- **Singleton:** Analytics tracker, LAR manager
- **Sealed Classes:** Type-safe analytics events
- **StateFlow:** Reactive state management
- **Repository Pattern:** Content data access
- **Factory Pattern:** ExoPlayer creation

---

## 🎤 Interview Discussion Points

### Technical Questions You Can Answer:

**1. "How would you optimize video startup time?"**
- Prefetch manifest files
- CDN edge caching
- Predictive preloading
- Lower initial bitrate selection

**2. "How do you handle network changes during playback?"**
- Adaptive Bitrate Streaming (ABR)
- ExoPlayer's LoadControl
- Buffer management strategies
- Quality degradation gracefully

**3. "What's the difference between client-side and server-side ad insertion?"**
- **Client-side (CSAI):** App fetches and stitches ads (used in this demo)
- **Server-side (SSAI):** CDN stitches ads before delivery (better for live)
- Trade-offs: SSAI prevents ad-blocking, CSAI allows better targeting

**4. "How would you implement offline downloads with DRM?"**
- ExoPlayer's DownloadManager
- Persistent license storage
- L1 Widevine for security
- Expiration policies

**5. "What metrics matter most for streaming QoS?"**
- **Startup time:** Time to first frame
- **Rebuffering ratio:** % of time buffering
- **Video Start Failures:** Playback errors
- **Bitrate distribution:** Quality delivery
- **Completion rate:** User engagement

### Architecture Decisions:

**Why Media3 (ExoPlayer)?**
- Industry standard for Android
- Excellent DRM support
- Customizable and extensible
- Active development by Google

**Why Kotlin Coroutines?**
- Modern async programming
- Lifecycle-aware
- Clean, readable code
- Better than callbacks/RxJava

**Why Jetpack Compose?**
- Modern Android UI toolkit
- Declarative paradigm
- Less boilerplate
- Better performance

---

## 🚀 Running the App

### Prerequisites
- Android Studio (latest)
- Android SDK 24+
- Physical device or emulator

### Build & Run
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Testing on Android TV
1. Enable Android TV emulator in AVD Manager
2. Select "Android TV (1080p)" device profile
3. Use D-pad navigation for TV controls

---

## 📊 Demo Flow for Interview

### Recommended Walkthrough:

1. **Start on Browse Screen**
   - Point out content badges (DRM, LAR)
   - Explain grid layout optimization
   - Show Android TV support

2. **Play DRM-Protected Content**
   - Select "DRM Protected Content (Widevine)"
   - Explain Widevine license acquisition
   - Show technical details card

3. **Play LAR-Enabled Content**
   - Select "Big Buck Bunny" (has LAR)
   - Wait for ad indicator overlay at ~2 minutes
   - Explain SCTE-35 simulation

4. **Check Analytics Dashboard**
   - Show tracked events
   - Explain each metric
   - Discuss production implementations

---

## 🎓 Learning Resources

### Official Documentation
- [ExoPlayer Developer Guide](https://developer.android.com/guide/topics/media/exoplayer)
- [Widevine DRM](https://www.widevine.com/)
- [HLS Specification](https://datatracker.ietf.org/doc/html/rfc8216)
- [DASH Standard](https://dashif.org/)

### Industry Standards
- **SCTE-35:** Digital Program Insertion Cueing
- **VAST/VPAID:** Video ad serving templates
- **IAB Standards:** Interactive Advertising Bureau

### TV2-Specific Research
- Study TV2 Play's current features
- Understand Danish market
- Know competitors (DR, Viaplay, Netflix)

---

## 💡 Potential Enhancements

### For Further Discussion:

1. **Live Streaming:**
   - Low-latency HLS (LL-HLS)
   - WebRTC for ultra-low latency
   - Live DVR functionality

2. **Advanced DRM:**
   - Multi-DRM support (PlayReady, FairPlay)
   - Watermarking for forensics
   - License rotation

3. **Enhanced Analytics:**
   - Heatmaps (where users drop off)
   - A/B testing framework
   - ML-based recommendations

4. **Accessibility:**
   - Closed captions (WebVTT)
   - Audio descriptions
   - Screen reader support

5. **Performance:**
   - Preloading strategies
   - CDN optimization
   - Battery efficiency

---

## ✅ Interview Success Tips

### Demonstrate:
- **Deep understanding** of streaming fundamentals
- **Problem-solving** approach to technical challenges
- **User-centric** thinking (UX over just features)
- **Production mindset** (monitoring, debugging, scale)

### Emphasize:
- You built this to **learn the domain**
- You understand **trade-offs** in architecture
- You can **explain complex topics** simply
- You're **excited about streaming tech**

### Questions to Ask:
- "What's TV2 Play's current CDN strategy?"
- "How do you balance ad load with user experience?"
- "What analytics tools does the team use?"
- "How do you handle peak traffic during live events?"

---

## 📝 Code Highlights to Discuss

### DRM Implementation
**File:** `ui/PlayerScreen.kt` (lines ~280-300)
```kotlin
val drmSessionManager = DefaultDrmSessionManager.Builder()
    .setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, ...)
```
*Show understanding of secure content delivery*

### LAR Logic
**File:** `streaming/LinearAdReplacementManager.kt` (lines ~50-60)
```kotlin
fun checkForAdBreak(currentPosition: Long, ...): AdPod?
```
*Demonstrate real-time ad insertion knowledge*

### Analytics Architecture
**File:** `analytics/VideoAnalytics.kt` (lines ~60-90)
```kotlin
object VideoAnalyticsTracker {
    fun trackEvent(event: AnalyticsEvent)
```
*Show production-ready monitoring approach*

---

## 🎉 Good Luck!

This app demonstrates you have:
- ✅ Hands-on experience with video streaming
- ✅ Understanding of DRM protection
- ✅ Knowledge of LAR/ad technologies
- ✅ Analytics and monitoring skills
- ✅ Android TV development capability

**Remember:** The goal isn't perfection—it's showing you can **learn complex domains quickly** and **build production-quality code**.

You've got this! 🚀
