package com.cineby.tv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cineby.tv.data.model.MediaItem
import com.cineby.tv.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {
    private val _media = MutableStateFlow<MediaItem?>(null)
    val media: StateFlow<MediaItem?> = _media.asStateFlow()

    fun load(mediaId: String) {
        viewModelScope.launch {
            _media.value = repository.details(mediaId)
        }
    }

    fun saveProgress(positionMs: Long, durationMs: Long) {
        val item = _media.value ?: return
        viewModelScope.launch {
            repository.saveProgress(item, positionMs, durationMs)
        }
    }

    fun nextEpisode(currentEpisodeId: String?): String? {
        val episodes = _media.value?.episodes.orEmpty()
        if (episodes.isEmpty() || currentEpisodeId == null || currentEpisodeId == "none") return null
        val index = episodes.indexOfFirst { it.id == currentEpisodeId }
        return episodes.getOrNull(index + 1)?.id
    }
}
