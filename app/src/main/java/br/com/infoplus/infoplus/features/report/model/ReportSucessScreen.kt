package br.com.infoplus.infoplus.features.report

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.navigation.Routes

@Composable
fun ReportSuccessScreen(navController: NavHostController) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ocorrência registrada!",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Obrigado por registrar. Você ajudou a tornar o ambiente mais seguro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Voltar ao menu")
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        navController.navigate(Routes.REPORT) {
                            popUpTo(Routes.REPORT) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar outra ocorrência")
                }
            }
        }
    }
}
