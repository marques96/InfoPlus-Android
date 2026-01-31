package br.com.infoplus.infoplus.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.infoplus.infoplus.features.auth.LoginScreen
import br.com.infoplus.infoplus.features.home.HomeScreen
import br.com.infoplus.infoplus.features.opening.OpeningScreen
import br.com.infoplus.infoplus.features.report.ReportScreen
import br.com.infoplus.infoplus.features.report.ReportSuccessScreen
import br.com.infoplus.infoplus.features.history.HistoryScreen
import br.com.infoplus.infoplus.features.history.HistoryDetailScreen


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.OPENING
    ) {
        composable(Routes.OPENING) { OpeningScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.REPORT) { ReportScreen(navController) }
        composable(Routes.REPORT_SUCCESS) { ReportSuccessScreen(navController) }
        composable(Routes.HISTORY) { HistoryScreen(navController) }
        composable("${Routes.HISTORY_DETAIL}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            HistoryDetailScreen(navController, id)
        }

        composable(Routes.MAP) { PlaceholderScreen("Mapa de casos") }
        composable(Routes.MEMORIAL) { PlaceholderScreen("Memorial digital") }
        composable(Routes.SUPPORT) { PlaceholderScreen("Recursos de apoio") }
        composable(Routes.OPPORTUNITIES) { PlaceholderScreen("Editais e vagas") }

    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
