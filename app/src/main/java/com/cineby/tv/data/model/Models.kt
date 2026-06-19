package com.cineby.tv.data.model

enum class ContentType {
    MOVIE,
    SHOW,
    ANIME
}

data class Episode(
    val id: String,
    val title: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val streamUrl: String,
    val introEndSeconds: Long? = null
)

data class MediaItem(
    val id: String,
    val title: String,
    val description: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseYear: String,
    val rating: Double,
    val genres: List<String>,
    val type: ContentType,
    val streamUrl: String,
    val subtitles: List<String> = emptyList(),
    val related: List<String> = emptyList(),
    val episodes: List<Episode> = emptyList()
)

data class HomeFeed(
    val hero: List<MediaItem>,
    val continueWatching: List<MediaItem>,
    val trendingMovies: List<MediaItem>,
    val trendingShows: List<MediaItem>,
    val recentlyAdded: List<MediaItem>
)

data class SourceConfig(
    val activeUrl: String,
    val fallbackUrls: List<String>
)
