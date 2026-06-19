package com.cineby.tv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cineby.tv.data.model.MediaItem
import com.cineby.tv.data.repository.MediaRepository
import com.cineby.tv.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ResultState<MediaItem>>(ResultState.Loading)
    val state: StateFlow<ResultState<MediaItem>> = _state.asStateFlow()

    fun load(id: String) {
        viewModelScope.launch {
            _state.value = ResultState.Loading
            _state.value = runCatching { repository.details(id) ?: error("Not found") }
                .fold(
                    onSuccess = { ResultState.Success(it) },
                    onFailure = { ResultState.Error("Unable to load details", it) }
                )
        }
    }

    fun toggleFavorite(item: MediaItem, favorite: Boolean) {
        viewModelScope.launch {
            if (favorite) repository.removeFavorite(item.id) else repository.addFavorite(item)
        }
    }

    fun observeFavorite(id: String) = repository.observeIsFavorite(id)
}
