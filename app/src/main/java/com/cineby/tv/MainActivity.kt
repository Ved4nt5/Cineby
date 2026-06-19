package com.cineby.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cineby.tv.ui.CinebyTvApp
import com.cineby.tv.ui.theme.CinebyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CinebyTheme {
                CinebyTvApp()
            }
        }
    }
}

@Preview
@Composable
private fun PreviewApp() {
    CinebyTheme {
        CinebyTvApp()
    }
}
