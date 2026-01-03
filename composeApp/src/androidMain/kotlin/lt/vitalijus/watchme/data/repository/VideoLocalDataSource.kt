package lt.vitalijus.watchme.data.repository

import lt.vitalijus.watchme.domain.model.Video

/**
 * Defines the contract for the local data source (cache).
 */
interface VideoLocalDataSource {

    fun getVideos(): List<Video>
    fun getVideoById(id: String): Video?
    fun saveVideos(videos: List<Video>)
    fun saveVideo(video: Video)
    fun isExpired(): Boolean
}
