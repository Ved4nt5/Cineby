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
class SearchViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _results = MutableStateFlow<ResultState<List<MediaItem>>>(ResultState.Success(emptyList()))
    val results: StateFlow<ResultState<List<MediaItem>>> = _results.asStateFlow()

    fun updateQuery(value: String) {
        _query.value = value
        _suggestions.value = listOf(value, "$value movie", "$value show", "$value anime").filter { it.isNotBlank() }
    }

    fun search(value: String = _query.value) {
        viewModelScope.launch {
            _results.value = ResultState.Loading
            _results.value = runCatching { repository.search(value) }
                .fold(
                    onSuccess = { ResultState.Success(it) },
                    onFailure = { ResultState.Error("Search failed", it) }
                )
        }
    }
}
