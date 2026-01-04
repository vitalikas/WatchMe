package lt.vitalijus.watchme.domain.usecase

import lt.vitalijus.watchme.domain.model.Video

/**
 * Use Case: Search videos
 */
class SearchVideosUseCase {

    operator fun invoke(
        videos: List<Video>,
        query: String
    ): List<Video> {
        if (query.isBlank()) return videos

        val lowercaseQuery = query.lowercase()
        return videos.filter {
            it.title.lowercase().contains(lowercaseQuery) ||
                    it.description.lowercase().contains(lowercaseQuery) ||
                    it.category.lowercase().contains(lowercaseQuery)
        }
    }
}
