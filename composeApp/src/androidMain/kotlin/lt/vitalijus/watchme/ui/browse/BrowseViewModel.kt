package lt.vitalijus.watchme.ui.browse

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lt.vitalijus.watchme.architecture.MviViewModel
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.usecase.FilterVideosByCategoryUseCase
import lt.vitalijus.watchme.domain.usecase.GetCategoriesUseCase
import lt.vitalijus.watchme.domain.usecase.GetVideosUseCase
import lt.vitalijus.watchme.domain.usecase.RefreshVideosUseCase
import lt.vitalijus.watchme.domain.usecase.SearchVideosUseCase

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
    private val refreshVideosUseCase: RefreshVideosUseCase,
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
            is BrowseIntent.SelectCategory -> selectCategory(category = intent.category)
            is BrowseIntent.SearchVideos -> searchVideos(query = intent.query)
            is BrowseIntent.VideoClicked -> navigateToPlayer(video = intent.video)
            is BrowseIntent.AnalyticsClicked -> navigateToAnalytics()
        }
    }

    private fun loadVideos() {
        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    error = null
                )
            }

            getVideosUseCase()
                .collect { videos ->
                    setState {
                        val categories = getCategoriesUseCase(videos = videos)
                        val filteredVideos = applyFilters(videos = videos)

                        copy(
                            isLoading = false,
                            allVideos = videos,
                            displayedVideos = filteredVideos,
                            categories = categories,
                            error = null
                        )
                    }
                }
        }
    }

    private fun refreshVideos() {
        viewModelScope.launch {
            setState {
                copy(
                    isLoading = true,
                    error = null
                )
            }

            // Force refresh - invalidates cache and fetches fresh data
            refreshVideosUseCase()
                .fold(
                    onSuccess = { videos ->
                        val categories = getCategoriesUseCase(videos = videos)
                        val filteredVideos = applyFilters(videos = videos)

                        setState {
                            copy(
                                isLoading = false,
                                allVideos = videos,
                                displayedVideos = filteredVideos,
                                categories = categories,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        setState {
                            copy(
                                isLoading = false,
                                error = error.message ?: "Failed to refresh videos"
                            )
                        }
                        sendEffect(
                            effect = BrowseEffect.ShowError(
                                message = "Failed to refresh: ${error.message}"
                            )
                        )
                    }
                )
        }
    }

    private fun selectCategory(category: String) {
        setState {
            val filteredVideos = applyFilters(
                videos = allVideos,
                category = category
            )
            copy(
                selectedCategory = category,
                displayedVideos = filteredVideos
            )
        }
    }

    private fun searchVideos(query: String) {
        setState {
            val filteredVideos = applyFilters(
                videos = allVideos,
                searchQuery = query
            )
            copy(
                searchQuery = query,
                displayedVideos = filteredVideos
            )
        }
    }

    private fun navigateToPlayer(video: Video) {
        sendEffect(
            effect = BrowseEffect.NavigateToPlayer(
                videoId = video.id
            )
        )
    }

    private fun navigateToAnalytics() {
        sendEffect(effect = BrowseEffect.NavigateToAnalytics)
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
        result = filterVideosByCategoryUseCase(
            videos = result,
            category = category
        )

        // Apply search filter
        if (searchQuery.isNotBlank()) {
            result = searchVideosUseCase(
                videos = result,
                query = searchQuery
            )
        }

        return result
    }
}