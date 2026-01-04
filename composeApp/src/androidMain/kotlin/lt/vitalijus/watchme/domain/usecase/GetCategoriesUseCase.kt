package lt.vitalijus.watchme.domain.usecase

import lt.vitalijus.watchme.domain.model.Video

/**
 * Use Case: Get unique categories
 */
class GetCategoriesUseCase {

    operator fun invoke(videos: List<Video>): List<String> {
        return listOf("All") + videos.map { it.category }.distinct().sorted()
    }
}
