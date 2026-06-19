package com.cineby.tv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cineby.tv.data.model.HomeFeed
import com.cineby.tv.data.repository.MediaRepository
import com.cineby.tv.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ResultState<HomeFeed>>(ResultState.Loading)
    val state: StateFlow<ResultState<HomeFeed>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = ResultState.Loading
            _state.value = runCatching { repository.loadHomeFeed() }
                .fold(
                    onSuccess = { ResultState.Success(it) },
                    onFailure = { ResultState.Error("Unable to load home feed", it) }
                )
        }
    }
}
