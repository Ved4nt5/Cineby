package com.cineby.tv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cineby.tv.data.model.MediaItem
import com.cineby.tv.presentation.DetailsViewModel
import com.cineby.tv.ui.components.TvCard
import com.cineby.tv.util.ResultState

@Composable
fun DetailsScreen(
    mediaId: String,
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
    onPlay: (String?) -> Unit,
    onOpenDetails: (String) -> Unit
) {
    LaunchedEffect(mediaId) { viewModel.load(mediaId) }
    val state by viewModel.state.collectAsState()
    when (val current = state) {
        is ResultState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(24.dp))
        is ResultState.Error -> Text(current.message, modifier = Modifier.padding(24.dp))
        is ResultState.Success -> DetailsContent(
            item = current.data,
            viewModel = viewModel,
            onBack = onBack,
            onPlay = onPlay,
            onOpenDetails = onOpenDetails
        )
    }
}

@Composable
private fun DetailsContent(
    item: MediaItem,
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
    onPlay: (String?) -> Unit,
    onOpenDetails: (String) -> Unit
) {
    val isFavorite by viewModel.observeFavorite(item.id).collectAsState(initial = false)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onBack) { Text("Back") }
                Button(onClick = { onPlay(item.episodes.firstOrNull()?.id) }) { Text("Play") }
                Button(onClick = { viewModel.toggleFavorite(item, isFavorite) }) {
                    Text(if (isFavorite) "Remove Favorite" else "Add Favorite")
                }
            }
        }

        item {
            Text(item.title, style = MaterialTheme.typography.headlineLarge)
            Text("${item.releaseYear} • ${item.rating}")
            Text(item.genres.joinToString(" • "))
            Text(item.description, modifier = Modifier.padding(top = 8.dp))
        }

        if (item.episodes.isNotEmpty()) {
            item {
                Text("Episodes", style = MaterialTheme.typography.headlineSmall)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    item.episodes.forEach { episode ->
                        Button(onClick = { onPlay(episode.id) }) {
                            Text("S${episode.seasonNumber}E${episode.episodeNumber} • ${episode.title}")
                        }
                    }
                }
            }
        }

        item {
            Text("Related", style = MaterialTheme.typography.headlineSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(item.related, key = { it }) { relatedId ->
                    TvCard(
                        item = item.copy(id = relatedId, title = "Related $relatedId"),
                        onClick = { onOpenDetails(relatedId) }
                    )
                }
            }
        }
    }
}
