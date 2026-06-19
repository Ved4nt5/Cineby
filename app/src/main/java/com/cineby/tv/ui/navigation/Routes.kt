package com.cineby.tv.ui.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Details : Routes("details/{mediaId}") {
        fun create(mediaId: String) = "details/$mediaId"
    }
    data object Search : Routes("search")
    data object Settings : Routes("settings")
    data object Player : Routes("player/{mediaId}/{episodeId}") {
        fun create(mediaId: String, episodeId: String = "none") = "player/$mediaId/$episodeId"
    }
}
