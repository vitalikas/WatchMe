# 🔴 Live Streaming URLs - Real CDNs

Public live streaming endpoints for testing **true live content** (not looping videos).

---

## ✅ Recommended Live Streams (24/7)

### 1. **NASA TV** (HLS) ⭐ BEST
```kotlin
videoUrl = "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8"
```
- **Type:** 24/7 live space content
- **Quality:** HD
- **Reliability:** Excellent (NASA CDN)
- **Why:** Always available, real live content

### 2. **Red Bull TV** (HLS)
```kotlin
videoUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8"
```
- **Type:** Sports and extreme content
- **Quality:** HD
- **Reliability:** Good
- **Why:** High-quality live events

### 3. **DW News** (HLS) ⭐ BEST
```kotlin
videoUrl = "https://dwamdstream102.akamaized.net/hls/live/2015525/dwstream102/index.m3u8"
```
- **Type:** 24/7 live news
- **Quality:** HD
- **Reliability:** Excellent
- **Why:** Continuous news broadcast

### 4. **BBC News** (HLS)
```kotlin
videoUrl = "https://vs-cmaf-pushb-uk.live.cf.md.bbci.co.uk/x=4/i=urn:bbc:pips:service:bbc_news_channel_hd/iptv_hd_abr_v1.mpd"
```
- **Type:** Live news channel
- **Quality:** HD
- **Reliability:** Good (geo-restricted in some regions)
- **Format:** DASH

### 5. **Al Jazeera** (HLS) ⭐ BEST
```kotlin
videoUrl = "https://live-hls-web-aje.getaj.net/AJE/index.m3u8"
```
- **Type:** 24/7 live news
- **Quality:** HD
- **Reliability:** Excellent
- **Why:** Always broadcasting, global access

---

## 🧪 Test Streams (For Development)

### Apple Test Streams
```kotlin
// Basic HLS test stream
videoUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"

// Advanced features
videoUrl = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_adv_example_hevc/master.m3u8"
```

### Akamai Test Streams
```kotlin
// Demo stream
videoUrl = "https://moctobpltc-i.akamaihd.net/hls/live/571329/eight/playlist.m3u8"
```

### Unified Streaming Demo
```kotlin
videoUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
```

---

## 🌍 Regional Live Channels

### USA
```kotlin
// ABC News Live
videoUrl = "https://content.uplynk.com/channel/3324f2467c414329b3b0cc5cd987b6be.m3u8"

// CBS News
videoUrl = "https://cbsn-us.cbsnstream.cbsnews.com/out/v1/55a8648e8f134e82a470f83d562deeca/master.m3u8"
```

### Europe
```kotlin
// France 24 (English)
videoUrl = "https://static.france24.com/live/F24_EN_HI_HLS/live_web.m3u8"

// Euronews
videoUrl = "https://rakuten-euronews-1-gb.samsung.wurl.tv/playlist.m3u8"
```

### Middle East
```kotlin
// Al Jazeera English
videoUrl = "https://live-hls-web-aje.getaj.net/AJE/index.m3u8"

// Al Arabiya
videoUrl = "https://live.alarabiya.net/alarabiapublish/alarabiya.smil/playlist.m3u8"
```

---

## 🎥 Sports & Entertainment

### Red Bull TV
```kotlin
videoUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8"
```

### Olympic Channel
```kotlin
videoUrl = "https://ott-channels.akamaized.net/out/v1/3e30e05b1c5148b9bcef73fb0e84a16e/index.m3u8"
```

---

## 🔧 How to Use in Your App

### Update VideoContent.kt:

```kotlin
VideoContent(
    id = "5",
    title = "NASA TV - Live 24/7",
    description = "Real live streaming from NASA. " +
            "True 24/7 broadcast demonstrating live streaming capabilities with LAR.",
    thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg",
    videoUrl = "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8",
    duration = 0, // Live stream
    category = "Live",
    hasAds = true
)
```

---

## ✅ Best Options for TV2 Play Demo

### **Recommended: NASA TV**
```kotlin
videoUrl = "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8"
```

**Why:**
- ✅ Always available (24/7)
- ✅ High quality (HD)
- ✅ Reliable CDN (Akamai)
- ✅ No geo-restrictions
- ✅ Interesting content
- ✅ Perfect for demonstrating live features

### **Alternative: Al Jazeera English**
```kotlin
videoUrl = "https://live-hls-web-aje.getaj.net/AJE/index.m3u8"
```

**Why:**
- ✅ True news channel (like TV2)
- ✅ 24/7 availability
- ✅ Global access
- ✅ Professional broadcast quality

---

## 🎯 For Your Interview

### When Demonstrating Live Streaming:

**Point out:**
1. **Duration = 0** (indicates live stream)
2. **No seek bar** (can't scrub in live content)
3. **Buffer management** (live requires different strategy)
4. **LAR integration** (how ads would work in live)

**Say this:**
*"This is a true live stream from [NASA/Al Jazeera], not a looping video. You can see there's no duration indicator and seeking is disabled, which is appropriate for live content. In production, TV2 Play's live sports and news would work similarly, with server-side ad insertion at specific cue points signaled by SCTE-35 markers."*

---

## 🔴 Live vs VOD (Video on Demand)

| Feature | Live Stream | VOD |
|---------|------------|-----|
| **Duration** | Unknown (∞) | Fixed (634s) |
| **Seeking** | Limited/None | Full control |
| **Buffering** | More critical | Can prefetch |
| **Latency** | Important | Not relevant |
| **Caching** | Difficult | Easy |
| **LAR** | SCTE-35 markers | Pre-defined breaks |
| **Quality** | Adaptive (live) | Adaptive (file) |

---

## 🚨 Troubleshooting Live Streams

### Stream Not Playing?

**1. Check if stream is actually live:**
```bash
curl -I "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8"
# Should return 200 OK
```

**2. Test in browser:**
Open in video player: `https://hls-js.netlify.app/demo/`
Paste URL and test

**3. Check geo-restrictions:**
Some streams are region-locked (BBC, some European channels)

**4. Try alternative:**
Always have 2-3 backup live stream URLs

---

## 📝 Current Implementation

Your app currently uses:
```kotlin
videoUrl = "https://d2zihajmogu5jn.cloudfront.net/bipbop-advanced/bipbop_16x9_variant.m3u8"
```

**Recommendation:** Swap to NASA or Al Jazeera for more reliable demo.

---

## 🎯 Production Considerations

### For TV2 Play:

**Live Sports:**
- Ultra-low latency (WebRTC, LL-HLS)
- SCTE-35 ad markers
- DVR functionality (pause/rewind live)
- Multiple camera angles
- Live statistics overlay

**Live News:**
- Breaking news alerts
- Lower third graphics
- Multi-bitrate delivery
- Fallback to replay if live fails

**Live Events:**
- Countdown timers
- Pre-roll content
- Post-event replay availability
- Social media integration

---

## 🔗 Useful Resources

- **HLS Validator:** https://hls-validator.com/
- **DASH Player:** https://reference.dashif.org/dash.js/
- **ExoPlayer Demo:** https://exoplayer.dev/demo-application.html
- **Video Test Suite:** https://github.com/video-dev/streams

---

## ✅ Quick Test Commands

```bash
# Test if stream is accessible
curl -I "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8"

# Download manifest to inspect
curl "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8"

# Test with ffmpeg
ffmpeg -i "https://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8" -t 10 test.mp4
```

---

**Use NASA TV or Al Jazeera for the most reliable live streaming demo!** 🚀📡
