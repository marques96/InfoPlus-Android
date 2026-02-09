package br.com.infoplus.infoplus.features.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import br.com.infoplus.infoplus.core.ui.components.InfoEmptyState
import br.com.infoplus.infoplus.core.ui.components.ScreenContainer
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import br.com.infoplus.infoplus.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    vm: HistoryViewModel = hiltViewModel()
) {
    val items = vm.history.collectAsState().value
    var filter by remember { mutableStateOf(HistoryFilter.ALL) }

    val filtered = remember(items, filter) {
        when (filter) {
            HistoryFilter.ALL -> items
            HistoryFilter.PENDING -> items.filter { it.status != ReportStatus.SYNCED }
            HistoryFilter.SENT -> items.filter { it.status == ReportStatus.SYNCED }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.REPORT) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        ScreenContainer(padding = padding) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = filter == HistoryFilter.ALL,
                        onClick = { filter = HistoryFilter.ALL },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) { Text("Todos") }

                    SegmentedButton(
                        selected = filter == HistoryFilter.PENDING,
                        onClick = { filter = HistoryFilter.PENDING },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) { Text("Pendentes") }

                    SegmentedButton(
                        selected = filter == HistoryFilter.SENT,
                        onClick = { filter = HistoryFilter.SENT },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) { Text("Enviados") }
                }

                Spacer(Modifier.height(12.dp))

                if (filtered.isEmpty()) {
                    InfoEmptyState(
                        title = if (items.isEmpty()) "Nenhuma ocorrência ainda" else "Nada neste filtro",
                        subtitle = if (items.isEmpty())
                            "Registre uma ocorrência para construir histórico e apoiar dados para políticas públicas."
                        else
                            "Selecione outro filtro para visualizar seus registros.",
                        primaryActionText = "Registrar ocorrência",
                        onPrimaryAction = { navController.navigate(Routes.REPORT) },
                        modifier = Modifier.fillMaxSize()
                    )
                    return@ScreenContainer
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(filtered) { r ->
                        val dateText = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale("pt", "BR"))
                            .format(Date(r.createdAtMillis))

                        val statusLabel = if (r.status == ReportStatus.SYNCED) "ENVIADO" else "PENDENTE"

                        ElevatedCard(
                            onClick = { navController.navigate("${Routes.HISTORY_DETAIL}/${r.id}") },
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    r.draft.title.ifBlank { "Ocorrência" },
                                    style = MaterialTheme.typography.titleMedium
                                )

                                val categoryText = r.draft.category?.name ?: "Sem categoria"

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AssistChip(onClick = {}, label = { Text(categoryText) })
                                    AssistChip(onClick = {}, label = { Text(statusLabel) })
                                }

                                val victimLabel = when {
                                    r.draft.victimType == br.com.infoplus.infoplus.features.report.model.VictimType.OTHER ->
                                        "Vítima: ${
                                            r.draft.victimGender.name
                                                .replace("_", " ")
                                                .lowercase()
                                                .replaceFirstChar { it.titlecase() }
                                        }"
                                    r.draft.victimType == br.com.infoplus.infoplus.features.report.model.VictimType.SELF ->
                                        "Vítima: Você"
                                    else -> "Vítima: Não informado"
                                }

                                Text(
                                    text = victimLabel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = dateText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class HistoryFilter { ALL, PENDING, SENT }
