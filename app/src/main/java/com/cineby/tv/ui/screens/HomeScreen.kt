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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cineby.tv.data.model.HomeFeed
import com.cineby.tv.data.model.MediaItem
import com.cineby.tv.ui.components.TvCard
import com.cineby.tv.util.ResultState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    state: StateFlow<ResultState<HomeFeed>>,
    onRefresh: () -> Unit,
    onOpenDetails: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState by state.collectAsState()
    when (val current = uiState) {
        is ResultState.Loading -> LoadingView()
        is ResultState.Error -> ErrorView(current.message, onRefresh)
        is ResultState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onOpenSearch) { Text("Search") }
                        Button(onClick = onOpenSettings) { Text("Settings") }
                    }
                }
                item { HeroCarousel(current.data.hero, onOpenDetails) }
                item { ContentRow("Continue Watching", current.data.continueWatching, onOpenDetails) }
                item { ContentRow("Trending Movies", current.data.trendingMovies, onOpenDetails) }
                item { ContentRow("Trending TV Shows", current.data.trendingShows, onOpenDetails) }
                item { ContentRow("Recently Added", current.data.recentlyAdded, onOpenDetails) }
            }
        }
    }
}

@Composable
private fun HeroCarousel(items: List<MediaItem>, onOpenDetails: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Featured", style = MaterialTheme.typography.headlineSmall)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(items.take(8)) { item ->
                TvCard(item = item, onClick = { onOpenDetails(it.id) })
            }
        }
    }
}

@Composable
private fun ContentRow(title: String, items: List<MediaItem>, onOpenDetails: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(items, key = { it.id }) { item ->
                TvCard(item = item, onClick = { onOpenDetails(it.id) })
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text("Loading Cineby catalog...", modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun ErrorView(message: String, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = message)
        Button(onClick = onRefresh) {
            Text("Retry")
        }
    }
}
