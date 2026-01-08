package lt.vitalijus.watchme.ui.scte35_player

import lt.vitalijus.watchme.architecture.UiEffect
import lt.vitalijus.watchme.architecture.UiIntent
import lt.vitalijus.watchme.architecture.UiState
import lt.vitalijus.watchme.domain.model.Video

/**
 * Defines the contract between the SCTE-35 Player UI (Screen) and the ViewModel.
 */

// State - "What to draw"
data class Scte35State(
    val isLoading: Boolean = true,
    val video: Video? = null,
    val isPlayingAd: Boolean = false,
    val currentAdIndex: Int = 0,
    val totalAds: Int = 0,
    val scte35MarkersDetected: Boolean = false,
    val triggeredAdPositions: Set<Long> = emptySet(),
    val error: String? = null
) : UiState

// Intent - "What the user or system wants to do"
sealed class Scte35Intent : UiIntent {
    data class LoadVideo(val videoId: String) : Scte35Intent()
    data class SimulateAdBreakAt(val position: Long) : Scte35Intent()
    object ManualAdBreakRequested : Scte35Intent()
    data class AdPlaybackStateChanged(
        val isPlaying: Boolean,
        val adIndex: Int,
        val total: Int
    ) : Scte35Intent()

    object Scte35MarkerFound : Scte35Intent()
}

// Effect - "One-time events from the ViewModel"
sealed class Scte35Effect : UiEffect