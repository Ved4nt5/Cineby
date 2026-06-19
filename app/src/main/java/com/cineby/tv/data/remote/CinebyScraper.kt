package com.cineby.tv.data.remote

import com.cineby.tv.data.model.ContentType
import com.cineby.tv.data.model.MediaItem
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CinebyScraper @Inject constructor() {
    fun scrapeFallbackHome(baseUrl: String): List<MediaItem> {
        return runCatching {
            val document = Jsoup.connect(baseUrl).get()
            document.select("article, .item, .card").mapIndexed { index, element ->
                MediaItem(
                    id = element.attr("data-id").ifBlank { "fallback-$index" },
                    title = element.select("h2,h3,.title").text().ifBlank { "Cineby item $index" },
                    description = element.select("p,.description").text(),
                    posterUrl = element.select("img").attr("abs:src"),
                    backdropUrl = element.select("img").attr("abs:src"),
                    releaseYear = "",
                    rating = 0.0,
                    genres = emptyList(),
                    type = ContentType.MOVIE,
                    streamUrl = element.select("a").attr("abs:href")
                )
            }
        }.getOrDefault(emptyList())
    }
}
