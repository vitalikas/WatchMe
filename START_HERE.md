# 🎬 START HERE - WatchMe Interview App

**Welcome to your TV2 Play interview preparation app!**

This document will guide you through everything you need to know.

---

## 📚 Document Guide (Read in This Order)

### 1. **START_HERE.md** ← You are here! 👋
Overview and getting started guide

### 2. **QUICK_START.md** (5 min read)
Quick overview of features and demo flow

### 3. **INTERVIEW_CHEATSHEET.md** (10 min read)
Key concepts, terminology, and sample Q&A

### 4. **INTERVIEW_GUIDE.md** (20 min read)
Deep technical explanations and interview strategies

### 5. **APP_SUMMARY.md** (5 min read)
Complete feature list and what was built

### 6. **ARCHITECTURE.md** (10 min read)
System design and component diagrams

### 7. **COMMANDS.md** (reference)
Build, run, and debug commands

### 8. **TROUBLESHOOTING.md** (when needed)
Solutions for common issues and demo backup plans

### 9. **README_INTERVIEW.md** (quick reference)
Main project README with all key info

---

## 🚀 First Steps (Do This Now)

### Step 1: Build the App
```bash
./gradlew installDebug
```

### Step 2: Test It
1. Open the app on your device
2. Browse through the video catalog
3. Play "Big Buck Bunny" (has LAR ads)
4. Play "DRM Protected Content" (Widevine)
5. Check the Analytics Dashboard

### Step 3: Read Documentation
1. Start with **QUICK_START.md**
2. Then **INTERVIEW_CHEATSHEET.md**
3. Skim **INTERVIEW_GUIDE.md** for details

### Step 4: Prepare Your Demo
Practice explaining:
- What you built
- Why you built it
- How it demonstrates TV2 Play requirements

---

## ✅ What This App Demonstrates

### Required Skills ✓
- [x] Video streaming technologies (HLS, DASH)
- [x] LAR (Linear Ad Replacement)
- [x] DRM implementation (Widevine)
- [x] Video analytics
- [x] Android TV support

### Additional Skills ✓
- [x] Modern Android development (Jetpack Compose)
- [x] Clean architecture
- [x] Production-quality code
- [x] Comprehensive documentation

---

## 🎯 3-Screen App Overview

### Screen 1: Browse/Catalog
📱 **What it shows:**
- Video content grid
- DRM and LAR badges
- Android TV compatible navigation

🎤 **What to say:**
"This is the content discovery screen. Notice the badges indicating which videos have DRM protection and LAR ad insertion. The grid layout is optimized for both mobile and Android TV using D-pad navigation."

### Screen 2: Video Player
📱 **What it shows:**
- ExoPlayer with DRM support
- LAR ad insertion overlays
- Real-time analytics tracking
- Technical details display

🎤 **What to say:**
"This uses Media3's ExoPlayer with Widevine DRM support. When you play the DRM-protected video, it acquires a license from the DRM server. For videos with LAR enabled, you'll see ad overlays at specific timestamps simulating SCTE-35 marker insertion. All playback events are tracked for analytics."

### Screen 3: Analytics Dashboard
📱 **What it shows:**
- Real-time metrics
- Event log
- QoS monitoring
- Summary statistics

🎤 **What to say:**
"The analytics dashboard shows all tracked events: video starts, buffering, ad impressions, DRM initializations, and errors. This demonstrates understanding of what metrics matter for streaming services like TV2 Play."

---

## 🔑 Key Technologies Explained Simply

### HLS & DASH (Streaming Formats)
**What:** Ways to deliver video over HTTP
**Why TV2 uses it:** Works everywhere, adapts to network speed
**Your demo:** Different videos use different formats

### Widevine DRM (Content Protection)
**What:** Google's system to prevent piracy
**Why TV2 uses it:** Protect premium content like sports
**Your demo:** "DRM Protected Content" video

### LAR (Linear Ad Replacement)
**What:** Dynamically insert ads into live/linear streams
**Why TV2 uses it:** Monetize content with targeted ads
**Your demo:** "Big Buck Bunny" shows ad overlays

### Video Analytics
**What:** Track playback events and metrics
**Why TV2 uses it:** Understand user behavior, optimize quality
**Your demo:** Analytics Dashboard shows all events

### Android TV
**What:** TV-optimized Android experience
**Why TV2 uses it:** Many users watch on TV
**Your demo:** Works with D-pad navigation, Leanback launcher

---

## 🎤 Your Elevator Pitch (Memorize This)

*"I built this 3-screen streaming app to demonstrate the technologies TV2 Play uses. It includes HLS and DASH streaming with ExoPlayer, Widevine DRM for content protection, Linear Ad Replacement simulating SCTE-35 markers, comprehensive video analytics tracking, and full Android TV support. I had no prior streaming experience, so I researched industry standards and implemented production-quality patterns. This shows I can quickly learn complex domains and deliver professional code."*

**Time:** 30 seconds  
**Practice:** Say it out loud 5 times

---

## 🎬 Demo Flow (Practice This)

### Introduction (30s)
"Thanks for the opportunity! I built this demo app to show I understand TV2 Play's tech stack. Let me walk you through it."

### Browse Screen (30s)
"This is the content catalog with 6 different video types. The badges show which have DRM protection and ad insertion capabilities."

### Player - DRM (1-2 min)
"Let's play this DRM-protected video. When it starts, ExoPlayer requests a Widevine license from the server. [Show technical details card] You can see it's using DASH format with Widevine DRM."

