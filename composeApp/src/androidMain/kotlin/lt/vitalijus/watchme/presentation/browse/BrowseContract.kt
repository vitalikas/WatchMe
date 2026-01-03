package lt.vitalijus.watchme.presentation.browse

import lt.vitalijus.watchme.architecture.UiEffect
import lt.vitalijus.watchme.architecture.UiIntent
import lt.vitalijus.watchme.architecture.UiState
import lt.vitalijus.watchme.domain.model.Video

/**
 * Browse Screen State (Immutable)
 */
data class BrowseState(
    val isLoading: Boolean = true,
    val allVideos: List<Video> = emptyList(),
    val displayedVideos: List<Video> = emptyList(),
    val selectedCategory: String = "All",
    val categories: List<String> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) : UiState

/**
 * Browse Screen Intents (User Actions)
 */
sealed class BrowseIntent : UiIntent {
    object LoadVideos : BrowseIntent()
    object Refresh : BrowseIntent()
    data class SelectCategory(val category: String) : BrowseIntent()
    data class SearchVideos(val query: String) : BrowseIntent()
    data class VideoClicked(val video: Video) : BrowseIntent()
    object AnalyticsClicked : BrowseIntent()
}

/**
 * Browse Screen Effects (One-time events)
 */
sealed class BrowseEffect : UiEffect {
    data class NavigateToPlayer(val videoId: String) : BrowseEffect()
    object NavigateToAnalytics : BrowseEffect()
    data class ShowError(val message: String) : BrowseEffect()
}
