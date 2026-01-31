package br.com.infoplus.infoplus.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.features.report.model.Gender

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    vm: RegisterViewModel = hiltViewModel()
) {
    val state by vm.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cadastro") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Informe seus dados para personalizar o INFO+ e registrar o gênero corretamente nas ocorrências.")

            OutlinedTextField(
                value = state.name,
                onValueChange = vm::setName,
                label = { Text("Seu nome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Gênero (obrigatório)")
            Gender.values().forEach { g ->
                if (g == Gender.NAO_INFORMADO) return@forEach
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = state.gender == g,
                        onClick = { vm.setGender(g) }
                    )
                    Text(g.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() })
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = { vm.save { navController.popBackStack() } },
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Salvando…")
                } else {
                    Text("Salvar cadastro")
                }
            }
        }
    }
}
