package lt.vitalijus.watchme.ui.scte35_player

import lt.vitalijus.watchme.architecture.MviViewModel
import lt.vitalijus.watchme.domain.usecase.GetVideoByIdUseCase

/**
 * SCTE-35 Player ViewModel
 *
 * Responsibilities:
 * - Hold player UI state
 * - Handle player intents
 * - Persist state across configuration changes
 * - Track SCTE-35 marker detection
 * - Track simulated ad break positions
 * - NO ExoPlayer or AdVideoPlayer management (stays in Composable)
 */
class Scte35PlayerViewModel(
    private val getVideoByIdUseCase: GetVideoByIdUseCase
) : MviViewModel<Scte35State, Scte35Intent, Scte35Effect>(
    initialState = Scte35State()
) {

    /**
     * Redux Reducer: Pure function (State, Intent) -> State
     */
    override suspend fun reduce(intent: Scte35Intent) {
        when (intent) {
            is Scte35Intent.LoadVideo -> loadVideo(videoId = intent.videoId)
            is Scte35Intent.SimulateAdBreakAt -> triggerSimulatedAdBreak(position = intent.position)
            is Scte35Intent.ManualAdBreakRequested -> {

            }

            is Scte35Intent.AdPlaybackStateChanged -> updateAdPlaybackState(
                isPlaying = intent.isPlaying,
                adIndex = intent.adIndex,
                total = intent.total
            )

            is Scte35Intent.Scte35MarkerFound -> markScte35Detected()
            is Scte35Intent.SavePlaybackPosition -> savePosition(position = intent.position)
            is Scte35Intent.SaveAdState -> saveAdState(state = intent)
        }
    }

    private suspend fun loadVideo(videoId: String) {
        setState { copy(isLoading = true) }

        getVideoByIdUseCase(videoId = videoId)
            .onSuccess { video ->
                setState {
                    copy(
                        isLoading = false,
                        video = video,
                        error = null
                    )
                }
            }
            .onFailure { throwable ->
                setState {
                    copy(
                        isLoading = false,
                        error = "Failed to load video: ${throwable.message}"
                    )
                }
            }
    }

    private fun triggerSimulatedAdBreak(position: Long) {
        setState {
            copy(
                triggeredAdPositions = triggeredAdPositions + position
            )
        }
    }

    private fun updateAdPlaybackState(
        isPlaying: Boolean,
        adIndex: Int,
        total: Int
    ) {
        setState {
            copy(
                isPlayingAd = isPlaying,
                currentAdIndex = adIndex,
                totalAds = total
            )
        }
    }

    private fun markScte35Detected() {
        setState { copy(scte35MarkersDetected = true) }
    }

    private fun savePosition(position: Long) {
        if (position > 0) {
            setState { copy(playbackPosition = position) }
        }
    }

    private fun saveAdState(state: Scte35Intent.SaveAdState) {
        setState {
            copy(
                adPlaybackPosition = state.adPosition,
                adIndexToPlay = state.adIndex
            )
        }
    }
}
