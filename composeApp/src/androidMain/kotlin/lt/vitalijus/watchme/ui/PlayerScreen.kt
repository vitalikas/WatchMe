package lt.vitalijus.watchme.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import lt.vitalijus.watchme.analytics.AnalyticsEvent
import lt.vitalijus.watchme.analytics.VideoAnalyticsTracker
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.streaming.LinearAdReplacementManager
import lt.vitalijus.watchme.ui.util.formatDuration

/**
 * Video Player Screen - Second Screen
 * Implements ExoPlayer with DRM support and LAR integration
 * Demonstrates key streaming technologies required for TV2 Play
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    video: Video,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var bufferingPercentage by remember { mutableStateOf(0) }
    var currentQuality by remember { mutableStateOf("Auto") }
    var error by remember { mutableStateOf<String?>(null) }

    // Save playback position across configuration changes (fold/unfold, rotation, etc.)
    var savedPosition by rememberSaveable { mutableStateOf(0L) }
    var wasPlaying by rememberSaveable { mutableStateOf(true) }

    // LAR state
    val isAdPlaying by LinearAdReplacementManager.isAdPlaying.collectAsState()
    val currentAdPod by LinearAdReplacementManager.currentAdPod.collectAsState()

    // ExoPlayer setup
    val exoPlayer = remember(video.id) {
        createExoPlayer(
            context = context,
            video = video,
            initialPosition = savedPosition,
            playWhenReady = wasPlaying,
            onError = { errorMessage ->
                error = errorMessage
                VideoAnalyticsTracker.trackEvent(
                    AnalyticsEvent.ErrorOccurred(
                        videoId = video.id,
                        errorMessage = errorMessage,
                        timestamp = System.currentTimeMillis()
                    )
                )
            },
            onBufferingUpdate = { percentage ->
                bufferingPercentage = percentage
            }
        )
    }

    // Track analytics
    LaunchedEffect(video.id) {
        VideoAnalyticsTracker.trackEvent(
            AnalyticsEvent.VideoStarted(
                videoId = video.id,
                videoTitle = video.title,
                timestamp = System.currentTimeMillis()
            )
        )

        // Initialize DRM if present
        if (video.hasDrm && video.drmLicenseUrl != null) {
            VideoAnalyticsTracker.trackEvent(
                AnalyticsEvent.DrmInitialized(
                    videoId = video.id,
                    drmScheme = "Widevine",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Update playback state
    LaunchedEffect(exoPlayer) {
        while (true) {
            isPlaying = exoPlayer.isPlaying
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration.takeIf { it != C.TIME_UNSET } ?: 0L

            // Check for ad breaks (LAR simulation)
            if (video.hasAds) {
                // Check if we should start a new ad pod
                val adPod = LinearAdReplacementManager.checkForAdBreak(
                    currentPosition = currentPosition,
                    videoId = video.id,
                    hasAds = video.hasAds
                )

                // Start ad pod if found and not already playing
                if (adPod != null && currentAdPod?.id != adPod.id) {
                    LinearAdReplacementManager.startAdPod(adPod)
                    VideoAnalyticsTracker.trackEvent(
                        AnalyticsEvent.AdShown(
                            videoId = video.id,
                            adId = adPod.id,
                            adPosition = currentPosition,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                // End ad pod if:
                // 1. Position moved past the ad duration
                // 2. User seeked/scrubbed outside the ad range
                if (currentAdPod != null) {
                    val adStart = currentAdPod!!.startPosition
                    val adEnd = adStart + currentAdPod!!.duration

                    if (currentPosition < adStart || currentPosition > adEnd) {
                        LinearAdReplacementManager.endAdPod()
                    }
                }
            }

            delay(250) // Reduced frequency to prevent frame drops
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Save current state for configuration changes (fold/unfold, rotation)
            savedPosition = exoPlayer.currentPosition
            wasPlaying = exoPlayer.isPlaying

            val finalPosition = exoPlayer.currentPosition
            VideoAnalyticsTracker.trackEvent(
                AnalyticsEvent.VideoPaused(
                    videoId = video.id,
                    position = finalPosition,
                    timestamp = System.currentTimeMillis()
                )
            )
            LinearAdReplacementManager.reset()
            exoPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(video.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Video player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Ad indicator overlay
                if (isAdPlaying && currentAdPod != null) {
                    AdIndicatorOverlay(
                        adPod = currentAdPod!!,
                        currentPosition = currentPosition
                    )
                }

                // Buffering indicator
                if (bufferingPercentage > 0 && bufferingPercentage < 100) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Video information
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Error message
                error?.let { errorMsg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            "⚠️ Error: $errorMsg",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Video details
                Text(
                    video.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    video.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Technical info
                TechnicalInfoCard(video, currentQuality, duration)
            }
        }
    }
}

@Composable
fun AdIndicatorOverlay(adPod: lt.vitalijus.watchme.streaming.AdPod, currentPosition: Long) {
    val positionInPod = currentPosition - adPod.startPosition
    val currentAd = LinearAdReplacementManager.getCurrentAd(positionInPod)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.align(Alignment.TopEnd),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF57C00).copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    "📺 AD",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                currentAd?.let { ad ->
                    Text(
                        ad.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TechnicalInfoCard(video: Video, quality: String, duration: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "📊 Technical Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoRow("Format", if (video.videoUrl.contains(".m3u8")) "HLS" else "DASH")
            InfoRow("Quality", quality)
            InfoRow("Duration", formatDuration(duration / 1000))
            InfoRow(
                "DRM", if (video.hasDrm)
                    "✓ Widevine" else "None"
            )
            InfoRow("LAR Enabled", if (video.hasAds) "✓ Yes" else "No")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
private fun createExoPlayer(
    context: android.content.Context,
    video: Video,
    initialPosition: Long = 0L,
    playWhenReady: Boolean = true,
    onError: (String) -> Unit,
    onBufferingUpdate: (Int) -> Unit
): ExoPlayer {
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()

    // Configure DRM if present
    val mediaSourceFactory = if (video.hasDrm && video.drmLicenseUrl != null) {
        val drmCallback = HttpMediaDrmCallback(
            video.drmLicenseUrl,
            httpDataSourceFactory
        )

        val drmSessionManager = DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(
                C.WIDEVINE_UUID,
                FrameworkMediaDrm.DEFAULT_PROVIDER
            )
            .build(drmCallback)

        DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)
            .setDrmSessionManagerProvider { drmSessionManager }
    } else {
        DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)
    }

    val player = ExoPlayer.Builder(context)
        .setMediaSourceFactory(mediaSourceFactory)
        .build()

    // Add player listener
    player.addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    VideoAnalyticsTracker.trackEvent(
                        AnalyticsEvent.BufferingStarted(
                            videoId = video.id,
                            position = player.currentPosition,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                Player.STATE_READY -> {
                    VideoAnalyticsTracker.trackEvent(
                        AnalyticsEvent.BufferingEnded(
                            videoId = video.id,
                            bufferingDuration = 500, // Simplified
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                Player.STATE_ENDED -> {
                    VideoAnalyticsTracker.trackEvent(
                        AnalyticsEvent.VideoCompleted(
                            videoId = video.id,
                            totalDuration = player.duration,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            onError(error.message ?: "Unknown playback error")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                VideoAnalyticsTracker.trackEvent(
                    AnalyticsEvent.VideoPlayed(
                        videoId = video.id,
                        position = player.currentPosition,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } else {
                VideoAnalyticsTracker.trackEvent(
                    AnalyticsEvent.VideoPaused(
                        videoId = video.id,
                        position = player.currentPosition,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    })

    // Prepare media
    val mediaItem = MediaItem.fromUri(video.videoUrl)
    player.setMediaItem(mediaItem)
    player.prepare()

    // Restore playback position (for configuration changes like fold/unfold)
    if (initialPosition > 0) {
        player.seekTo(initialPosition)
    }

    player.playWhenReady = playWhenReady

    return player
}
