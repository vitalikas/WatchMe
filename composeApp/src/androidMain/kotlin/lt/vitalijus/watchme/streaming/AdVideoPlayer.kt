package lt.vitalijus.watchme.streaming

import android.content.Context
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
    var currentAdUrls: List<String> = emptyList()
        private set

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

    fun playAdBreak(
        adUrls: List<String>,
        startAtAdIndex: Int = 0,
        startAtAdPosition: Long = 0L,
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

        // Only save content position if we are starting a fresh ad break
        if (!_isPlayingAd.value) {
            println("🎬 Starting fresh ad break with ${adUrls.size} ads")
            contentPlaybackPosition = player.currentPosition
            contentPlayWhenReady = player.playWhenReady
        } else {
            println("🎬 Resuming ad break at ad $startAtAdIndex, position ${startAtAdPosition}ms")
        }


        this.currentAdUrls = adUrls

        // Update ad state in correct order (isPlayingAd last to trigger flow emission)
        _totalAds.value = adUrls.size
        _currentAdIndex.value = startAtAdIndex
        _isPlayingAd.value = true

        println("🎬 Ad state set: isPlayingAd=${_isPlayingAd.value}, total=${_totalAds.value}, index=${_currentAdIndex.value}")

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
        var seekingToNextAd = false
        val adListener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (_isPlayingAd.value) {
                    _currentAdIndex.value = player.currentMediaItemIndex
                    println("📺 Now playing ad ${_currentAdIndex.value + 1}/${_totalAds.value}")
                    seekingToNextAd = false
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && _isPlayingAd.value) {
                    println("✅ All ads completed, returning to content")
                    player.removeListener(this)
                    resumeContent(onComplete = onAdBreakComplete)
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                // This logic is to prevent users from manually scrubbing/skipping ads
                if (_isPlayingAd.value && reason == Player.DISCONTINUITY_REASON_SEEK && !seekingToNextAd) {
                    println("🚫 Seek attempt blocked during ad playback")
                    seekingToNextAd = true
                    // We seek to the same position to effectively cancel the user's seek
                    player.seekTo(newPosition.mediaItemIndex, oldPosition.positionMs)
                }
            }
        }

        // Play ads
        with(player) {
            removeListener(adListener)
            addListener(adListener)
            setMediaSource(concatenatingMediaSource)
            prepare()
            seekTo(startAtAdIndex, startAtAdPosition)
            playWhenReady = true
        }
    }

    /**
     * Resume main content after ads complete
     */
    private fun resumeContent(onComplete: () -> Unit) {
        val player = mainPlayer ?: return
        val mediaItem = contentMediaItem ?: return

        // Clear ad state
        _isPlayingAd.value = false
        _currentAdIndex.value = 0
        _totalAds.value = 0
        this.currentAdUrls = emptyList()

        println("🎯 Resuming content at position ${contentPlaybackPosition}ms")

        // Restore main content
        val mediaSource = DefaultMediaSourceFactory(context)
            .createMediaSource(mediaItem)

        with(player) {
            setMediaSource(mediaSource)
            prepare()
            seekTo(contentPlaybackPosition)
            playWhenReady = contentPlayWhenReady
        }

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

    fun release() {
        mainPlayer = null
        contentMediaItem = null
        _isPlayingAd.value = false
        currentAdUrls = emptyList()
    }

    /**
     * Get sample ad URLs for testing
     */
    companion object {
        fun getSampleAdUrls(): List<String> = listOf(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
        )
    }
}
