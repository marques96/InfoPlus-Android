package br.com.infoplus.infoplus.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.infoplus.infoplus.features.auth.LoginScreen
import br.com.infoplus.infoplus.features.auth.RegisterScreen
import br.com.infoplus.infoplus.features.history.HistoryDetailScreen
import br.com.infoplus.infoplus.features.history.HistoryScreen
import br.com.infoplus.infoplus.features.home.HomeScreen
import br.com.infoplus.infoplus.features.map.CasesMapScreen
import br.com.infoplus.infoplus.features.opening.OpeningScreen
import br.com.infoplus.infoplus.features.opportunities.OpportunitiesScreen
import br.com.infoplus.infoplus.features.report.ReportScreen
import br.com.infoplus.infoplus.features.report.ReportSuccessScreen
import br.com.infoplus.infoplus.features.support.SupportScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.OPENING
    ) {
        composable(Routes.OPENING) { OpeningScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { RegisterScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) }

        composable(Routes.REPORT) { ReportScreen(navController) }

        // ✅ ÚNICA rota de success (com offline param)
        composable(
            route = "${Routes.REPORT_SUCCESS}?offline={offline}",
            arguments = listOf(
                navArgument("offline") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { entry ->
            val offline = entry.arguments?.getBoolean("offline") ?: false
            ReportSuccessScreen(navController = navController, offline = offline)
        }

        composable(Routes.HISTORY) { HistoryScreen(navController) }
        composable("${Routes.HISTORY_DETAIL}/{id}") { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            HistoryDetailScreen(navController, id)
        }

        composable(Routes.MAP) { CasesMapScreen(navController) }
        composable(Routes.SUPPORT) { SupportScreen(navController) }
        composable(Routes.OPPORTUNITIES) { OpportunitiesScreen(navController) }

        composable(Routes.MEMORIAL) { PlaceholderScreen("Memorial digital") }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
