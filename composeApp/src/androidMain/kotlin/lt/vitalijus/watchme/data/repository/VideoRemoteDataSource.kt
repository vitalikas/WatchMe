package lt.vitalijus.watchme.data.repository

import lt.vitalijus.watchme.domain.model.Video

/**
 * Defines the contract for the remote data source (network).
 */
interface VideoRemoteDataSource {

    suspend fun fetchVideos(): List<Video>
    suspend fun fetchVideoById(id: String): Video?
}
