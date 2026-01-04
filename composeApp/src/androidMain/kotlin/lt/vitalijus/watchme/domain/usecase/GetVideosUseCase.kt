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

    operator fun invoke(): Flow<List<Video>> {
        return repository.observeVideos()
    }
}
