package com.cineby.tv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cineby.tv.presentation.DetailsViewModel
import com.cineby.tv.presentation.HomeViewModel
import com.cineby.tv.presentation.PlayerViewModel
import com.cineby.tv.presentation.SearchViewModel
import com.cineby.tv.presentation.SettingsViewModel
import com.cineby.tv.ui.player.PlayerScreen
import com.cineby.tv.ui.screens.DetailsScreen
import com.cineby.tv.ui.screens.HomeScreen
import com.cineby.tv.ui.screens.SearchScreen
import com.cineby.tv.ui.screens.SettingsScreen

@Composable
fun CinebyNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) {
            val vm: HomeViewModel = hiltViewModel()
            HomeScreen(
                state = vm.state,
                onRefresh = vm::load,
                onOpenDetails = { navController.navigate(Routes.Details.create(it)) },
                onOpenSearch = { navController.navigate(Routes.Search.route) },
                onOpenSettings = { navController.navigate(Routes.Settings.route) }
            )
        }

        composable(
            route = Routes.Details.route,
            arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId").orEmpty()
            val vm: DetailsViewModel = hiltViewModel()
            DetailsScreen(
                mediaId = mediaId,
                viewModel = vm,
                onBack = navController::popBackStack,
                onPlay = { episodeId -> navController.navigate(Routes.Player.create(mediaId, episodeId ?: "none")) },
                onOpenDetails = { navController.navigate(Routes.Details.create(it)) }
            )
        }

        composable(Routes.Search.route) {
            val vm: SearchViewModel = hiltViewModel()
            SearchScreen(
                viewModel = vm,
                onBack = navController::popBackStack,
                onOpenDetails = { navController.navigate(Routes.Details.create(it)) }
            )
        }

        composable(Routes.Settings.route) {
            val vm: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = vm,
                onBack = navController::popBackStack
            )
        }

        composable(
            route = Routes.Player.route,
            arguments = listOf(
                navArgument("mediaId") { type = NavType.StringType },
                navArgument("episodeId") { type = NavType.StringType }
            )
        ) { backStack ->
            val mediaId = backStack.arguments?.getString("mediaId").orEmpty()
            val episodeId = backStack.arguments?.getString("episodeId")
            val vm: PlayerViewModel = hiltViewModel()
            PlayerScreen(
                mediaId = mediaId,
                episodeId = episodeId,
                viewModel = vm,
                onBack = navController::popBackStack
            )
        }
    }
}
