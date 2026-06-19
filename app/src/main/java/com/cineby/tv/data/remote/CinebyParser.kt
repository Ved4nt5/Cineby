package com.cineby.tv.data.remote

import com.cineby.tv.data.model.ContentType
import com.cineby.tv.data.model.Episode
import com.cineby.tv.data.model.HomeFeed
import com.cineby.tv.data.model.MediaItem
import org.json.JSONArray
import org.json.JSONObject

object CinebyParser {
    fun parseHome(raw: String, continueWatching: List<MediaItem>): HomeFeed {
        val root = JSONObject(raw)
        return HomeFeed(
            hero = parseMediaArray(root.optJSONArray("hero")),
            continueWatching = continueWatching,
            trendingMovies = parseMediaArray(root.optJSONArray("trending_movies")),
            trendingShows = parseMediaArray(root.optJSONArray("trending_shows")),
            recentlyAdded = parseMediaArray(root.optJSONArray("recently_added"))
        )
    }

    fun parseSearch(raw: String): List<MediaItem> {
        val root = JSONObject(raw)
        return parseMediaArray(root.optJSONArray("results"))
    }

    fun parseDetails(raw: String): MediaItem {
        return parseMedia(JSONObject(raw))
    }

    private fun parseMediaArray(array: JSONArray?): List<MediaItem> {
        if (array == null) return emptyList()
        return buildList {
            repeat(array.length()) { index ->
                val item = array.optJSONObject(index) ?: return@repeat
                add(parseMedia(item))
            }
        }
    }

    private fun parseMedia(item: JSONObject): MediaItem {
        val type = when (item.optString("type").lowercase()) {
            "show", "series", "tv" -> ContentType.SHOW
            "anime" -> ContentType.ANIME
            else -> ContentType.MOVIE
        }
        val episodes = buildList {
            val arr = item.optJSONArray("episodes") ?: JSONArray()
            repeat(arr.length()) { index ->
                val e = arr.optJSONObject(index) ?: return@repeat
                add(
                    Episode(
                        id = e.optString("id", "${item.optString("id")}-$index"),
                        title = e.optString("title", "Episode ${index + 1}"),
                        seasonNumber = e.optInt("season", 1),
                        episodeNumber = e.optInt("number", index + 1),
                        streamUrl = e.optString("stream_url"),
                        introEndSeconds = e.optLong("intro_end").takeIf { it > 0 }
                    )
                )
            }
        }

        return MediaItem(
            id = item.optString("id"),
            title = item.optString("title"),
            description = item.optString("description"),
            posterUrl = item.optString("poster"),
            backdropUrl = item.optString("backdrop"),
            releaseYear = item.optString("year"),
            rating = item.optDouble("rating", 0.0),
            genres = parseStringArray(item.optJSONArray("genres")),
            type = type,
            streamUrl = item.optString("stream_url"),
            subtitles = parseStringArray(item.optJSONArray("subtitles")),
            related = parseStringArray(item.optJSONArray("related")),
            episodes = episodes
        )
    }

    private fun parseStringArray(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        return buildList {
            repeat(array.length()) { index ->
                add(array.optString(index))
            }
        }
    }
}
