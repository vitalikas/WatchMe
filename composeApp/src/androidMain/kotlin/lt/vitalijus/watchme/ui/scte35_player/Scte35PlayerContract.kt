package lt.vitalijus.watchme.ui.scte35_player

import lt.vitalijus.watchme.architecture.UiEffect
import lt.vitalijus.watchme.architecture.UiIntent
import lt.vitalijus.watchme.architecture.UiState
import lt.vitalijus.watchme.domain.model.Video

/**
 * Defines the contract between the SCTE-35 Player UI (Screen) and the ViewModel.
 */
data class Scte35State(
    val isLoading: Boolean = true,
    val video: Video? = null,
    val playbackPosition: Long = 0L,
    val scte35MarkersDetected: Boolean = false,

    val isPlayingAd: Boolean = false,
    val triggeredAdPositions: Set<Long> = emptySet(),
    val currentAdIndex: Int = 0,
    val totalAds: Int = 0,
    val adPlaybackPosition: Long = 0L,
    val adIndexToPlay: Int = 0,
    val error: String? = null
) : UiState

sealed class Scte35Intent : UiIntent {
    data class LoadVideo(val videoId: String) : Scte35Intent()
    data class SimulateAdBreakAt(val position: Long) : Scte35Intent()
    object ManualAdBreakRequested : Scte35Intent()
    data class SavePlaybackPosition(val position: Long) : Scte35Intent()
    data class AdPlaybackStateChanged(
        val isPlaying: Boolean,
        val adIndex: Int,
        val total: Int
    ) : Scte35Intent()

    object Scte35MarkerFound : Scte35Intent()

    data class SaveAdState(
        val adPosition: Long,
        val adIndex: Int
    ) : Scte35Intent()
}

sealed class Scte35Effect : UiEffect
