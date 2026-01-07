package lt.vitalijus.watchme.ui.player

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import lt.vitalijus.watchme.data.repository.KtorVideoRemoteDataSource
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.streaming.AdVideoPlayer
import lt.vitalijus.watchme.streaming.Scte35Handler

/**
 * SCTE-35 Player Screen - Wrapper that handles video loading
 */
@UnstableApi
@Composable
fun Scte35PlayerScreen(
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
            Scte35PlayerScreenContent(
                video = video!!,
                onBack = onBack
            )
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
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Create Ad Video Player manager
    val adVideoPlayer = remember { AdVideoPlayer(context) }
    
    // Observe ad playback state
    val isPlayingAd by adVideoPlayer.isPlayingAd.collectAsState()
    val currentAdIndex by adVideoPlayer.currentAdIndex.collectAsState()
    val totalAds by adVideoPlayer.totalAds.collectAsState()
    
    // Track if we've detected any real SCTE-35 markers
    var scte35MarkersDetected by remember { mutableStateOf(false) }
    var triggeredAdPositions by remember { mutableStateOf(setOf<Long>()) }
    
    // Create ExoPlayer with SCTE-35 support
    val exoPlayer = remember(video.id) {
        val player = ExoPlayer.Builder(context).build()
        
        val mediaItem = MediaItem.fromUri(video.videoUrl)
        
        // Store player and media item in ad manager
        adVideoPlayer.setPlayer(player, mediaItem)
        
        // Add SCTE-35 handler
        val scte35Handler = Scte35Handler(
            onAdBreakStart = { durationMs, positionMs ->
                println("🎯 SCTE-35 Ad break detected: ${durationMs}ms")
                scte35MarkersDetected = true
                
                // Fetch ad URLs (in production, this would be from an ad server)
                val adUrls = AdVideoPlayer.getSampleAdUrls()
                
                // Play ad videos
                adVideoPlayer.playAdBreak(adUrls) {
                    println("✅ Ad break complete, resumed content")
                }
            },
            onAdBreakEnd = {
                // SCTE-35 signaled return to content
                adVideoPlayer.endAdBreak()
            }
        )
        
        player.addListener(scte35Handler)
        
        // Set media source (ExoPlayer will auto-detect the format)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        
        player
    }
    
    // Fallback: Simulate ad breaks at specific positions if no real SCTE-35 markers detected
    LaunchedEffect(exoPlayer, scte35MarkersDetected) {
        // Wait a bit to see if real markers are detected
        delay(5000)
        
        if (!scte35MarkersDetected && !isPlayingAd) {
            println("⚠️ No SCTE-35 markers detected in stream, using simulated ad breaks for demo")
            
            while (isActive) {
                val currentPosition = exoPlayer.currentPosition
                
                // Simulate ad breaks at specific positions (30s, 90s, 150s)
                val adBreakPositions = listOf(30000L, 90000L, 150000L)
                
                for (breakPosition in adBreakPositions) {
                    if (currentPosition >= breakPosition && 
                        currentPosition < breakPosition + 1000 && 
                        !triggeredAdPositions.contains(breakPosition) &&
                        !isPlayingAd) {
                        
                        println("🎬 Simulated ad break at ${breakPosition}ms")
                        triggeredAdPositions = triggeredAdPositions + breakPosition
                        
                        val adUrls = AdVideoPlayer.getSampleAdUrls()
                        adVideoPlayer.playAdBreak(adUrls) {
                            println("✅ Simulated ad break complete")
                        }
                        break
                    }
                }
                
                delay(500)
            }
        }
    }
    
    // Cleanup on dispose
    DisposableEffect(video.id) {
        onDispose {
            adVideoPlayer.release()
            exoPlayer.release()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SCTE-35 Demo") },
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
                
                // Ad indicator when playing ads
                if (isPlayingAd) {
                    Scte35AdIndicator(
                        currentAdIndex = currentAdIndex,
                        totalAds = totalAds
                    )
                }
            }
            
            // Information section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
                
                // Test Ad Button
                if (!isPlayingAd) {
                    Button(
                        onClick = {
                            println("🧪 Manual ad test triggered")
                            val adUrls = AdVideoPlayer.getSampleAdUrls()
                            adVideoPlayer.playAdBreak(adUrls) {
                                println("✅ Test ad complete")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        )
                    ) {
                        Text("🧪 Test Ad Now (Manual Trigger)")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // SCTE-35 Info Card
                Scte35InfoCard(
                    isPlayingAd = isPlayingAd,
                    scte35Detected = scte35MarkersDetected
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
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.align(Alignment.TopEnd),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE91E63).copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🎬 REAL AD",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Ad ${currentAdIndex + 1} of $totalAds",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                CircularProgressIndicator(
                    modifier = Modifier.height(16.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun Scte35InfoCard(isPlayingAd: Boolean, scte35Detected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayingAd) {
                Color(0xFFE91E63).copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "🎯 SCTE-35 Real Ad Insertion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // SCTE-35 Detection Status
            Text(
                if (scte35Detected) {
                    "✅ Real SCTE-35 markers detected!"
                } else {
                    "⚠️ Using simulated ad breaks (no SCTE-35 in stream)"
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (scte35Detected) Color(0xFF4CAF50) else Color(0xFFFFA726)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "How it works:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (scte35Detected) {
                InfoBullet("1. Stream contains real SCTE-35 markers")
                InfoBullet("2. ExoPlayer detects markers automatically")
                InfoBullet("3. Content pauses when marker hit")
                InfoBullet("4. Actual ad videos play sequentially")
                InfoBullet("5. Content resumes after ads complete")
            } else {
                InfoBullet("Simulated ad breaks at: 30s, 90s, 150s")
                InfoBullet("Click 'Test Ad Now' to trigger instantly")
                InfoBullet("Real ads play (not overlays)")
                InfoBullet("Content resumes after ads")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                if (isPlayingAd) {
                    "🔴 Currently Playing Ad Video"
                } else {
                    "✅ Playing Main Content"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPlayingAd) Color(0xFFE91E63) else Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun InfoBullet(text: String) {
    Text(
        "• $text",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.7f),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
