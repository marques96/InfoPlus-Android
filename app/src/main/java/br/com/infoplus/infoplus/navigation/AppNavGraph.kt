package br.com.infoplus.infoplus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.infoplus.infoplus.features.auth.LoginScreen
import br.com.infoplus.infoplus.features.home.HomeScreen
import br.com.infoplus.infoplus.features.opening.OpeningScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.OPENING
    ) {
        composable(Routes.OPENING) { OpeningScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.HOME) { HomeScreen() }
    }
}
