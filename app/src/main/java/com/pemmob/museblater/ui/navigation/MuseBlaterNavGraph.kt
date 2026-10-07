package com.pemmob.museblater.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemmob.museblater.ui.detail.DetailScreen
import com.pemmob.museblater.ui.home.HomeScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Detail : Screen("detail/{malId}") {
        fun createRoute(malId: Int) = "detail/$malId"
    }
}

@Composable
fun MuseBlaterNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAnimeClick = { malId ->
                    navController.navigate(Screen.Detail.createRoute(malId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("malId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val malId = backStackEntry.arguments?.getInt("malId") ?: return@composable
            DetailScreen(
                malId = malId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
