package lt.vitalijus.watchme.domain.usecase

import kotlinx.coroutines.flow.Flow
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.repository.VideoRepository

/**
 * Use Case: Get all videos
 * Single Responsibility: Fetch and provide videos
 *
 * Benefits:
 * - Testable business logic
 * - Reusable across ViewModels
 * - Clear separation of concerns
 */
class GetVideosUseCase(
    private val repository: VideoRepository
) {
    /**
     * Execute use case
     * Returns Flow for reactive updates
     */
    operator fun invoke(): Flow<List<Video>> {
        return repository.observeVideos()
    }
}

/**
 * Use Case: Get video by ID
 */
class GetVideoByIdUseCase(
    private val repository: VideoRepository
) {
    suspend operator fun invoke(videoId: String): Result<Video?> {
        return repository.getVideoById(videoId)
    }
}

/**
 * Use Case: Filter videos by category
 */
class FilterVideosByCategoryUseCase {
    operator fun invoke(videos: List<Video>, category: String?): List<Video> {
        if (category.isNullOrBlank() || category == "All") {
            return videos
        }
        return videos.filter { it.category == category }
    }
}

/**
 * Use Case: Search videos
 */
class SearchVideosUseCase {
    operator fun invoke(videos: List<Video>, query: String): List<Video> {
        if (query.isBlank()) return videos

        val lowercaseQuery = query.lowercase()
        return videos.filter {
            it.title.lowercase().contains(lowercaseQuery) ||
                    it.description.lowercase().contains(lowercaseQuery) ||
                    it.category.lowercase().contains(lowercaseQuery)
        }
    }
}

/**
 * Use Case: Get unique categories
 */
class GetCategoriesUseCase {
    operator fun invoke(videos: List<Video>): List<String> {
        return listOf("All") + videos.map { it.category }.distinct().sorted()
    }
}
