# 🎬 WatchMe - TV2 Play Interview Demo App

A comprehensive Android streaming application built to demonstrate expertise in video streaming technologies for the **TV2 Play Developer** position.

---

## 🚀 Quick Start

### Build & Run
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### What to Read First
1. **📄 QUICK_START.md** - Overview and demo instructions
2. **📋 INTERVIEW_CHEATSHEET.md** - Key concepts and Q&A
3. **📚 INTERVIEW_GUIDE.md** - Deep technical explanations
4. **📊 APP_SUMMARY.md** - Complete feature list

---

## ✅ Features Implemented

### Core Streaming Technologies
- ✅ **HLS & DASH Streaming** - Both major formats
- ✅ **ExoPlayer (Media3)** - Industry-standard player
- ✅ **Adaptive Bitrate** - Automatic quality switching
- ✅ **Multiple video sources** - 6 different test streams

### DRM (Digital Rights Management)
- ✅ **Widevine DRM** - Content protection
- ✅ **License acquisition** - DRM server integration
- ✅ **Secure playback** - Protected content delivery
- ✅ **DRM analytics** - Tracking initialization

### LAR (Linear Ad Replacement)
- ✅ **SCTE-35 simulation** - Ad marker implementation
- ✅ **Ad pod management** - Multiple ads per break
- ✅ **Dynamic insertion** - Position-based triggering
- ✅ **Ad overlays** - Visual indicators
- ✅ **Ad analytics** - Impression tracking

### Video Analytics
- ✅ **Comprehensive tracking** - 10 event types
- ✅ **Real-time dashboard** - Live metrics
- ✅ **QoS monitoring** - Buffering, errors, quality
- ✅ **Summary statistics** - KPI cards
- ✅ **Event log** - Recent activity

### Android TV Support
- ✅ **Leanback launcher** - TV home screen integration
- ✅ **D-pad navigation** - Remote control support
- ✅ **TV-optimized UI** - 10-foot interface
- ✅ **Dual compatibility** - Mobile + TV

---

## 📱 App Screens

### 1. Browse/Catalog
Content discovery with video thumbnails, DRM/LAR badges, and navigation.

### 2. Video Player
Full-featured playback with ExoPlayer, DRM protection, LAR ad insertion, and real-time analytics.

### 3. Analytics Dashboard
Comprehensive metrics including videos watched, play time, buffering, ads shown, DRM initializations, and errors.

---

## 🎯 Sample Videos

| Video | Format | Features |
|-------|--------|----------|
| Big Buck Bunny | HLS | LAR ads enabled |
| Elephants Dream | DASH | No ads |
| Sintel | HLS | High quality |
| DRM Protected | DASH | Widevine encryption |
| Live Stream | HLS | LAR simulation |
| Multi-bitrate | HLS | ABR test |

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Video:** Media3 (ExoPlayer)
- **Navigation:** Navigation Compose
- **Async:** Coroutines + StateFlow
- **Images:** Coil
- **Android TV:** Leanback

---

## 📂 Project Structure

```
WatchMe/
├── composeApp/src/androidMain/kotlin/lt/vitalijus/watchme/
│   ├── model/                  # Data models
│   │   └── VideoContent.kt     # Video + DRM config
│   ├── analytics/              # Analytics system
│   │   └── VideoAnalytics.kt
│   ├── streaming/              # LAR implementation
│   │   └── LinearAdReplacementManager.kt
│   ├── ui/                     # Screens
│   │   ├── BrowseScreen.kt
│   │   ├── PlayerScreen.kt
│   │   └── AnalyticsScreen.kt
│   └── navigation/             # App navigation
│       └── Navigation.kt
│
├── QUICK_START.md             # Quick overview
├── INTERVIEW_CHEATSHEET.md    # Key concepts
├── INTERVIEW_GUIDE.md         # Technical deep-dive
└── APP_SUMMARY.md             # Feature summary
```

---

## 🎤 Interview Demo Flow

### 1. Introduction (30 seconds)
*"I built this 3-screen streaming app to demonstrate TV2 Play's key technologies: HLS/DASH streaming, Widevine DRM, Linear Ad Replacement, video analytics, and Android TV support. I had no prior streaming experience but researched industry standards and implemented production-quality patterns."*

