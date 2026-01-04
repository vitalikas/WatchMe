package lt.vitalijus.watchme.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.domain.repository.VideoRepository
import lt.vitalijus.watchme.util.runCatchingCancellable

/**
 * Repository implementation with caching
 * Single Source of Truth pattern
 */
class VideoRepositoryImpl(
    private val remoteDataSource: VideoRemoteDataSource,
    private val cache: VideoLocalDataSource
) : VideoRepository {

    override suspend fun getVideos(): Result<List<Video>> {
        // Check cache first
        val cached = cache.getVideos()
        if (cached.isNotEmpty() && !cache.isExpired()) {
            return Result.success(cached)
        }

        // Cache invalid/expired, fetch fresh
        return fetchAndCache()
    }

    override suspend fun refreshVideos(): Result<List<Video>> {
        // Force refresh: invalidate cache and fetch fresh data
        cache.invalidate()
        return fetchAndCache()
    }

    /**
     * Private helper to fetch from remote and update cache
     * Uses runCatchingCancellable to properly handle coroutine cancellation
     */
    private suspend fun fetchAndCache(): Result<List<Video>> {
        return runCatchingCancellable {
            val videos = remoteDataSource.fetchVideos()
            cache.saveVideos(videos = videos)
            videos
        }
            .recoverCatching { error ->
                // Attempt to recover using the cache
                val fallbackCache = cache.getVideos()
                if (fallbackCache.isEmpty()) {
                    throw error
                }
                fallbackCache
            }
    }

    override suspend fun getVideoById(id: String): Result<Video?> {
        return try {
            val video = cache.getVideoById(id)
                ?: remoteDataSource.fetchVideoById(id)

            video?.let { cache.saveVideo(it) }

            Result.success(video)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeVideos(): Flow<List<Video>> = flow {
        // Emit cached data immediately
        val cached = cache.getVideos()
        if (cached.isNotEmpty()) {
            emit(cached)
        }

        // Fetch fresh data
        try {
            val fresh = remoteDataSource.fetchVideos()
            cache.saveVideos(fresh)
            emit(fresh)
        } catch (e: Exception) {
            // Already emitted cache, just log error
            println("Failed to fetch fresh videos: ${e.message}")
        }
    }
}
