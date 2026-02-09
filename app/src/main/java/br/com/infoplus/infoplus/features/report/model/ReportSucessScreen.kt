package br.com.infoplus.infoplus.features.report

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSuccessScreen(
    navController: NavHostController,
    offline: Boolean
) {
    val context = LocalContext.current
    var showCallOptions by remember { mutableStateOf(false) }

    fun dial(number: String) {
        context.startActivity(
            Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$number") }
        )
    }

    val subtitle = if (offline) {
        "Sem conexão: o registro foi salvo localmente e será sincronizado automaticamente quando a internet voltar."
    } else {
        "O registro foi sincronizado com segurança. Obrigado por contribuir com dados para políticas públicas."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Concluído") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Sua ocorrência foi registrada com sucesso",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "O que você deseja fazer agora?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    // ✅ Ligar (expande opções)
                    Button(
                        onClick = { showCallOptions = !showCallOptions },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Ligar")
                    }

                    AnimatedVisibility(visible = showCallOptions) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { dial("100") },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Direitos Humanos (Disque 100)") }

                            OutlinedButton(
                                onClick = { dial("192") },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("SAMU 192") }

                            OutlinedButton(
                                onClick = { dial("190") },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Polícia 190") }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ✅ Recursos de Apoio (vai para a tela SUPPORT)
                    OutlinedButton(
                        onClick = { navController.navigate(Routes.SUPPORT) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Recursos de apoio")
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.REPORT) {
                        popUpTo(Routes.REPORT) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrar outra ocorrência") }

            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Voltar ao menu") }
        }
    }
}
