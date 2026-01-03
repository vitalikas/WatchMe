package lt.vitalijus.watchme.presentation.browse

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lt.vitalijus.watchme.architecture.MviViewModel
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.usecase.*

/**
 * Browse ViewModel (Redux-style)
 *
 * Responsibilities:
 * - Hold UI state
 * - Handle user intents
 * - Coordinate use cases
 * - NO business logic (delegated to use cases)
 */
class BrowseViewModel(
    private val getVideosUseCase: GetVideosUseCase,
    private val filterVideosByCategoryUseCase: FilterVideosByCategoryUseCase,
    private val searchVideosUseCase: SearchVideosUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : MviViewModel<BrowseState, BrowseIntent, BrowseEffect>(
    initialState = BrowseState()
) {

    init {
        handleIntent(BrowseIntent.LoadVideos)
    }

    /**
     * Redux Reducer: Pure function (State, Intent) -> State
     */
    override suspend fun reduce(intent: BrowseIntent) {
        when (intent) {
            is BrowseIntent.LoadVideos -> loadVideos()
            is BrowseIntent.Refresh -> refreshVideos()
            is BrowseIntent.SelectCategory -> selectCategory(intent.category)
            is BrowseIntent.SearchVideos -> searchVideos(intent.query)
            is BrowseIntent.VideoClicked -> navigateToPlayer(intent.video)
            is BrowseIntent.AnalyticsClicked -> navigateToAnalytics()
        }
    }

    private fun loadVideos() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            getVideosUseCase()
                .collect { videos ->
                    setState {
                        val categories = getCategoriesUseCase(videos)
                        val filtered = applyFilters(videos)

                        copy(
                            isLoading = false,
                            allVideos = videos,
                            displayedVideos = filtered,
                            categories = categories,
                            error = null
                        )
                    }
                }
        }
    }

    private suspend fun refreshVideos() {
        setState { copy(isLoading = true) }
        loadVideos()
    }

    private suspend fun selectCategory(category: String) {
        setState {
            val filtered = applyFilters(allVideos, category = category)
            copy(
                selectedCategory = category,
                displayedVideos = filtered
            )
        }
    }

    private suspend fun searchVideos(query: String) {
        setState {
            val filtered = applyFilters(allVideos, searchQuery = query)
            copy(
                searchQuery = query,
                displayedVideos = filtered
            )
        }
    }

    private fun navigateToPlayer(video: Video) {
        sendEffect(BrowseEffect.NavigateToPlayer(video.id))
    }

    private fun navigateToAnalytics() {
        sendEffect(BrowseEffect.NavigateToAnalytics)
    }

    /**
     * Helper: Apply all active filters
     * Pure function - no side effects
     */
    private fun applyFilters(
        videos: List<Video>,
        category: String = currentState.selectedCategory,
        searchQuery: String = currentState.searchQuery
    ): List<Video> {
        var result = videos

        // Apply category filter
        result = filterVideosByCategoryUseCase(result, category)

        // Apply search filter
        if (searchQuery.isNotBlank()) {
            result = searchVideosUseCase(result, searchQuery)
        }

        return result
    }
}
