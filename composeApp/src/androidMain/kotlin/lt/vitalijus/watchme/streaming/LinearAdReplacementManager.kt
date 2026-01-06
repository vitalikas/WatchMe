package lt.vitalijus.watchme.streaming

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Linear Ad Replacement (LAR) Manager
 *
 * LAR is a technology that replaces ads in linear video streams (live TV)
 * with targeted advertisements. This is crucial for streaming services like TV2 Play
 * to monetize live content while providing personalized ad experiences.
 *
 * Key concepts:
 * - Ad Pods: Groups of ads that replace content segments
 * - Cue Points: Timing markers where ads should be inserted
 * - SCTE-35 markers: Industry standard for ad insertion signaling (simulated here)
 */

data class AdPod(
    val id: String,
    val ads: List<Ad>,
    val startPosition: Long, // Position in video (ms) where ad pod starts
    val duration: Long // Total duration of ad pod (ms)
)

data class Ad(
    val id: String,
    val title: String,
    val duration: Long, // in milliseconds
    val advertiser: String,
    val clickThroughUrl: String? = null
)

/**
 * Simulates ad cue points for Linear Ad Replacement
 */
object LinearAdReplacementManager {

    private val _currentAdPod = MutableStateFlow<AdPod?>(null)
    val currentAdPod: StateFlow<AdPod?> = _currentAdPod.asStateFlow()

    private val _isAdPlaying = MutableStateFlow(false)
    val isAdPlaying: StateFlow<Boolean> = _isAdPlaying.asStateFlow()

    /**
     * Predefined ad pods for demo purposes
     * In production, these would come from an ad server based on SCTE-35 markers
     */
    private val sampleAdPods = listOf(
        AdPod(
            id = "pod1",
            ads = listOf(
                Ad("ad1", "Premium Coffee - 30s", 30000, "CoffeeBreak Inc."),
                Ad("ad2", "New Smartphone - 20s", 20000, "TechGiant Corp.")
            ),
            startPosition = 120000, // 2 minutes into content
            duration = 50000 // 50 seconds total
        ),
        AdPod(
            id = "pod2",
            ads = listOf(
                Ad("ad3", "Summer Sale - 15s", 15000, "Fashion Retailer"),
                Ad("ad4", "Car Insurance - 30s", 30000, "Insurance Co."),
                Ad("ad5", "Energy Drink - 15s", 15000, "Beverage Brand")
            ),
            startPosition = 300000, // 5 minutes into content
            duration = 60000 // 60 seconds total
        ),
        AdPod(
            id = "pod3",
            ads = listOf(
                Ad("ad6", "Holiday Destination - 30s", 30000, "Travel Agency")
            ),
            startPosition = 480000, // 8 minutes into content
            duration = 30000 // 30 seconds
        )
    )

    /**
     * Check if an ad should be played at the current position
     * This simulates SCTE-35 marker detection in live streams
     */
    fun checkForAdBreak(
        currentPosition: Long,
        videoId: String,
        hasAds: Boolean
    ): AdPod? {
        if (!hasAds) return null

        // Find ad pod that should be active at current position
        return sampleAdPods.find { pod ->
            val inTimeRange = currentPosition >= pod.startPosition &&
                    currentPosition < (pod.startPosition + pod.duration)
            val notCurrentlyPlaying = _currentAdPod.value?.id != pod.id

            inTimeRange && notCurrentlyPlaying
        }
    }

    /**
     * Start playing an ad pod
     */
    fun startAdPod(adPod: AdPod) {
        _currentAdPod.value = adPod
        _isAdPlaying.value = true
        println("🎬 LAR: Starting ad pod ${adPod.id} with ${adPod.ads.size} ads")
    }

    /**
     * End current ad pod
     */
    fun endAdPod() {
        val podId = _currentAdPod.value?.id
        _currentAdPod.value = null
        _isAdPlaying.value = false
        println("✅ LAR: Ended ad pod $podId")
    }

    /**
     * Get current ad in the pod
     */
    fun getCurrentAd(positionInPod: Long): Ad? {
        val adPod = _currentAdPod.value ?: return null
        var cumulativeDuration = 0L

        for (ad in adPod.ads) {
            if (positionInPod >= cumulativeDuration &&
                positionInPod < cumulativeDuration + ad.duration
            ) {
                return ad
            }
            cumulativeDuration += ad.duration
        }

        return null
    }

    /**
     * Reset LAR state
     */
    fun reset() {
        _currentAdPod.value = null
        _isAdPlaying.value = false
    }

    /**
     * Check if current position is within any ad pod
     */
    fun isInAdRange(currentPosition: Long, hasAds: Boolean): Boolean {
        if (!hasAds) return false

        return sampleAdPods.any { pod ->
            currentPosition >= pod.startPosition &&
                    currentPosition < (pod.startPosition + pod.duration)
        }
    }

    /**
     * Get all ad cue points for a video (useful for UI markers)
     */
    fun getAdCuePoints(hasAds: Boolean): List<Long> {
        return if (hasAds) {
            sampleAdPods.map { it.startPosition }
        } else {
            emptyList()
        }
    }

    /**
     * Simulate server-side ad insertion (SSAI) metadata
     * In production, this would come from your ad server
     */
    fun getAdMetadata(): Map<String, Any> {
        return mapOf(
            "adServerUrl" to "https://ads.tv2play.dk/vast",
            "adType" to "LINEAR",
            "adReplacementStrategy" to "SERVER_SIDE",
            "scte35Enabled" to true,
            "targetingParameters" to mapOf(
                "country" to "DK",
                "language" to "da",
                "contentType" to "live"
            )
        )
    }
}
