package com.cineby.tv.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CinebyDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF4E9BFF),
    onPrimary = Color.White,
    secondary = Color(0xFF7CC8FF),
    background = Color(0xFF0D0F14),
    surface = Color(0xFF171B24),
    onSurface = Color(0xFFE5E7EB),
    outline = Color(0xFF4B5563)
)

@Composable
fun CinebyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CinebyDarkColors,
        content = content
    )
}
