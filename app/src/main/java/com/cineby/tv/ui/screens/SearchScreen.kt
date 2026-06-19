package com.cineby.tv.ui.screens

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cineby.tv.presentation.SearchViewModel
import com.cineby.tv.ui.components.TvCard
import com.cineby.tv.util.ResultState

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBack: () -> Unit,
    onOpenDetails: (String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val results by viewModel.results.collectAsState()

    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
        viewModel.updateQuery(spoken)
        viewModel.search(spoken)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onBack) { Text("Back") }
                Button(onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Search Cineby")
                    }
                    voiceLauncher.launch(intent)
                }) { Text("Voice Search") }
            }
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = query,
                onValueChange = viewModel::updateQuery,
                label = { Text("Search movies, shows, anime") }
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.search() }) { Text("Search") }
            }
        }

        if (suggestions.isNotEmpty()) {
            item {
                Text("Suggestions")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(suggestions) { suggestion ->
                        Button(onClick = {
                            viewModel.updateQuery(suggestion)
                            viewModel.search(suggestion)
                        }) { Text(suggestion) }
                    }
                }
            }
        }

        when (val current = results) {
            is ResultState.Loading -> item { Text("Searching...") }
            is ResultState.Error -> item { Text(current.message) }
            is ResultState.Success -> {
                items(current.data, key = { it.id }) { item ->
                    TvCard(item = item, onClick = { onOpenDetails(it.id) }, modifier = Modifier.padding(bottom = 12.dp))
                }
            }
        }
    }
}
