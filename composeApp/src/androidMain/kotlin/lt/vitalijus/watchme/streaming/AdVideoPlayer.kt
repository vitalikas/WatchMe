package lt.vitalijus.watchme.streaming

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Ad Video Player Manager
 * 
 * Handles switching between main content and ad videos for Client-Side Ad Insertion (CSAI)
 * When an SCTE-35 marker is detected, this class:
 * 1. Pauses main content
 * 2. Plays ad videos sequentially
 * 3. Returns to main content after ads complete
 */
@UnstableApi
class AdVideoPlayer(
    private val context: Context
) {
    private var mainPlayer: ExoPlayer? = null
    private var contentMediaItem: MediaItem? = null
    private var contentPlaybackPosition: Long = 0
    private var contentPlayWhenReady: Boolean = true
    
    private val _isPlayingAd = MutableStateFlow(false)
    val isPlayingAd: StateFlow<Boolean> = _isPlayingAd.asStateFlow()
    
    private val _currentAdIndex = MutableStateFlow(0)
    val currentAdIndex: StateFlow<Int> = _currentAdIndex.asStateFlow()
    
    private val _totalAds = MutableStateFlow(0)
    val totalAds: StateFlow<Int> = _totalAds.asStateFlow()

    /**
     * Set the player instance to manage
     */
    fun setPlayer(player: ExoPlayer, mediaItem: MediaItem) {
        this.mainPlayer = player
        this.contentMediaItem = mediaItem
    }

    /**
     * Play an ad break with multiple ad videos
     */
    fun playAdBreak(
        adUrls: List<String>,
        onAdBreakComplete: () -> Unit
    ) {
        val player = mainPlayer ?: run {
            println("⚠️ No player set for AdVideoPlayer")
            return
        }
        
        if (adUrls.isEmpty()) {
            println("⚠️ No ad URLs provided")
            onAdBreakComplete()
            return
        }
        
        println("🎬 Starting ad break with ${adUrls.size} ads")
        
        // Save current content state
        contentPlaybackPosition = player.currentPosition
        contentPlayWhenReady = player.playWhenReady
        
        _isPlayingAd.value = true
        _totalAds.value = adUrls.size
        _currentAdIndex.value = 0
        
        // Create playlist of ad videos
        val adMediaItems = adUrls.map { url ->
            MediaItem.fromUri(url)
        }
        
        // Create concatenating media source for sequential ad playback
        val concatenatingMediaSource = ConcatenatingMediaSource()
        adMediaItems.forEach { mediaItem ->
            val mediaSource = DefaultMediaSourceFactory(context)
                .createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        
        // Track ad progress and completion
        val adListener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // Update current ad index when transitioning between ads
                if (_isPlayingAd.value) {
                    _currentAdIndex.value = player.currentMediaItemIndex
                    println("📺 Now playing ad ${_currentAdIndex.value + 1}/${_totalAds.value}")
                }
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && _isPlayingAd.value) {
                    println("✅ All ads completed, returning to content")
                    player.removeListener(this)
                    resumeContent(onAdBreakComplete)
                }
            }
        }
        
        player.addListener(adListener)
        
        // Play ads
        player.setMediaSource(concatenatingMediaSource)
        player.prepare()
        player.seekTo(0, 0) // Start from beginning of first ad
        player.playWhenReady = true
    }

    /**
     * Resume main content after ads complete
     */
    private fun resumeContent(onComplete: () -> Unit) {
        val player = mainPlayer ?: return
        val mediaItem = contentMediaItem ?: return
        
        _isPlayingAd.value = false
        _currentAdIndex.value = 0
        _totalAds.value = 0
        
        println("🎯 Resuming content at position ${contentPlaybackPosition}ms")
        
        // Restore main content
        val mediaSource = DefaultMediaSourceFactory(context)
            .createMediaSource(mediaItem)
        
        player.setMediaSource(mediaSource)
        player.prepare()
        player.seekTo(contentPlaybackPosition)
        player.playWhenReady = contentPlayWhenReady
        
        onComplete()
    }

    /**
     * Force end ad break (e.g., if user seeks during ads)
     */
    fun endAdBreak() {
        if (_isPlayingAd.value) {
            println("⏹️ Force ending ad break")
            resumeContent {}
        }
    }

    /**
     * Get sample ad URLs for testing
     */
    companion object {
        fun getSampleAdUrls(): List<String> = listOf(
            // Sample ad videos (Google's test videos)
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
        )
        
        fun getLongerAdUrls(): List<String> = listOf(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
        )
    }

    fun release() {
        mainPlayer = null
        contentMediaItem = null
        _isPlayingAd.value = false
    }
}
