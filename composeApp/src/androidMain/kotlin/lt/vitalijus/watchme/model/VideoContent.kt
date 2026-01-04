package lt.vitalijus.watchme.model

import lt.vitalijus.watchme.data.dto.VideoDto

/**
 * Sample content for the demo app
 * Using public test streams from various sources
 */
object SampleContent {
    val videos = listOf(
        VideoDto(
            id = "1",
            title = "Big Buck Bunny",
            description = "A large and lovable rabbit deals with three tiny bullies. " +
                    "This demonstrates HLS streaming with LAR ad insertion.",
            thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg",
            videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            duration = 634,
            category = "Demo",
            hasAds = true
        ),
        VideoDto(
            id = "2",
            title = "Elephants Dream (DASH)",
            description = "The story of two friends exploring a strange mechanical world. " +
                    "Demonstrates DASH streaming format.",
            thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
            videoUrl = "https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd",
            duration = 653,
            category = "Demo",
            hasAds = false
        ),
        VideoDto(
            id = "3",
            title = "Sintel",
            description = "A young woman seeks revenge for her dragon friend. " +
                    "High quality streaming test content.",
            thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            duration = 888,
            category = "Demo",
            hasAds = false
        ),
        VideoDto(
            id = "4",
            title = "DRM Protected Content (Widevine)",
            description = "Demonstrates Widevine DRM protected content. " +
                    "This shows how TV2 Play protects premium content.",
            thumbnailUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg",
            videoUrl = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd",
            duration = 735,
            category = "Protected",
            hasDrm = true,
            drmLicenseUrl = "https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test",
            hasAds = false
        ),
        VideoDto(
            id = "5",
            title = "CBS News - Live 24/7",
            description = "Real CBS News live stream broadcasting 24/7. " +
                    "True live news channel demonstrating how TV2 Play handles live content with LAR. " +
                    "Actual breaking news and professional broadcast quality.",
            thumbnailUrl = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=400&h=225&fit=crop",
            videoUrl = "https://cbsn-us.cbsnstream.cbsnews.com/out/v1/55a8648e8f134e82a470f83d562deeca/master.m3u8",
            duration = 0, // Live stream
            category = "Live",
            hasAds = true
        ),
        VideoDto(
            id = "6",
            title = "ABC News - Live Channel",
            description = "Real ABC News live 24/7 broadcast. " +
                    "Continuous news coverage showing live streaming implementation. " +
                    "Similar to TV2's live news and breaking events coverage.",
            thumbnailUrl = "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=400&h=225&fit=crop",
            videoUrl = "https://d2e1asnsl7br7b.cloudfront.net/7782e205e72f43aeb4a48ec97f66ebbe/index.m3u8",
            duration = 0, // Live stream
            category = "Live",
            hasAds = false
        )
    )
}
