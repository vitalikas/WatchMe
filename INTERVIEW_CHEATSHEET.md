# TV2 Play Interview Cheat Sheet

## 🎯 30-Second Elevator Pitch

*"I built this streaming app to prepare for the TV2 Play interview. It demonstrates HLS/DASH streaming with Widevine DRM protection, Linear Ad Replacement (LAR) using SCTE-35 marker simulation, comprehensive video analytics tracking, and full Android TV support. I had no prior streaming experience, but I researched industry standards and implemented production-quality patterns."*

---

## 📺 Video Streaming Basics

### Formats You Need to Know

**HLS (HTTP Live Streaming)**
- Created by Apple
- `.m3u8` manifest files
- Best for: iOS/Safari, live streaming
- Used by: TV2 Play for broad compatibility

**DASH (Dynamic Adaptive Streaming)**
- ISO standard
- `.mpd` manifest files  
- Best for: Multi-DRM, flexibility
- Used by: YouTube, Netflix

**Both support:**
- Adaptive Bitrate (ABR) - switches quality automatically
- Segmented delivery - downloads small chunks
- Low latency options

### ABR (Adaptive Bitrate Streaming)
**How it works:**
1. Encoder creates multiple quality versions (360p, 480p, 720p, 1080p)
2. Manifest file lists all available qualities
3. Player measures network speed
4. Player switches quality seamlessly
5. User gets best experience for their connection

**ExoPlayer does this automatically!**

---

## 🔒 DRM (Digital Rights Management)

### Widevine Levels

| Level | Security | Max Quality | Use Case |
|-------|----------|-------------|----------|
| L1 | Hardware-backed | 4K/HD | Premium content |
| L2 | Software-based | 480p | Standard content |
| L3 | Basic | 480p | Free content |

### DRM Flow
1. **Request:** Player requests license from DRM server
2. **Challenge:** Server sends encrypted challenge
3. **Response:** Player sends device info + proof
4. **License:** Server returns decryption keys
5. **Playback:** Content decrypted in secure path

### Why TV2 Needs DRM
- Protect premium content (sports, movies)
- Content licensing requirements
- Prevent piracy
- Revenue protection

---

## 📺 LAR (Linear Ad Replacement)

### What is it?
Technology that replaces ads in **live/linear TV streams** with targeted advertisements.

### How it Works
1. **SCTE-35 Markers:** Broadcaster inserts markers in stream
2. **Ad Decision Server:** Determines which ads to show (personalized!)
3. **Stitching:** Ads are inserted server-side or client-side
4. **Tracking:** Analytics confirm ad delivery

### SSAI vs CSAI

**Server-Side Ad Insertion (SSAI)**
- ✅ Can't be blocked by ad blockers
- ✅ Seamless experience (no buffering)
- ❌ Less flexible targeting
- ❌ More expensive infrastructure

**Client-Side Ad Insertion (CSAI)**
- ✅ Better targeting
- ✅ Cheaper to implement
- ❌ Can be blocked
- ❌ May cause buffering

**TV2 likely uses SSAI for live content!**

### SCTE-35
Industry standard for signaling ad breaks in video streams. Think of it as "invisible markers" that say "ad break here!"

---

## 📊 Video Analytics

### Key Metrics

**Quality of Service (QoS)**
- **Startup Time:** How long until video plays (target: <2s)
- **Rebuffering Ratio:** % of time spent buffering (target: <1%)
- **Video Start Failures:** % of playback errors (target: <0.5%)
- **Bitrate Distribution:** What quality users get

**User Engagement**
- **Completion Rate:** % who finish videos
- **Watch Time:** Total viewing duration
- **Engagement Rate:** Interactions per session
- **Abandonment Rate:** When users drop off

**Technical Health**
- **Error Rate:** Playback failures
- **DRM Failures:** License acquisition issues
- **CDN Performance:** Delivery speed
- **Device/OS Breakdown:** Platform usage

### Why Track These?
- **Content decisions:** What shows to produce/license?
- **Technical optimization:** Where to improve infrastructure?
- **Monetization:** Are ads being delivered effectively?
- **User experience:** Are users happy?

---

## 🏗️ ExoPlayer Architecture

### Why ExoPlayer?
- Used by YouTube, Netflix, Disney+
- Google-maintained
- Highly customizable
- Excellent DRM support
- Superior to MediaPlayer API

### Key Components
```
ExoPlayer
├── MediaSource (HLS/DASH parsers)
├── Renderer (Video/Audio/Text)
├── TrackSelector (Quality switching)
├── LoadControl (Buffering strategy)
└── DrmSessionManager (DRM handling)
```

