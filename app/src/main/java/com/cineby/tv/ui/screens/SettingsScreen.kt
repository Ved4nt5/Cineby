package com.cineby.tv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cineby.tv.presentation.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val sourceConfig by viewModel.sourceConfig.collectAsState()
    val message by viewModel.message.collectAsState()
    val subtitleEnabled by viewModel.subtitleEnabled.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()

    var sourceInput by remember { mutableStateOf(sourceConfig.activeUrl) }
    var fallbackInput by remember { mutableStateOf(sourceConfig.fallbackUrls.joinToString(",")) }
    var importInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Button(onClick = onBack) { Text("Back") }
        }

        item {
            Text("Source URL Manager")
            OutlinedTextField(
                value = sourceInput,
                onValueChange = { sourceInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Base URL") }
            )
            Button(onClick = { viewModel.saveSource(sourceInput) }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Save URL")
            }
            Button(onClick = { viewModel.testSource(sourceInput) }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Test Connection")
            }

            OutlinedTextField(
                value = fallbackInput,
                onValueChange = { fallbackInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                label = { Text("Fallback URLs (comma-separated)") }
            )
            Button(onClick = { viewModel.saveFallbacks(fallbackInput) }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Save Fallbacks")
            }
        }

        item {
            Text("Import/Export Source Configuration")
            Button(onClick = viewModel::exportConfig) { Text("Export Config") }
            OutlinedTextField(
                value = importInput,
                onValueChange = { importInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                label = { Text("Paste config JSON") }
            )
            Button(onClick = { viewModel.importConfig(importInput) }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Import Config")
            }
        }

        item {
            Text("Subtitle Preferences")
            Switch(checked = subtitleEnabled, onCheckedChange = viewModel::setSubtitleEnabled)
        }

        item {
            Text("Playback Preferences")
            Slider(
                value = playbackSpeed,
                onValueChange = viewModel::setPlaybackSpeed,
                valueRange = 0.5f..2.0f,
                steps = 5
            )
            Text("Speed: ${"%.2f".format(playbackSpeed)}x")
        }

        item {
            Text("Cache Management")
            Button(onClick = viewModel::clearCache) { Text("Clear Cache") }
        }

        item {
            Text("About")
            Text("Cineby TV - Android TV OTT experience with dynamic source switching and playback controls.")
        }

        if (!message.isNullOrBlank()) {
            item {
                Text(
                    text = message.orEmpty(),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
