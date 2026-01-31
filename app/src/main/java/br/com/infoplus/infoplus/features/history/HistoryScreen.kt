package br.com.infoplus.infoplus.features.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import br.com.infoplus.infoplus.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, vm: HistoryViewModel = hiltViewModel()) {
    val items = vm.history.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Histórico de ocorrências") })
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Ainda não há ocorrências registradas.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { r ->
                ElevatedCard (
                    onClick = { navController.navigate("${Routes.HISTORY_DETAIL}/${r.id}") }
                ){
                    Column(Modifier.padding(12.dp)) {
                        Text(r.draft.title.ifBlank { "Ocorrência" }, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = r.draft.category?.name ?: "Sem categoria",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val genderLabel = when {
                            r.draft.victimType == br.com.infoplus.infoplus.features.report.model.VictimType.OTHER ->
                                "Vítima: ${r.draft.victimGender.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }}"
                            r.draft.victimType == br.com.infoplus.infoplus.features.report.model.VictimType.SELF ->
                                "Vítima: Você"
                            else -> "Vítima: Não informado"
                        }

                        Text(
                            text = genderLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(8.dp))

                        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                            .format(Date(r.createdAtMillis))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(date, style = MaterialTheme.typography.bodySmall)
                            val label = if (r.status == ReportStatus.SYNCED) "ENVIADO" else "PENDENTE"
                            AssistChip(onClick = {}, label = { Text(label) })
                        }
                    }
                }
            }
        }
    }
}