### Player - LAR (1-2 min)
"Now let's play Big Buck Bunny which has LAR enabled. If I skip to 2 minutes... [ad overlay appears] ...you see the ad indicator showing LAR ad insertion at this timestamp. This simulates SCTE-35 markers in live streams."

### Analytics (1 min)
"Finally, the analytics dashboard tracks everything: video starts, buffering events, ad impressions, DRM initializations. This data would normally go to a backend service for monitoring and optimization."

### Wrap-up (30s)
"That's the demo! I'm happy to dive deeper into any part of the implementation or discuss how this relates to TV2 Play's needs."

**Total time:** 5-6 minutes

---

## 💡 Common Interview Questions

### "Why did you build this?"
"To demonstrate I can quickly learn complex domains. I had zero streaming experience but wanted to show I understand what TV2 Play needs and can implement it."

### "What was most challenging?"
"Understanding DRM licensing flow and LAR ad insertion timing. I researched industry standards like SCTE-35 and implemented a simulation that matches production patterns."

### "What would you add for production?"
"Backend APIs for content, real SCTE-35 detection, analytics backend integration, offline playback with persistent DRM licenses, CDN optimization, and enhanced error handling."

### "How does this relate to TV2 Play?"
"TV2 Play has 500k+ daily users streaming live TV and on-demand content. They need DRM for premium content like sports, LAR for monetization, and analytics to optimize quality and engagement. This demo shows I understand those requirements."

### "What did you learn?"
"Streaming protocols work differently than I expected - chunked delivery, adaptive bitrate, manifest parsing. Also learned DRM isn't just encryption but a complete licensing system. And LAR's SCTE-35 markers are crucial for live TV monetization."

---

## 🎯 Interview Success Factors

### Before Interview
- [x] Build and test app
- [ ] Read QUICK_START.md
- [ ] Read INTERVIEW_CHEATSHEET.md
- [ ] Practice demo flow (5-6 min)
- [ ] Practice elevator pitch (30 sec)
- [ ] Prepare 3-5 questions for interviewer
- [ ] Charge device, test on device

### During Interview
- [ ] Stay calm and confident
- [ ] Demo app smoothly
- [ ] Explain technical choices
- [ ] Show enthusiasm for streaming tech
- [ ] Ask thoughtful questions
- [ ] Take notes on feedback

### After Demo
- [ ] Answer technical questions
- [ ] Reference specific code files
- [ ] Discuss improvements
- [ ] Show learning ability
- [ ] Express interest in role

---

## 📱 Technical Deep Dives (If Asked)

### "Explain your DRM implementation"
👉 **Reference:** `ui/PlayerScreen.kt` lines 350-400
- Used Media3's DefaultDrmSessionManager
- Configured with Widevine UUID
- HttpMediaDrmCallback for license requests
- Integrated with ExoPlayer's MediaSource

### "How does LAR work?"
👉 **Reference:** `streaming/LinearAdReplacementManager.kt`
- Monitors playback position every 100ms
- Checks against predefined ad cue points
- Simulates SCTE-35 marker detection
- Shows overlay when ad pod is active
- Tracks impression in analytics

### "Walk me through your analytics architecture"
👉 **Reference:** `analytics/VideoAnalytics.kt`
- Singleton tracker for global access
- Sealed class for type-safe events
- StateFlow for reactive UI updates
- Summary statistics calculated on-the-fly
- Production would send to backend

### "How did you handle Android TV?"
👉 **Reference:** `AndroidManifest.xml`, `ui/BrowseScreen.kt`
- Added LEANBACK_LAUNCHER intent filter
- Made touchscreen optional
- Used focusable components
- Grid layout works with D-pad
- Tested with TV emulator

---

## 🚀 Ready to Go!

### You have:
✅ Working demo app  
✅ Comprehensive documentation  
✅ Technical explanations ready  
✅ Understanding of streaming concepts  
✅ Production-quality code  

### You're ready to:
✅ Demo the app confidently  
✅ Explain technical choices  
✅ Discuss improvements  
✅ Show enthusiasm  
✅ Answer tough questions  

---

## 🎉 Final Checklist

**30 Minutes Before Interview:**
- [ ] Build and install: `./gradlew installDebug`
- [ ] Test all 3 screens work
- [ ] Have device charged and ready
- [ ] Review elevator pitch
- [ ] Breathe and relax!

**During Interview:**
- [ ] Start with elevator pitch
- [ ] Demo systematically (Browse → Player → Analytics)
- [ ] Be ready to show code
- [ ] Stay positive and enthusiastic
- [ ] Ask questions about TV2's tech

**Remember:**
- You built something impressive from scratch
- You learned complex topics quickly
- You wrote production-quality code
- You're well-prepared
- You've got this! 💪

---

## 📞 Quick Reference

**Main docs:**
- QUICK_START.md - Quick overview
- INTERVIEW_CHEATSHEET.md - Key concepts
- INTERVIEW_GUIDE.md - Deep dive

**Code locations:**
- DRM: `ui/PlayerScreen.kt`
- LAR: `streaming/LinearAdReplacementManager.kt`
- Analytics: `analytics/VideoAnalytics.kt`
- Models: `model/VideoContent.kt`

**Build commands:**
```bash
./gradlew installDebug
adb shell am start -n lt.vitalijus.watchme/.MainActivity
```

---

## 🌟 You're Ready!

This is more preparation than most candidates do. You've shown:
- **Initiative** - Built this proactively
- **Learning ability** - Learned streaming from scratch
- **Technical skills** - Production-quality implementation
- **Communication** - Clear documentation

**Go ace that interview! 🚀📺**

---

*Good luck from your coding assistant! You've got this!* 💙
