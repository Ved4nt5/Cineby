package com.cineby.tv.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.cineby.tv.presentation.PlayerViewModel

@Composable
fun PlayerScreen(
    mediaId: String,
    episodeId: String?,
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val media by viewModel.media.collectAsState()

    var speed by remember { mutableStateOf(1.0f) }
    var skipIntroSecond by remember { mutableStateOf<Long?>(null) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .build().apply {
                playWhenReady = true
            }
    }

    LaunchedEffect(mediaId) {
        viewModel.load(mediaId)
    }

    LaunchedEffect(media) {
        val content = media ?: return@LaunchedEffect
        val episode = content.episodes.firstOrNull { it.id == episodeId } ?: content.episodes.firstOrNull()
        val streamUrl = episode?.streamUrl?.ifBlank { content.streamUrl } ?: content.streamUrl
        if (streamUrl.isNotBlank()) {
            exoPlayer.setMediaItem(MediaItem.fromUri(streamUrl))
            exoPlayer.prepare()
        }
        skipIntroSecond = episode?.introEndSeconds
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveProgress(exoPlayer.currentPosition, exoPlayer.duration.coerceAtLeast(0L))
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onBack) { Text("Back") }
            Button(onClick = {
                speed = if (speed >= 2f) 0.5f else speed + 0.25f
                exoPlayer.playbackParameters = PlaybackParameters(speed)
            }) { Text("Speed ${"%.2f".format(speed)}x") }
            Button(onClick = {
                val introEnd = skipIntroSecond ?: 0L
                exoPlayer.seekTo(introEnd * 1000)
            }) { Text("Skip Intro") }
            Button(onClick = {
                val next = viewModel.nextEpisode(episodeId)
                if (next != null) {
                    viewModel.load(mediaId)
                }
            }) { Text("Next Episode") }
            Button(onClick = {
                val trackSelection = exoPlayer.trackSelectionParameters
                exoPlayer.trackSelectionParameters = trackSelection.buildUpon().setPreferredAudioLanguage("en").build()
            }) { Text("Audio EN") }
            Button(onClick = {
                val trackSelection = exoPlayer.trackSelectionParameters
                exoPlayer.trackSelectionParameters = trackSelection.buildUpon().setPreferredTextLanguage("en").build()
            }) { Text("Sub EN") }
        }
    }
}
