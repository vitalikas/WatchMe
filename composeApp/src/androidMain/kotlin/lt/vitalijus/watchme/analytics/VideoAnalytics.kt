package lt.vitalijus.watchme.analytics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Video Analytics Event Types
 * Tracks various streaming events crucial for content performance monitoring
 */
sealed class AnalyticsEvent {
    data class VideoStarted(val videoId: String, val videoTitle: String, val timestamp: Long) : AnalyticsEvent()
    data class VideoPlayed(val videoId: String, val position: Long, val timestamp: Long) : AnalyticsEvent()
    data class VideoPaused(val videoId: String, val position: Long, val timestamp: Long) : AnalyticsEvent()
    data class VideoCompleted(val videoId: String, val totalDuration: Long, val timestamp: Long) : AnalyticsEvent()
    data class BufferingStarted(val videoId: String, val position: Long, val timestamp: Long) : AnalyticsEvent()
    data class BufferingEnded(val videoId: String, val bufferingDuration: Long, val timestamp: Long) : AnalyticsEvent()
    data class QualityChanged(val videoId: String, val fromQuality: String, val toQuality: String, val timestamp: Long) : AnalyticsEvent()
    data class AdShown(val videoId: String, val adId: String, val adPosition: Long, val timestamp: Long) : AnalyticsEvent()
    data class DrmInitialized(val videoId: String, val drmScheme: String, val timestamp: Long) : AnalyticsEvent()
    data class ErrorOccurred(val videoId: String, val errorMessage: String, val timestamp: Long) : AnalyticsEvent()
}

/**
 * Analytics Summary for Dashboard
 */
data class AnalyticsSummary(
    val totalVideosWatched: Int = 0,
    val totalPlayTime: Long = 0, // in milliseconds
    val totalBufferingEvents: Int = 0,
    val totalAdsShown: Int = 0,
    val totalDrmInitializations: Int = 0,
    val totalErrors: Int = 0,
    val averageBufferingTime: Long = 0,
    val mostWatchedVideo: String? = null
)

/**
 * Video Analytics Tracker
 * Singleton service for tracking video streaming analytics
 * In a production environment, this would send data to a backend analytics service
 */
object VideoAnalyticsTracker {
    private val _events = MutableStateFlow<List<AnalyticsEvent>>(emptyList())
    val events: StateFlow<List<AnalyticsEvent>> = _events.asStateFlow()

    private val _summary = MutableStateFlow(AnalyticsSummary())
    val summary: StateFlow<AnalyticsSummary> = _summary.asStateFlow()

    private val videoWatchCount = mutableMapOf<String, Int>() // videoTitle -> count
    private val videoTitles = mutableMapOf<String, String>() // videoId -> videoTitle
    private val videoPlayStartTimes = mutableMapOf<String, Long>() // videoId -> startTime
    private var totalBufferingTime = 0L
    private var bufferingStartTime = 0L

    /**
     * Track a video analytics event
     */
    fun trackEvent(event: AnalyticsEvent) {
        // Add to events list
        _events.value = _events.value + event

        // Update summary based on event type
        updateSummary(event)

        // In production, this would send data to analytics backend
        logEvent(event)
    }

    private fun updateSummary(event: AnalyticsEvent) {
        val currentSummary = _summary.value

        val updatedSummary = when (event) {
            is AnalyticsEvent.VideoStarted -> {
                // Store video title mapping
                videoTitles[event.videoId] = event.videoTitle
                videoWatchCount[event.videoTitle] = (videoWatchCount[event.videoTitle] ?: 0) + 1
                videoPlayStartTimes[event.videoId] = event.timestamp
                currentSummary.copy(
                    totalVideosWatched = currentSummary.totalVideosWatched + 1,
                    mostWatchedVideo = findMostWatchedVideo()
                )
            }
            is AnalyticsEvent.VideoPaused -> {
                // Update play time when paused
                val startTime = videoPlayStartTimes[event.videoId]
                val playDuration = if (startTime != null) {
                    event.timestamp - startTime
                } else 0
                currentSummary.copy(
                    totalPlayTime = currentSummary.totalPlayTime + playDuration
                )
            }
            is AnalyticsEvent.VideoCompleted -> {
                // Update play time when completed
                val startTime = videoPlayStartTimes[event.videoId]
                val playDuration = if (startTime != null) {
                    event.timestamp - startTime
                } else event.totalDuration
                videoPlayStartTimes.remove(event.videoId)
                currentSummary.copy(
                    totalPlayTime = currentSummary.totalPlayTime + playDuration
                )
            }
            is AnalyticsEvent.BufferingStarted -> {
                bufferingStartTime = event.timestamp
                currentSummary.copy(
                    totalBufferingEvents = currentSummary.totalBufferingEvents + 1
                )
            }
            is AnalyticsEvent.BufferingEnded -> {
                totalBufferingTime += event.bufferingDuration
                currentSummary.copy(
                    averageBufferingTime = if (currentSummary.totalBufferingEvents > 0) {
                        totalBufferingTime / currentSummary.totalBufferingEvents
                    } else 0
                )
            }
            is AnalyticsEvent.AdShown -> {
                currentSummary.copy(
                    totalAdsShown = currentSummary.totalAdsShown + 1
                )
            }
            is AnalyticsEvent.DrmInitialized -> {
                currentSummary.copy(
                    totalDrmInitializations = currentSummary.totalDrmInitializations + 1
                )
            }
            is AnalyticsEvent.ErrorOccurred -> {
                currentSummary.copy(
                    totalErrors = currentSummary.totalErrors + 1
                )
            }
            else -> currentSummary
        }

        _summary.value = updatedSummary
    }

    private fun findMostWatchedVideo(): String {
        return videoWatchCount.maxByOrNull { it.value }?.key ?: "None"
    }

    private fun logEvent(event: AnalyticsEvent) {
        // In production, send to analytics backend (e.g., Firebase, Mixpanel, custom backend)
        println("📊 Analytics Event: ${event.javaClass.simpleName} - $event")
    }

    /**
     * Clear all analytics data (useful for testing)
     */
    fun clearAnalytics() {
        _events.value = emptyList()
        _summary.value = AnalyticsSummary()
        videoWatchCount.clear()
        videoTitles.clear()
        videoPlayStartTimes.clear()
        totalBufferingTime = 0
        bufferingStartTime = 0
    }

    /**
     * Get recent events (last N events)
     */
    fun getRecentEvents(count: Int = 10): List<AnalyticsEvent> {
        return _events.value.takeLast(count)
    }
}
