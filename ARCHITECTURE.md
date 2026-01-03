# WatchMe Architecture Overview

## 📐 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        WatchMe App                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌───────────┐     ┌────────────┐     ┌──────────────┐    │
│  │  Browse   │────▶│   Player   │────▶│  Analytics   │    │
│  │  Screen   │     │   Screen   │     │  Dashboard   │    │
│  └───────────┘     └────────────┘     └──────────────┘    │
│       │                  │                     │           │
│       └──────────────────┴─────────────────────┘           │
│                          │                                 │
│                  ┌───────▼────────┐                        │
│                  │   Navigation   │                        │
│                  └────────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎬 Video Playback Flow

```
┌──────────────┐
│ User selects │
│    video     │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│ Check if DRM     │
│   protected?     │
└──────┬───────────┘
       │
       ├─── Yes ──▶ ┌────────────────────┐
       │            │ Request DRM license│
       │            │  from server       │
       │            └─────────┬──────────┘
       │                      │
       │                      ▼
       │            ┌────────────────────┐
       │            │ Initialize Widevine│
       │            │  DRM Session       │
       │            └─────────┬──────────┘
       │                      │
       └─── No ───▶          │
                              ▼
                   ┌────────────────────┐
                   │ Configure ExoPlayer│
                   │  MediaSource       │
                   └─────────┬──────────┘
                             │
                             ▼
                   ┌────────────────────┐
                   │ Load HLS/DASH      │
                   │   manifest         │
                   └─────────┬──────────┘
                             │
                             ▼
                   ┌────────────────────┐
                   │ Start playback &   │
                   │  track analytics   │
                   └────────────────────┘
```

---

## 🎯 LAR (Linear Ad Replacement) Flow

```
┌──────────────┐
│Video playing │
└──────┬───────┘
       │
       ▼
┌──────────────────────┐
│ Monitor playback     │
│   position (100ms)   │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐      No
│ Position matches  ────────▶ Continue
│  ad cue point?       │      monitoring
└──────┬───────────────┘
       │ Yes
       ▼
┌──────────────────────┐
│ Get Ad Pod for       │
│  current position    │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Show ad overlay      │
│  indicator           │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Track ad impression  │
│  in analytics        │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Continue until       │
│  ad pod ends         │
└──────────────────────┘
```

---

## 📊 Analytics Event Flow

```
┌─────────────────┐
│ Playback Event  │
│   (e.g., play)  │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│ VideoAnalyticsTracker   │
│   .trackEvent()         │
└────────┬────────────────┘
         │
         ├──▶ ┌──────────────────┐
         │    │ Add to event log │
         │    └──────────────────┘
         │
         ├──▶ ┌──────────────────┐
         │    │ Update summary   │
         │    │   statistics     │
         │    └──────────────────┘
         │
         ├──▶ ┌──────────────────┐
         │    │ Emit to StateFlow│
         │    │   (reactive UI)  │
         │    └──────────────────┘
         │
         └──▶ ┌──────────────────┐
              │ Log to console   │
              │ (prod: send to   │
              │  backend)        │
              └──────────────────┘
```

---

## 🏗️ Component Architecture

### Data Layer
```
┌────────────────────────────────────┐
│         model/                     │
├────────────────────────────────────┤
│ • VideoContent                     │
│ • DrmConfig                        │
│ • DrmScheme                        │
│ • SampleContent                    │
└────────────────────────────────────┘
```

### Business Logic Layer
```
┌────────────────────────────────────┐
│      analytics/                    │
├────────────────────────────────────┤
│ • VideoAnalyticsTracker (Singleton)│
│ • AnalyticsEvent (Sealed Class)    │
│ • AnalyticsSummary                 │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│      streaming/                    │
├────────────────────────────────────┤
│ • LinearAdReplacementManager       │
│ • AdPod                            │
│ • Ad                               │
└────────────────────────────────────┘
```

### Presentation Layer
```
┌────────────────────────────────────┐
│          ui/                       │
├────────────────────────────────────┤
│ • BrowseScreen (Composable)        │
│ • PlayerScreen (Composable)        │
│ • AnalyticsScreen (Composable)     │
│ • Reusable UI components           │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│      navigation/                   │
├────────────────────────────────────┤
│ • AppNavigation                    │
│ • Screen (Sealed Class)            │
└────────────────────────────────────┘
```

---

## 🔄 State Management

### Analytics State Flow
```
VideoAnalyticsTracker
    │
    ├─▶ MutableStateFlow<List<AnalyticsEvent>>
    │       │
    │       └─▶ Observed by AnalyticsScreen
    │
    └─▶ MutableStateFlow<AnalyticsSummary>
            │
            └─▶ Observed by AnalyticsScreen
```

### LAR State Flow
```
LinearAdReplacementManager
    │
    ├─▶ MutableStateFlow<AdPod?>
    │       │
    │       └─▶ Observed by PlayerScreen
    │
    └─▶ MutableStateFlow<Boolean> (isAdPlaying)
            │
            └─▶ Observed by PlayerScreen
```

