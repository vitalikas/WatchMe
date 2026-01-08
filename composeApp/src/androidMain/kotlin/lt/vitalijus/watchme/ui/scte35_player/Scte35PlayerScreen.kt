package lt.vitalijus.watchme.ui.scte35_player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.streaming.AdVideoPlayer
import lt.vitalijus.watchme.streaming.Scte35Handler
import lt.vitalijus.watchme.streaming.SimulatedAdBreakScheduler
import org.koin.androidx.compose.koinViewModel

/**
 * SCTE-35 Player Screen - Wrapper that handles video loading
 */
@UnstableApi
@Composable
fun Scte35PlayerScreen(
    videoId: String,
    onBack: () -> Unit
) {
    val viewModel: Scte35PlayerViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Load video on init
    LaunchedEffect(videoId) {
        viewModel.handleIntent(intent = Scte35Intent.LoadVideo(videoId = videoId))
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.video != null -> {
            Scte35PlayerScreenContent(
                video = state.video!!,
                state = state,
                onIntent = viewModel::handleIntent,
                onBack = onBack
            )
        }

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error ?: "Unknown error",
                    color = Color.Red
                )
            }
        }
    }
}

/**
 * SCTE-35 Player Screen Content
 *
 * Demonstrates real SCTE-35 marker detection and ad insertion
 * - Parses actual SCTE-35 markers from HLS streams
 * - Plays real ad videos (not overlays)
 * - Returns to content after ads complete
 */
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
private fun Scte35PlayerScreenContent(
    video: Video,
    state: Scte35State,
    onIntent: (Scte35Intent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Create Ad Video Player manager
    val adVideoPlayer = remember { AdVideoPlayer(context = context) }

    // Track player's play/pause state for the button
    var isPlaying by remember { mutableStateOf(true) }

    // Track controls visibility (shown when tapping player area)
    var showAdControls by remember { mutableStateOf(false) }

    // Track ad progress (0f to 1f)
    var adProgress by remember { mutableFloatStateOf(0f) }

    // Observe ad playback state and sync with ViewModel
    LaunchedEffect(adVideoPlayer) { // Key to the player object itself
        // Combine all relevant StateFlows from AdVideoPlayer.
        // This ensures that whenever isPlayingAd, currentAdIndex, OR totalAds changes,
        // a new intent is sent with the latest state of all three.
        combine(
            adVideoPlayer.isPlayingAd,
            adVideoPlayer.currentAdIndex,
            adVideoPlayer.totalAds
        ) { isPlaying, adIndex, total ->
            // This transform block creates a Triple of the latest values
            Triple(isPlaying, adIndex, total)
        }.collect { (isPlaying, adIndex, total) ->
            // The collect block receives the Triple and sends the intent
            onIntent(
                Scte35Intent.AdPlaybackStateChanged(
                    isPlaying = isPlaying,
                    adIndex = adIndex,
                    total = total
                )
            )
        }
    }

    // Create ExoPlayer with SCTE-35 support
    val contentPlayer = remember(video.id) {
        val player = ExoPlayer.Builder(context).build()

        val mediaItem = MediaItem.fromUri(video.videoUrl)

        // Store player and media item in ad manager
        adVideoPlayer.setPlayer(
            player = player,
            mediaItem = mediaItem
        )

        // Add SCTE-35 handler
        val scte35Handler = Scte35Handler(
            onAdBreakStart = { durationMs, positionMs ->
                println("🎯 SCTE-35 Ad break detected: ${durationMs}ms")
                onIntent(Scte35Intent.Scte35MarkerFound)

                // Fetch ad URLs (in production, this would be from an ad server)
                val adUrls = AdVideoPlayer.getSampleAdUrls()

                // Play ad videos
                adVideoPlayer.playAdBreak(adUrls = adUrls) {
                    println("✅ Ad break complete, resumed content")
                }
            },
            onAdBreakEnd = {
                // SCTE-35 signaled return to content
                adVideoPlayer.endAdBreak()
            }
        )

        with(player) {
            addListener(scte35Handler)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        player
    }

    // Observe player playback state to update pause/play button
    LaunchedEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlayWhenReadyChanged(
                playWhenReady: Boolean,
                reason: Int
            ) {
                isPlaying = playWhenReady
            }
        }
        contentPlayer.addListener(listener)

        // Initialize state
        isPlaying = contentPlayer.playWhenReady
    }

    // Auto-hide controls after 3 seconds when shown (during ad playback)
    LaunchedEffect(showAdControls, state.isPlayingAd) {
        if (showAdControls && state.isPlayingAd) {
            delay(3000)
            showAdControls = false
        }
    }

    // Show controls when ad starts playing
    LaunchedEffect(state.isPlayingAd) {
        if (state.isPlayingAd) {
            showAdControls = true
        }
    }

    // Update ad progress periodically during ad playback
    LaunchedEffect(state.isPlayingAd) {
        if (state.isPlayingAd) {
            while (state.isPlayingAd) {
                val duration = contentPlayer.duration
                val currentPosition = contentPlayer.currentPosition

                if (duration > 0) {
                    adProgress = currentPosition.toFloat() / duration.toFloat()
                }

                delay(100) // Update every 100ms
            }
            adProgress = 0f // Reset when ad ends
        }
    }

    // Fallback: Simulate ad breaks at specific positions if no real SCTE-35 markers detected
    // Note: This uses a pure stateless scheduler that doesn't depend on LaunchedEffect lifecycle
    val adBreakScheduler = remember { SimulatedAdBreakScheduler() }

    LaunchedEffect(contentPlayer) {
        // Track triggered positions locally within this LaunchedEffect
        // This prevents race conditions during async state updates
        val localTriggeredPositions = mutableSetOf<Long>()

        println("🔍 Simulated ad break scheduler started")

        // Periodically check if we should trigger an ad break (every 500ms)
        while (true) {
            delay(500)

            val currentPosition = contentPlayer.currentPosition

            val adBreakPosition = adBreakScheduler.calculateAdBreakPosition(
                currentPosition = currentPosition,
                triggeredPositions = localTriggeredPositions,
                isPlayingAd = state.isPlayingAd,
                scte35Detected = state.scte35MarkersDetected
            )

            if (adBreakPosition != null) {
                println("⚠️ No SCTE-35 markers detected in stream, using simulated ad breaks for demo")
                println("🎬 Simulated ad break at ${adBreakPosition}ms")

                // Mark as triggered locally IMMEDIATELY (synchronous)
                localTriggeredPositions.add(adBreakPosition)

                println("🚀 Calling playAdBreak...")
                // Actually play the ads
                val adUrls = AdVideoPlayer.getSampleAdUrls()
                adVideoPlayer.playAdBreak(
                    adUrls = adUrls,
                    onAdBreakComplete = {
                        println("✅ Simulated ad break complete")
                    }
                )
            }
        }
    }

    // Cleanup on dispose
    DisposableEffect(video.id) {
        onDispose {
            adVideoPlayer.release()
            contentPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SCTE-35 Demo") },
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
                .padding(paddingValues = paddingValues)
        ) {
            // Video player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 16f / 9f)
                    .background(color = Color.Black)
                    .clickable {
                        // Toggle controls visibility when tapping on player area
                        if (state.isPlayingAd) {
                            showAdControls = !showAdControls
                        }
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = contentPlayer
                            useController = !state.isPlayingAd // Hide controller during ads
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { playerView ->
                        playerView.useController = !state.isPlayingAd // Update controller state
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Ad indicator when playing ads
                if (state.isPlayingAd) {
                    Scte35AdIndicator(
                        currentAdIndex = state.currentAdIndex,
                        totalAds = state.totalAds
                    )

                    // Play/Pause button for ads (centered overlay) - only visible when tapped
                    if (showAdControls) {
                        Surface(
                            modifier = Modifier
                                .size(size = 64.dp)
                                .align(alignment = Alignment.Center)
                                .clickable {
                                    val newState = !isPlaying
                                    contentPlayer.playWhenReady = newState
                                    isPlaying = newState
                                },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) {
                                        Icons.Default.Pause
                                    } else {
                                        Icons.Default.PlayArrow
                                    },
                                    contentDescription = if (isPlaying) {
                                        "Pause Ad"
                                    } else {
                                        "Play Ad"
                                    },
                                    tint = Color.White,
                                    modifier = Modifier.size(size = 32.dp)
                                )
                            }
                        }
                    }

                    // YouTube-style progress bar (only visible during ads)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(alignment = Alignment.BottomCenter)
                    ) {
                        LinearProgressIndicator(
                            progress = { adProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = 4.dp),
                            color = Color(0xFFFFD700), // YouTube yellow
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // Information section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(height = 8.dp))

                Text(
                    video.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(height = 16.dp))

                // Test Ad Button
                if (!state.isPlayingAd) {
                    Button(
                        onClick = {
                            println("🧪 Manual ad test triggered")
                            onIntent(Scte35Intent.ManualAdBreakRequested)
                            val adUrls = AdVideoPlayer.getSampleAdUrls()
                            adVideoPlayer.playAdBreak(adUrls) {
                                println("✅ Test ad complete")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(color = 0xFFE91E63)
                        )
                    ) {
                        Text("🧪 Test Ad Now (Manual Trigger)")
                    }

                    Spacer(modifier = Modifier.height(height = 16.dp))
                }

                // SCTE-35 Info Card
                Scte35InfoCard(
                    isPlayingAd = state.isPlayingAd,
                    scte35Detected = state.scte35MarkersDetected
                )
            }
        }
    }
}

@Composable
fun Scte35AdIndicator(
    currentAdIndex: Int,
    totalAds: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        Card(
            modifier = Modifier.align(alignment = Alignment.TopEnd),
            colors = CardDefaults.cardColors(
                containerColor = Color(color = 0xFFE91E63).copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.padding(all = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎬 AD",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Ad ${currentAdIndex + 1} of $totalAds",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun Scte35InfoCard(
    isPlayingAd: Boolean,
    scte35Detected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayingAd) {
                Color(color = 0xFFE91E63).copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Text(
                text = "🎯 SCTE-35 Real Ad Insertion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(height = 12.dp))

            // SCTE-35 Detection Status
            Text(
                text = if (scte35Detected) {
                    "✅ Real SCTE-35 markers detected!"
                } else {
                    "⚠️ Using simulated ad breaks (no SCTE-35 in stream)"
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (scte35Detected) Color(color = 0xFF4CAF50) else Color(color = 0xFFFFA726)
            )

            Spacer(modifier = Modifier.height(height = 12.dp))

            Text(
                text = "How it works:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            if (scte35Detected) {
                InfoBullet(text = "1. Stream contains real SCTE-35 markers")
                InfoBullet(text = "2. ExoPlayer detects markers automatically")
                InfoBullet(text = "3. Content pauses when marker hit")
                InfoBullet(text = "4. Actual ad videos play sequentially")
                InfoBullet(text = "5. Content resumes after ads complete")
            } else {
                InfoBullet(text = "Simulated ad breaks at: 30s, 90s, 150s")
                InfoBullet(text = "Click 'Test Ad Now' to trigger instantly")
                InfoBullet(text = "Real ads play (not overlays)")
                InfoBullet(text = "Content resumes after ads")
            }

            Spacer(modifier = Modifier.height(height = 12.dp))

            Text(
                text = if (isPlayingAd) {
                    "🔴 Currently Playing Ad Video"
                } else {
                    "✅ Playing Main Content"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPlayingAd) Color(color = 0xFFE91E63) else Color(color = 0xFF4CAF50)
            )
        }
    }
}

@Composable
fun InfoBullet(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.7f),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
