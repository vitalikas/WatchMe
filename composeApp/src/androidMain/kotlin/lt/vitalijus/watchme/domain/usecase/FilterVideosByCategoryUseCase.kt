package lt.vitalijus.watchme.domain.usecase

import lt.vitalijus.watchme.domain.model.Video

/**
 * Use Case: Filter videos by category
 */
class FilterVideosByCategoryUseCase {

    operator fun invoke(
        videos: List<Video>,
        category: String?
    ): List<Video> {
        if (category.isNullOrBlank() || category == "All") {
            return videos
        }
        return videos.filter { it.category == category }
    }
}