### Customization Points
- Custom UI controls
- Analytics listeners
- Error handling
- Cache strategies
- Quality selection

---

## 🎬 Android TV Specifics

### Key Differences from Mobile
- **D-pad navigation** (no touch)
- **10-foot UI** (larger text/buttons)
- **Leanback library** (TV-optimized components)
- **Picture quality** (typically 1080p/4K TVs)

### Your Implementation
- Added `LEANBACK_LAUNCHER` intent filter
- Made touchscreen optional in manifest
- Used focusable navigation
- Grid layout optimized for TV browsing

### TV2 Play on Android TV
Half a million daily users likely includes significant TV viewers!

---

## 🚀 Production Considerations

### What You'd Add for Production

**1. CDN Integration**
- Akamai, Cloudflare, or Fastly
- Edge caching for low latency
- Geographic load balancing

**2. Backend APIs**
- Content catalog service
- User authentication
- Personalization engine
- Recommendation system

**3. Advanced Analytics**
- Firebase Analytics
- Google Analytics for TV
- Custom event pipeline
- Real-time dashboards

**4. Enhanced DRM**
- Multi-DRM (Widevine + PlayReady + FairPlay)
- Offline download support
- License rotation
- Watermarking

**5. Performance**
- Preloading next episode
- Thumbnail previews
- Predictive caching
- Battery optimization

**6. Accessibility**
- Closed captions (WebVTT)
- Audio descriptions
- Screen reader support
- High contrast mode

---

## 🎤 Sample Interview Answers

**Q: "Walk me through your streaming app."**
*"I built a 3-screen demo app. The Browse screen shows a content catalog with video thumbnails and metadata badges indicating DRM protection and ad-enabled content. The Player screen uses ExoPlayer with Widevine DRM support and simulates LAR ad insertion at specific timestamps. The Analytics Dashboard tracks all playback events in real-time. The entire app is Android TV compatible with D-pad navigation."*

**Q: "How did you implement DRM?"**
*"I used Media3's DrmSessionManager with Widevine UUID. When playing protected content, ExoPlayer sends a license request to the DRM server URL. The server returns decryption keys, and ExoPlayer handles secure content decryption. I track DRM initialization events in analytics to monitor success rates."*

**Q: "What's your approach to video quality optimization?"**
*"ExoPlayer handles adaptive bitrate automatically, but I'd optimize by: setting appropriate initial quality based on network type, prefetching manifests for faster startup, implementing bandwidth estimation improvements, and using analytics to identify quality issues across devices and networks."*

**Q: "How would you debug a buffering issue?"**
*"First, check analytics for patterns - specific content, devices, or times? Then investigate: CDN performance logs, network conditions, device capabilities, encoding bitrates. Use ExoPlayer's debug logging to see buffer states, quality switches, and network requests. Could be server-side (CDN), client-side (device), or content issues (encoding)."*

**Q: "Why should we hire you?"**
*"I demonstrated I can quickly learn complex domains - I had zero streaming experience but built a production-quality demo. I understand the technical stack TV2 uses, I'm enthusiastic about video technology, and I showed initiative by building this before the interview. Plus, I can explain these concepts clearly, which helps in team collaboration."*

---

## 🔍 TV2 Play Research

### Facts to Know
- **Denmark's largest commercial TV network**
- **500,000+ daily users** on TV2 Play
- **Live TV + On-demand** content
- **Sports, news, entertainment** programming
- **Multiple platforms:** Web, iOS, Android, TV

### Competitors
- **DR (Public broadcaster)**
- **Viaplay (Nordic streaming)**
- **Netflix, Disney+, HBO Max** (international)

### Technical Challenges
- **Live sports streaming** (low latency critical)
- **Peak traffic events** (popular shows, live sports)
- **Content rights management** (geographic restrictions)
- **Multi-device sync** (continue watching)

---

## ✅ Final Checklist Before Interview

- [ ] Run the app and test all 3 screens
- [ ] Review key files: PlayerScreen.kt, LinearAdReplacementManager.kt, VideoAnalytics.kt
- [ ] Prepare to explain DRM, LAR, and analytics
- [ ] Know your sample videos and their features
- [ ] Have questions ready about TV2's tech stack
- [ ] Practice 30-second app demo
- [ ] Review this cheat sheet

---

## 🎯 Success Factors

**Technical Knowledge:** ✅ You understand the tech  
**Practical Experience:** ✅ You built a working app  
**Learning Ability:** ✅ You learned it all quickly  
**Enthusiasm:** ✅ You went above and beyond  
**Communication:** ✅ You can explain it clearly  

**You're ready! Good luck! 🚀**
