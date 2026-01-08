package lt.vitalijus.watchme.ui.standard_player

import lt.vitalijus.watchme.architecture.UiEffect
import lt.vitalijus.watchme.architecture.UiIntent
import lt.vitalijus.watchme.architecture.UiState
import lt.vitalijus.watchme.streaming.AdPod

/**
 * Player Screen State (Immutable)
 */
data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isBuffering: Boolean = false,
    val currentQuality: String = "Auto",
    val error: String? = null,
    val isAdPlaying: Boolean = false,
    val currentAdPod: AdPod? = null
) : UiState

/**
 * Player Screen Intents (User Actions)
 */
sealed class PlayerIntent : UiIntent {

    data class UpdatePlaybackState(
        val isPlaying: Boolean,
        val currentPosition: Long,
        val duration: Long
    ) : PlayerIntent()

    data class UpdateBuffering(val isBuffering: Boolean) : PlayerIntent()
    data class UpdateQuality(val quality: String) : PlayerIntent()
    data class HandleError(val errorMessage: String) : PlayerIntent()
    data class UpdateAdState(
        val isAdPlaying: Boolean,
        val adPod: AdPod?
    ) : PlayerIntent()

    object ClearError : PlayerIntent()
}

/**
 * Player Screen Effects (One-time events)
 */
sealed class PlayerEffect : UiEffect {

    data class ShowError(val message: String) : PlayerEffect()
    data class TrackAnalyticsEvent(
        val eventType: String,
        val data: Map<String, Any>
    ) : PlayerEffect()
}