### ExoPlayer State
```
ExoPlayer
    │
    ├─▶ Player.Listener callbacks
    │       │
    │       ├─▶ onPlaybackStateChanged
    │       ├─▶ onIsPlayingChanged
    │       ├─▶ onPlayerError
    │       └─▶ Other events
    │
    └─▶ Updates trigger analytics events
```

---

## 🎨 UI Component Hierarchy

### Browse Screen
```
BrowseScreen
    │
    ├─▶ Scaffold
        │
        ├─▶ TopAppBar
        │   └─▶ Analytics IconButton
        │
        └─▶ Column
            ├─▶ Feature Card (highlights)
            └─▶ LazyVerticalGrid
                └─▶ VideoCard (multiple)
                    ├─▶ Thumbnail Image
                    ├─▶ Badges (DRM, LAR)
                    ├─▶ Duration overlay
                    └─▶ Video info
```

### Player Screen
```
PlayerScreen
    │
    ├─▶ Scaffold
        │
        ├─▶ TopAppBar
        │   └─▶ Back button
        │
        └─▶ Column
            ├─▶ PlayerView (AndroidView)
            │   ├─▶ ExoPlayer
            │   ├─▶ Ad overlay (if active)
            │   └─▶ Buffering indicator
            │
            └─▶ Video Information
                ├─▶ Title & description
                └─▶ Technical details card
```

### Analytics Screen
```
AnalyticsScreen
    │
    ├─▶ Scaffold
        │
        ├─▶ TopAppBar
        │   ├─▶ Back button
        │   └─▶ Clear button
        │
        └─▶ LazyColumn
            ├─▶ Summary metrics
            │   └─▶ MetricCard (multiple)
            │
            ├─▶ Most watched card
            │
            └─▶ Event log
                └─▶ EventCard (multiple)
```

---

## 🔌 External Dependencies

### Media Playback
- **Media3 ExoPlayer** - Video playback engine
- **Media3 DASH** - DASH format support
- **Media3 HLS** - HLS format support
- **Media3 UI** - Player UI components
- **Media3 Common** - Shared utilities

### UI & Navigation
- **Jetpack Compose** - UI framework
- **Material3** - Design components
- **Navigation Compose** - Screen navigation
- **Coil** - Image loading

### Android TV
- **Leanback** - TV-optimized components

---

## 🎯 Design Patterns Used

### Singleton Pattern
- **VideoAnalyticsTracker** - Single instance for analytics
- **LinearAdReplacementManager** - Single LAR coordinator

### Sealed Classes
- **AnalyticsEvent** - Type-safe event hierarchy
- **Screen** - Navigation route definitions

### Observer Pattern
- **StateFlow** - Reactive state updates
- **Player.Listener** - ExoPlayer callbacks

### Factory Pattern
- **createExoPlayer()** - Player instance creation
- **DRM session configuration**

### Repository Pattern
- **SampleContent** - Data source abstraction

---

## 🚀 Data Flow Example

### User plays a DRM-protected video:

1. **BrowseScreen**: User clicks video card
2. **Navigation**: Routes to PlayerScreen with video ID
3. **PlayerScreen**: Retrieves video from SampleContent
4. **createExoPlayer()**: Creates player with DRM config
5. **DrmSessionManager**: Requests license from server
6. **ExoPlayer**: Receives decryption keys
7. **Analytics**: Tracks VideoStarted event
8. **Analytics**: Tracks DrmInitialized event
9. **ExoPlayer**: Begins playback
10. **LAR Manager**: Monitors for ad breaks
11. **Analytics**: Tracks VideoPlayed events
12. **User**: Can navigate to Analytics Dashboard
13. **AnalyticsScreen**: Displays all tracked events

---

## 🔒 Security Considerations

### DRM
- Secure playback path (hardware-backed on L1 devices)
- License caching with expiration
- Device attestation
- HTTPS for license requests

### Data
- No user data collection in demo
- Analytics stored locally
- Production would use encrypted transmission

---

## 📈 Scalability Considerations

### Current Implementation (Demo)
- Local video catalog
- Simulated LAR markers
- In-memory analytics
- Single-device usage

### Production Enhancements
- Backend API for content
- Real SCTE-35 marker detection
- Analytics backend (Firebase, Mixpanel)
- User authentication
- CDN integration
- Cloud DRM service
- Distributed caching
- Load balancing

---

## 🎯 Key Takeaways

### Architecture Strengths
✅ **Clean separation of concerns** - UI, business logic, data  
✅ **Reactive state management** - StateFlow for real-time updates  
✅ **Testable design** - Singleton managers, pure functions  
✅ **Extensible** - Easy to add features  
✅ **Production-ready patterns** - Industry standards

### Interview Points
- Explain each layer's responsibility
- Discuss state management choices
- Demonstrate data flow understanding
- Show scalability thinking

---

**This architecture demonstrates production-quality thinking while keeping the demo focused and understandable.**
