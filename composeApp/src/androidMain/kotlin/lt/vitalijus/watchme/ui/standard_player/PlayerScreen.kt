package lt.vitalijus.watchme.ui.standard_player

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import lt.vitalijus.watchme.analytics.AnalyticsEvent
import lt.vitalijus.watchme.data.repository.KtorVideoRemoteDataSource
import lt.vitalijus.watchme.analytics.VideoAnalyticsTracker
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.streaming.LinearAdReplacementManager
import lt.vitalijus.watchme.ui.standard_player.components.AdIndicatorOverlay
import lt.vitalijus.watchme.ui.standard_player.components.TechnicalInfoCard
import org.koin.androidx.compose.koinViewModel

/**
 * Video Player Screen - Wrapper that handles video loading
 */
@Composable
fun PlayerScreen(
    videoId: String,
    onBack: () -> Unit
) {
    var video by remember { mutableStateOf<Video?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load video asynchronously
    LaunchedEffect(videoId) {
        isLoading = true
        video = KtorVideoRemoteDataSource().fetchVideoById(videoId)
        isLoading = false
    }
    
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        video != null -> {
            PlayerScreenContent(
                video = video!!,
                onBack = onBack
            )
        }
    }
}

/**
 * Video Player Screen - Second Screen
 * Implements ExoPlayer with DRM support and LAR integration
 * Demonstrates key streaming technologies required for TV2 Play
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerScreenContent(
    video: Video,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PlayerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // LAR state (still from manager, as it's shared across app)
    val isAdPlaying by LinearAdReplacementManager.isAdPlaying.collectAsState()
    val currentAdPod by LinearAdReplacementManager.currentAdPod.collectAsState()

    // Create ExoPlayer - cached across recompositions, recreated only when video.id changes
    val exoPlayer = remember(video.id) {
        createExoPlayer(
            context = context,
            video = video,
            initialPosition = 0L,
            playWhenReady = true,
            onError = { errorMessage ->
                viewModel.handleIntent(
                    intent = PlayerIntent.HandleError(
                        errorMessage = errorMessage
                    )
                )
                VideoAnalyticsTracker.trackEvent(
                    event = AnalyticsEvent.ErrorOccurred(
                        videoId = video.id,
                        errorMessage = errorMessage,
                        timestamp = System.currentTimeMillis()
                    )
                )
            },
            onBufferingStateChange = { isBuffering ->
                viewModel.handleIntent(
                    intent = PlayerIntent.UpdateBuffering(
                        isBuffering = isBuffering
                    )
                )
            },
            onDurationReady = { playerInstance, duration ->
                viewModel.handleIntent(
                    intent = PlayerIntent.UpdatePlaybackState(
                        isPlaying = playerInstance.isPlaying,
                        currentPosition = playerInstance.currentPosition,
                        duration = duration
                    )
                )
            }
        )
    }

    // Manage player lifecycle with DisposableEffect
    DisposableEffect(video.id) {
        // Track analytics - video started
        VideoAnalyticsTracker.trackEvent(
            event = AnalyticsEvent.VideoStarted(
                videoId = video.id,
                videoTitle = video.title,
                timestamp = System.currentTimeMillis()
            )
        )

        // Track DRM initialization if present
        if (video.hasDrm && video.drmLicenseUrl != null) {
            VideoAnalyticsTracker.trackEvent(
                event = AnalyticsEvent.DrmInitialized(
                    videoId = video.id,
                    drmScheme = "Widevine",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Monitor for LAR ad breaks (only if ads are enabled)
        // Also updates position in state for ad indicator overlay
        val monitoringJob = if (video.hasAds) {
            scope.launch {
                while (isActive) {
                    val currentPosition = exoPlayer.currentPosition

                    // Update position and playing state for ad indicator overlay
                    // Note: duration is updated once via onDurationReady callback
                    viewModel.handleIntent(
                        intent = PlayerIntent.UpdatePlaybackState(
                            isPlaying = exoPlayer.isPlaying,
                            currentPosition = currentPosition,
                            duration = state.duration  // Use cached duration from state
                        )
                    )

                    // Check if we should start a new ad pod
                    val adPod = LinearAdReplacementManager.checkForAdBreak(
                        currentPosition = currentPosition,
                        videoId = video.id,
                        hasAds = video.hasAds
                    )

                    // Start ad pod if found and not already playing
                    if (adPod != null && currentAdPod?.id != adPod.id) {
                        LinearAdReplacementManager.startAdPod(adPod = adPod)
                        VideoAnalyticsTracker.trackEvent(
                            event = AnalyticsEvent.AdShown(
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

                    delay(500) // Check every 500ms - sufficient for ad break detection
                }
            }
        } else {
            null
        }

        onDispose {
            // Cancel monitoring job if exists
            monitoringJob?.cancel()

            // Track analytics
            VideoAnalyticsTracker.trackEvent(
                event = AnalyticsEvent.VideoPaused(
                    videoId = video.id,
                    position = exoPlayer.currentPosition,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Clean up resources
            LinearAdReplacementManager.reset()
            exoPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = video.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                .verticalScroll(rememberScrollState())
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
                        currentPosition = state.currentPosition
                    )
                }

                // Buffering indicator
                if (state.isBuffering) {
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
                state.error?.let { errorMsg ->
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
                    text = video.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(height = 8.dp))
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(height = 16.dp))
                // Technical info
                TechnicalInfoCard(
                    video = video,
                    quality = state.currentQuality,
                    duration = state.duration
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
private fun createExoPlayer(
    context: Context,
    video: Video,
    initialPosition: Long,
    playWhenReady: Boolean,
    onError: (String) -> Unit,
    onBufferingStateChange: (Boolean) -> Unit,
    onDurationReady: (ExoPlayer, Long) -> Unit = { _, _ -> }
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
                Player.STATE_IDLE -> {

                }

                Player.STATE_BUFFERING -> {
                    onBufferingStateChange(true)
                    VideoAnalyticsTracker.trackEvent(
                        event = AnalyticsEvent.BufferingStarted(
                            videoId = video.id,
                            position = player.currentPosition,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                Player.STATE_READY -> {
                    onBufferingStateChange(false)
                    VideoAnalyticsTracker.trackEvent(
                        event = AnalyticsEvent.BufferingEnded(
                            videoId = video.id,
                            bufferingDuration = 500, // Simplified
                            timestamp = System.currentTimeMillis()
                        )
                    )

                    // Notify duration is ready
                    val duration = player.duration.takeIf { it != C.TIME_UNSET } ?: 0L
                    if (duration > 0L) {
                        onDurationReady(player, duration)
                    }
                }

                Player.STATE_ENDED -> {
                    VideoAnalyticsTracker.trackEvent(
                        event = AnalyticsEvent.VideoCompleted(
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
                    event = AnalyticsEvent.VideoPlayed(
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
