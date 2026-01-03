package lt.vitalijus.watchme.domain.repository

import kotlinx.coroutines.flow.Flow
import lt.vitalijus.watchme.domain.model.Video

/**
 * Repository interface (Dependency Inversion Principle)
 * Domain layer depends on this interface, not implementation
 */
interface VideoRepository {

    suspend fun getVideos(): Result<List<Video>>
    suspend fun getVideoById(id: String): Result<Video?>
    fun observeVideos(): Flow<List<Video>>
}
