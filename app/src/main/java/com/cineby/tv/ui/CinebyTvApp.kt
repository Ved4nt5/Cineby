package com.cineby.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.cineby.tv.ui.navigation.CinebyNavHost

@Composable
fun CinebyTvApp() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0F14), Color(0xFF111827))
                )
            )
    ) {
        CinebyNavHost()
    }
}