### 2. Browse Screen (1 minute)
- Show content grid
- Point out DRM and LAR badges
- Navigate to analytics dashboard

### 3. Player Screen - DRM (2 minutes)
- Play "DRM Protected Content"
- Explain Widevine license acquisition
- Show technical details card
- Discuss secure playback path

### 4. Player Screen - LAR (2 minutes)
- Play "Big Buck Bunny"
- Fast forward to ~2 minutes
- Ad overlay appears
- Explain SCTE-35 marker simulation

### 5. Analytics Dashboard (2 minutes)
- Show tracked metrics
- Review event log
- Discuss production analytics
- Explain QoS monitoring

### 6. Q&A
Reference specific code files and implementation details.

---

## 🔑 Key Interview Points

### What This Demonstrates

**Technical Competence**
- Complex streaming implementation
- Modern Android development
- Clean code architecture
- Best practices

**Learning Ability**
- Learned streaming tech from scratch
- Quick domain understanding
- Applied knowledge practically
- Self-directed learning

**Professional Approach**
- Production-quality code
- Comprehensive documentation
- Analytics mindset
- User experience focus

**Initiative**
- Built proactively for interview
- Exceeded basic requirements
- Researched industry standards
- Thorough preparation

---

## 💡 Discussion Topics

### Technical Questions
- How ExoPlayer handles adaptive bitrate
- DRM license acquisition flow
- LAR server-side vs client-side
- Video analytics best practices
- Android TV optimization

### TV2 Play Specific
- Live sports streaming challenges
- Peak traffic handling
- Content rights management
- Multi-device synchronization
- Danish market considerations

### Improvements
- Offline playback with DRM
- Enhanced analytics backend
- Live streaming low-latency
- Subtitle/caption support
- Advanced UI polish

---

## 📚 Additional Documentation

### Technical Details
- **INTERVIEW_GUIDE.md** - Complete technical explanations
- **Code comments** - Inline documentation
- **Architecture patterns** - MVVM, Repository, Singleton

### Interview Prep
- **INTERVIEW_CHEATSHEET.md** - Quick reference
- **APP_SUMMARY.md** - Feature overview
- **Sample Q&A** - Common questions

---

## ✅ Build Status

✅ **Builds successfully** - All dependencies resolved  
✅ **No errors** - Clean compilation  
⚠️ **Minor warnings** - Deprecation notices (non-blocking)  
✅ **Ready to demo** - Fully functional

---

## 🎯 What Makes This Strong

### 1. Demonstrates All Required Skills
- ✅ Video streaming technologies
- ✅ DRM implementation
- ✅ LAR understanding
- ✅ Analytics tracking
- ✅ Android TV development

### 2. Shows Learning Ability
- Built from zero streaming knowledge
- Researched industry standards
- Applied concepts practically
- Production-ready implementation

### 3. Professional Quality
- Clean code architecture
- Comprehensive documentation
- Error handling
- User experience focus

### 4. Interview Ready
- Working demo app
- Technical explanations
- Code walkthrough prepared
- Questions anticipated

---

## 🚀 Next Steps

### Before Interview
- [x] Build and test app
- [ ] Read all documentation
- [ ] Practice demo flow
- [ ] Prepare questions for TV2

### During Interview
- [ ] Demonstrate app features
- [ ] Explain technical choices
- [ ] Discuss improvements
- [ ] Ask about TV2's challenges

### After Demo
- [ ] Answer technical questions
- [ ] Reference specific code
- [ ] Show enthusiasm
- [ ] Ask next steps

---

## 📞 Key Files to Reference

| Topic | File |
|-------|------|
| DRM Implementation | `ui/PlayerScreen.kt` (lines 350-400) |
| LAR Logic | `streaming/LinearAdReplacementManager.kt` |
| Analytics | `analytics/VideoAnalytics.kt` |
| Data Models | `model/VideoContent.kt` |
| Navigation | `navigation/Navigation.kt` |

---

## 🎉 You're Ready!

This project demonstrates you have the **technical skills**, **learning ability**, and **professional approach** that TV2 Play needs.

### Key Message
*"I built this to show I can quickly learn complex domains and deliver production-quality code. I'm excited about streaming technology and ready to contribute to TV2 Play's success."*

---

**Good luck with your interview! 🚀📺**

*Built with ❤️ for the TV2 Play interview*
