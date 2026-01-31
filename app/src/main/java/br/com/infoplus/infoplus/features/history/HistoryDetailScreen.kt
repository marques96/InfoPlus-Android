package br.com.infoplus.infoplus.features.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import br.com.infoplus.infoplus.features.report.model.Attachment
import br.com.infoplus.infoplus.features.report.model.ReportStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    navController: NavController,
    id: String,
    vm: HistoryDetailViewModel = hiltViewModel()
) {
    val ui = vm.ui.collectAsState().value

    // Carrega o item ao abrir a tela
    // (Se você preferir, dá pra fazer isso no init do VM com SavedStateHandle)
    vm.load(id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da ocorrência") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (ui.status == ReportStatus.QUEUED) {
                        IconButton(onClick = { vm.trySyncOne(id) }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Tentar enviar agora")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (ui.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (ui.errorMessage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Não foi possível carregar o detalhe.", style = MaterialTheme.typography.titleMedium)
                Text(ui.errorMessage, color = MaterialTheme.colorScheme.error)
                Button(onClick = { vm.load(id) }) {
                    Text("Tentar novamente")
                }
            }
            return@Scaffold
        }

        val record = ui.record ?: run {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Ocorrência não encontrada.")
            }
            return@Scaffold
        }

        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
            .format(Date(record.createdAtMillis))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = record.draft.title.ifBlank { "Ocorrência" },
                            style = MaterialTheme.typography.titleLarge
                        )

                        val statusLabel = if (record.status == ReportStatus.SYNCED) "ENVIADO" else "PENDENTE"
                        AssistChip(onClick = {}, label = { Text(statusLabel) })

                        Text(
                            text = "Data: $date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Categoria", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = record.draft.category?.name ?: "Sem categoria",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Gênero da vítima", style = MaterialTheme.typography.titleMedium)

                        val text = when (record.draft.victimType) {
                            br.com.infoplus.infoplus.features.report.model.VictimType.SELF ->
                                "Você (será vinculado ao cadastro)"
                            br.com.infoplus.infoplus.features.report.model.VictimType.OTHER ->
                                record.draft.victimGender.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.titlecase() }
                            else -> "Não informado"
                        }

                        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Descrição", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = record.draft.description.ifBlank { "Sem descrição" },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Localização", style = MaterialTheme.typography.titleMedium)

                        val locationText = buildString {
                            val street = record.draft.street
                            val number = record.draft.number
                            val district = record.draft.district
                            val city = record.draft.city
                            val manual = record.draft.manualLocationText

                            if (street.isNotBlank()) {
                                append(street)
                                if (number.isNotBlank()) append(", $number")
                                if (district.isNotBlank()) append(" - $district")
                                if (city.isNotBlank()) append(" / $city")
                            } else if (manual.isNotBlank()) {
                                append(manual)
                            } else {
                                append("Localização não informada")
                            }
                        }

                        Text(locationText, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        val lat = record.draft.lat
                        val lon = record.draft.lon
                        if (lat != null && lon != null) {
                            Text(
                                text = "Coords: $lat, $lon",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Privacidade", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (record.draft.isAnonymous) "Denúncia anônima" else "Identificada",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                ElevatedCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Anexos", style = MaterialTheme.typography.titleMedium)
                        val atts = record.draft.attachments

                        if (atts.isEmpty()) {
                            Text("Nenhum anexo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            AttachmentsList(atts)
                        }
                    }
                }
            }

            if (record.status == ReportStatus.QUEUED) {
                item {
                    Button(
                        onClick = { vm.trySyncOne(id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tentar enviar agora")
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentsList(items: List<Attachment>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { a ->
            ElevatedCard {
                Column(Modifier.padding(12.dp)) {
                    Text(a.type.name, style = MaterialTheme.typography.labelLarge)
                    Text(
                        a.uri,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
