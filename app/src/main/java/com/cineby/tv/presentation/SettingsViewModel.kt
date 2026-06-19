package com.cineby.tv.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cineby.tv.data.model.SourceConfig
import com.cineby.tv.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {
    val sourceConfig: StateFlow<SourceConfig> = repository.sourceConfig.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SourceConfig(activeUrl = "https://cineby.at", fallbackUrls = emptyList())
    )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _subtitleEnabled = MutableStateFlow(true)
    val subtitleEnabled: StateFlow<Boolean> = _subtitleEnabled

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    fun saveSource(url: String) {
        viewModelScope.launch {
            val result = repository.setSource(url)
            _message.value = result.fold(
                onSuccess = { "Source saved" },
                onFailure = { it.message ?: "Invalid source" }
            )
        }
    }

    fun testSource(url: String) {
        viewModelScope.launch {
            _message.value = if (repository.testSource(url)) "Connection successful" else "Connection failed"
        }
    }

    fun saveFallbacks(raw: String) {
        viewModelScope.launch {
            val urls = raw.split(',').map { it.trim() }.filter { it.isNotBlank() }
            repository.setFallbacks(urls)
            _message.value = "Fallback sources updated"
        }
    }

    fun importConfig(json: String) {
        viewModelScope.launch {
            _message.value = repository.importSourceConfig(json).fold(
                onSuccess = { "Config imported" },
                onFailure = { "Import failed: ${it.message}" }
            )
        }
    }

    fun exportConfig() {
        val config = sourceConfig.value
        _message.value = repository.exportSourceConfig(config)
    }

    fun setSubtitleEnabled(enabled: Boolean) {
        _subtitleEnabled.value = enabled
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _message.value = "Cache cleared"
        }
    }
}
