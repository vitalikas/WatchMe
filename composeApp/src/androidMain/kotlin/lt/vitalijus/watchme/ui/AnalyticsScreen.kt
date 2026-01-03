package lt.vitalijus.watchme.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lt.vitalijus.watchme.analytics.AnalyticsEvent
import lt.vitalijus.watchme.analytics.VideoAnalyticsTracker
import java.text.SimpleDateFormat
import java.util.*

/**
 * Analytics Dashboard Screen - Third Screen
 * Displays comprehensive video analytics and streaming metrics
 * Demonstrates understanding of video performance monitoring
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onBack: () -> Unit
) {
    val summary by VideoAnalyticsTracker.summary.collectAsState()
    val events by VideoAnalyticsTracker.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { VideoAnalyticsTracker.clearAnalytics() }) {
                        Icon(Icons.Default.Delete, "Clear Analytics")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary metrics
            item {
                Text(
                    "📊 Summary Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Videos Watched",
                        value = summary.totalVideosWatched.toString(),
                        icon = "🎬",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Play Time",
                        value = formatMilliseconds(summary.totalPlayTime),
                        icon = "⏱️",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Buffering Events",
                        value = summary.totalBufferingEvents.toString(),
                        icon = "⏳",
                        modifier = Modifier.weight(1f),
                        color = if (summary.totalBufferingEvents > 5) 
                            MaterialTheme.colorScheme.errorContainer 
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                    MetricCard(
                        title = "Avg Buffer Time",
                        value = "${summary.averageBufferingTime}ms",
                        icon = "📶",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Ads Shown (LAR)",
                        value = summary.totalAdsShown.toString(),
                        icon = "📺",
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFF3E0)
                    )
                    MetricCard(
                        title = "DRM Inits",
                        value = summary.totalDrmInitializations.toString(),
                        icon = "🔒",
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE1BEE7)
                    )
                }
            }

            item {
                MetricCard(
                    title = "Errors",
                    value = summary.totalErrors.toString(),
                    icon = "⚠️",
                    modifier = Modifier.fillMaxWidth(),
                    color = if (summary.totalErrors > 0) 
                        MaterialTheme.colorScheme.errorContainer 
                    else MaterialTheme.colorScheme.tertiaryContainer
                )
            }

            // Most watched video
            if (summary.mostWatchedVideo != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "🏆 Most Watched",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                summary.mostWatchedVideo ?: "None",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // Event log
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "📝 Event Log (Recent)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (events.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No events tracked yet.\nStart watching videos!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(events.takeLast(20).reversed()) { event ->
                    EventCard(event)
                }
            }

            // Explanatory card
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "ℹ️ About Video Analytics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "This dashboard tracks key streaming metrics:\n\n" +
                            "• Playback events (play, pause, complete)\n" +
                            "• Quality of Service (buffering, errors)\n" +
                            "• LAR ad impressions\n" +
                            "• DRM initialization events\n" +
                            "• User engagement metrics\n\n" +
                            "In production, this data would be sent to analytics platforms " +
                            "like Google Analytics, Mixpanel, or custom solutions for " +
                            "content optimization and user experience improvements.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EventCard(event: AnalyticsEvent) {
    val eventDisplay = getEventDisplay(event)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = eventDisplay.color
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    eventDisplay.icon,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        eventDisplay.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        eventDisplay.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                formatTimestamp(getEventTimestamp(event)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getEventDisplay(event: AnalyticsEvent): EventDisplay {
    return when (event) {
        is AnalyticsEvent.VideoStarted -> EventDisplay(
            "▶️", "Video Started", event.videoTitle, Color(0xFFBBDEFB)
        )
        is AnalyticsEvent.VideoPlayed -> EventDisplay(
            "▶️", "Playing", "Position: ${formatMilliseconds(event.position)}", Color(0xFFF5F5F5)
        )
        is AnalyticsEvent.VideoPaused -> EventDisplay(
            "⏸️", "Paused", "Position: ${formatMilliseconds(event.position)}", Color(0xFFF5F5F5)
        )
        is AnalyticsEvent.VideoCompleted -> EventDisplay(
            "✅", "Completed", "Duration: ${formatMilliseconds(event.totalDuration)}", Color(0xFFD1C4E9)
        )
        is AnalyticsEvent.BufferingStarted -> EventDisplay(
            "⏳", "Buffering Started", "Position: ${formatMilliseconds(event.position)}", Color(0xFFFFF9C4)
        )
        is AnalyticsEvent.BufferingEnded -> EventDisplay(
            "✓", "Buffering Ended", "Duration: ${event.bufferingDuration}ms", Color(0xFFC8E6C9)
        )
        is AnalyticsEvent.QualityChanged -> EventDisplay(
            "📶", "Quality Changed", "${event.fromQuality} → ${event.toQuality}", Color(0xFFB3E5FC)
        )
        is AnalyticsEvent.AdShown -> EventDisplay(
            "📺", "Ad Shown (LAR)", "Ad ID: ${event.adId}", Color(0xFFFFE0B2)
        )
        is AnalyticsEvent.DrmInitialized -> EventDisplay(
            "🔒", "DRM Initialized", event.drmScheme, Color(0xFFE1BEE7)
        )
        is AnalyticsEvent.ErrorOccurred -> EventDisplay(
            "⚠️", "Error", event.errorMessage, Color(0xFFFFCDD2)
        )
    }
}

private fun getEventTimestamp(event: AnalyticsEvent): Long {
    return when (event) {
        is AnalyticsEvent.VideoStarted -> event.timestamp
        is AnalyticsEvent.VideoPlayed -> event.timestamp
        is AnalyticsEvent.VideoPaused -> event.timestamp
        is AnalyticsEvent.VideoCompleted -> event.timestamp
        is AnalyticsEvent.BufferingStarted -> event.timestamp
        is AnalyticsEvent.BufferingEnded -> event.timestamp
        is AnalyticsEvent.QualityChanged -> event.timestamp
        is AnalyticsEvent.AdShown -> event.timestamp
        is AnalyticsEvent.DrmInitialized -> event.timestamp
        is AnalyticsEvent.ErrorOccurred -> event.timestamp
    }
}

private data class EventDisplay(
    val icon: String,
    val title: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color
)

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatMilliseconds(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    
    return when {
        hours > 0 -> String.format("%dh %dm", hours, minutes % 60)
        minutes > 0 -> String.format("%dm %ds", minutes, seconds % 60)
        else -> String.format("%ds", seconds)
    }
}
