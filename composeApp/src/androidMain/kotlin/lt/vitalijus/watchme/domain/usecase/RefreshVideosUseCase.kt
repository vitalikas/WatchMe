package lt.vitalijus.watchme.domain.usecase

import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.repository.VideoRepository

/**
 * Use Case: Force refresh videos from source
 *
 * Difference from GetVideosUseCase:
 * - GetVideos: Uses cache if valid (fast, efficient)
 * - RefreshVideos: Always fetches fresh data (slow, but guaranteed fresh)
 *
 * Use when:
 * - User explicitly pulls to refresh
 * - Need to ensure latest data
 * - Cache might be stale
 */
class RefreshVideosUseCase(
    private val repository: VideoRepository
) {

    /**
     * Force fetch fresh videos, bypassing cache
     *
     * Flow:
     * 1. Invalidate cache
     * 2. Fetch from remote
     * 3. Update cache with fresh data
     * 4. Return fresh data
     *
     * Fallback: If network fails, still returns cached data (better than error)
     */
    suspend operator fun invoke(): Result<List<Video>> {
        return repository.refreshVideos()
    }
}
