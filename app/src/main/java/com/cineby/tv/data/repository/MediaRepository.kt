package com.cineby.tv.data.repository

import com.cineby.tv.data.local.CinebyDao
import com.cineby.tv.data.local.FavoriteEntity
import com.cineby.tv.data.local.WatchProgressEntity
import com.cineby.tv.data.model.ContentType
import com.cineby.tv.data.model.HomeFeed
import com.cineby.tv.data.model.MediaItem
import com.cineby.tv.data.model.SourceConfig
import com.cineby.tv.data.remote.CinebyApiFactory
import com.cineby.tv.data.remote.CinebyParser
import com.cineby.tv.data.remote.CinebyScraper
import com.cineby.tv.data.source.SourceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val sourceManager: SourceManager,
    private val apiFactory: CinebyApiFactory,
    private val scraper: CinebyScraper,
    private val dao: CinebyDao
) {
    val sourceConfig: Flow<SourceConfig> = sourceManager.sourceConfig
    val favorites = dao.observeFavorites()
    val continueWatching = dao.observeWatchProgress()

    suspend fun loadHomeFeed(): HomeFeed = withContext(Dispatchers.IO) {
        val continueWatchingItems = dao.observeWatchProgress().first().map { it.toMediaItem() }
        fetchFromSources { service ->
            service.getHome().body()?.string()?.let { raw ->
                CinebyParser.parseHome(raw, continueWatchingItems)
            }
        } ?: fallbackHome(continueWatchingItems)
    }

    suspend fun search(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        fetchFromSources { service ->
            service.search(query).body()?.string()?.let(CinebyParser::parseSearch)
        } ?: emptyList()
    }

    suspend fun details(id: String): MediaItem? = withContext(Dispatchers.IO) {
        fetchFromSources { service ->
            service.getContentDetails(id).body()?.string()?.let(CinebyParser::parseDetails)
        }
    }

    suspend fun setSource(url: String): Result<Unit> = sourceManager.setActiveSource(url)

    suspend fun setFallbacks(urls: List<String>) = sourceManager.setFallbackSources(urls)

    suspend fun testSource(url: String): Boolean = sourceManager.testConnection(url)

    fun isValidSource(url: String): Boolean = sourceManager.isValidSource(url)

    fun exportSourceConfig(config: SourceConfig): String = sourceManager.exportConfig(config)

    suspend fun importSourceConfig(json: String): Result<Unit> = sourceManager.importConfig(json)

    suspend fun saveProgress(item: MediaItem, positionMs: Long, durationMs: Long) {
        dao.upsertWatchProgress(
            WatchProgressEntity(
                mediaId = item.id,
                title = item.title,
                posterUrl = item.posterUrl,
                positionMs = positionMs,
                durationMs = durationMs,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearCache() {
        dao.clearWatchProgress()
    }

    suspend fun addFavorite(item: MediaItem) {
        dao.upsertFavorite(
            FavoriteEntity(
                mediaId = item.id,
                title = item.title,
                posterUrl = item.posterUrl,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFavorite(mediaId: String) {
        dao.removeFavorite(mediaId)
    }

    fun observeIsFavorite(mediaId: String): Flow<Boolean> = dao.observeIsFavorite(mediaId)

    private suspend fun <T> fetchFromSources(request: suspend (service: com.cineby.tv.data.remote.CinebyApiService) -> T?): T? {
        val config = sourceManager.sourceConfig.first()
        val urls = listOf(config.activeUrl) + config.fallbackUrls.filterNot { it == config.activeUrl }
        urls.forEach { url ->
            runCatching {
                apiFactory.create(url).let(request)
            }.getOrNull()?.let { return it }
        }
        return null
    }

    private fun fallbackHome(continueWatchingItems: List<MediaItem>): HomeFeed {
        val scraped = scraper.scrapeFallbackHome(SourceManager.DEFAULT_SOURCE)
        return HomeFeed(
            hero = scraped.take(5),
            continueWatching = continueWatchingItems,
            trendingMovies = scraped.filter { it.type == ContentType.MOVIE }.take(20),
            trendingShows = scraped.filter { it.type == ContentType.SHOW }.take(20),
            recentlyAdded = scraped.takeLast(20)
        )
    }
}

private fun WatchProgressEntity.toMediaItem(): MediaItem {
    return MediaItem(
        id = mediaId,
        title = title,
        description = "",
        posterUrl = posterUrl,
        backdropUrl = posterUrl,
        releaseYear = "",
        rating = 0.0,
        genres = emptyList(),
        type = ContentType.MOVIE,
        streamUrl = ""
    )
}
