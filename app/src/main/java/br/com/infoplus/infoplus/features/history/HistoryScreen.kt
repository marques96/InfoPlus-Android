package br.com.infoplus.infoplus.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import br.com.infoplus.infoplus.core.ui.components.InfoEmptyState
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import br.com.infoplus.infoplus.features.report.model.OccurrenceRecord
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
                title = { Text("Seu Histórico", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.REPORT) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Relatar", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
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

            if (filtered.isEmpty()) {
                InfoEmptyState(
                    title = if (items.isEmpty()) "Nada por aqui ainda" else "Sem resultados",
                    subtitle = if (items.isEmpty())
                        "Seus relatos ajudam a tornar a cidade mais segura para todos."
                    else
                        "Tente mudar o filtro acima.",
                    primaryActionText = "Registrar primeiro relato",
                    onPrimaryAction = { navController.navigate(Routes.REPORT) },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(filtered) { r ->
                        HistoryItemCard(
                            record = r,
                            onClick = { navController.navigate("${Routes.HISTORY_DETAIL}/${r.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    record: OccurrenceRecord,
    onClick: () -> Unit
) {
    val dateText = SimpleDateFormat("dd MMM, HH:mm", Locale("pt", "BR"))
        .format(Date(record.createdAtMillis))

    val isSynced = record.status == ReportStatus.SYNCED

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSynced) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = record.draft.category?.toIcon() ?: Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = if (isSynced) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.draft.title.ifBlank { "Relato sem título" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("•", color = MaterialTheme.colorScheme.outline)
                    Icon(
                        imageVector = if (isSynced) Icons.Default.CheckCircle else Icons.Default.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (isSynced) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Text(
                        text = if (isSynced) "Enviado" else "Pendente",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSynced) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun OccurrenceCategory.toIcon(): ImageVector = when(this) {
    OccurrenceCategory.ASSALTO -> Icons.Default.Warning
    OccurrenceCategory.ASSEDIO -> Icons.Default.RecordVoiceOver
    OccurrenceCategory.VIOLENCIA -> Icons.Default.Gavel
    OccurrenceCategory.DESAPARECIMENTO -> Icons.Default.Search
    OccurrenceCategory.EMERGENCIA_MEDICA -> Icons.Default.MedicalServices
    OccurrenceCategory.OUTROS -> Icons.Default.MoreHoriz
}

private enum class HistoryFilter { ALL, PENDING, SENT }
