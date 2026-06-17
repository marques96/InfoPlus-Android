package br.com.infoplus.infoplus.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    vm.load(id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detalhes da Ocorrência",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    if (ui.status == ReportStatus.QUEUED) {
                        IconButton(onClick = { vm.trySyncOne(id) }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Tentar enviar agora", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (ui.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(strokeWidth = 3.dp)
            }
            return@Scaffold
        }

        if (ui.errorMessage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(16.dp))
                Text("Erro ao carregar detalhes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(ui.errorMessage, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { vm.load(id) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tentar novamente")
                }
            }
            return@Scaffold
        }

        val record = ui.record ?: run {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Ocorrência não encontrada.")
            }
            return@Scaffold
        }

        val date = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
            .format(Date(record.createdAtMillis))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HeaderSection(
                    title = record.draft.title.ifBlank { "Ocorrência sem Título" },
                    date = date,
                    status = record.status
                )
            }

            item {
                DetailCard(
                    icon = Icons.Default.Category,
                    label = "Categoria",
                    value = record.draft.category?.name ?: "Não informada",
                    iconColor = Color(0xFF1976D2)
                )
            }

            item {
                val victimText = when (record.draft.victimType) {
                    br.com.infoplus.infoplus.features.report.model.VictimType.SELF -> "Você"
                    br.com.infoplus.infoplus.features.report.model.VictimType.OTHER ->
                        record.draft.victimGender.name.replace("_", " ").lowercase()
                            .replaceFirstChar { it.titlecase() }
                    else -> "Não informado"
                }
                DetailCard(
                    icon = Icons.Default.Person,
                    label = "Gênero da Vítima",
                    value = victimText,
                    iconColor = Color(0xFF7B1FA2)
                )
            }

            item {
                DetailCard(
                    icon = Icons.Default.Description,
                    label = "Descrição dos Fatos",
                    value = record.draft.description.ifBlank { "Nenhuma descrição detalhada fornecida." },
                    iconColor = Color(0xFFF57C00)
                )
            }

            item {
                val locationText = buildString {
                    val street = record.draft.street
                    val number = record.draft.number
                    val district = record.draft.district
                    val city = record.draft.city
                    val manual = record.draft.manualLocationText

                    if (street.isNotBlank()) {
                        append(street)
                        if (number.isNotBlank()) append(", $number")
                        if (district.isNotBlank()) append("\n$district")
                        if (city.isNotBlank()) append(" - $city")
                    } else if (manual.isNotBlank()) {
                        append(manual)
                    } else {
                        append("Localização não informada")
                    }
                }
                DetailCard(
                    icon = Icons.Default.LocationOn,
                    label = "Localização",
                    value = locationText,
                    iconColor = Color(0xFFD32F2F),
                    secondaryValue = if (record.draft.lat != null) "Coordenadas: ${record.draft.lat}, ${record.draft.lon}" else null
                )
            }

            item {
                DetailCard(
                    icon = if (record.draft.isAnonymous) Icons.Default.VisibilityOff else Icons.Default.PrivacyTip,
                    label = "Privacidade",
                    value = if (record.draft.isAnonymous) "Relato Anônimo" else "Relato Identificado",
                    iconColor = if (record.draft.isAnonymous) Color(0xFF616161) else Color(0xFF388E3C)
                )
            }

            item {
                AttachmentsSection(record.draft.attachments)
            }

            if (record.status == ReportStatus.QUEUED) {
                item {
                    Button(
                        onClick = { vm.trySyncOne(id) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sincronizar Agora", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(title: String, date: String, status: ReportStatus) {
    Column {
        val statusColor = if (status == ReportStatus.SYNCED) Color(0xFF388E3C) else Color(0xFFF57C00)
        val statusText = if (status == ReportStatus.SYNCED) "Sincronizado" else "Pendente de Envio"

        Surface(
            color = statusColor.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = statusText.uppercase(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(Modifier.height(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    secondaryValue: String? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
                if (secondaryValue != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = secondaryValue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentsSection(attachments: List<Attachment>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Anexos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Text(
                    text = attachments.size.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (attachments.isEmpty()) {
            Text(
                "Nenhum arquivo anexado a esta ocorrência.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            attachments.forEach { attachment ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (attachment.type.name) {
                                    "AUDIO" -> Icons.Default.Audiotrack
                                    "PHOTO" -> Icons.Default.PhotoCamera
                                    else -> Icons.Default.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = attachment.type.name,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = attachment.uri.split("/").last(),
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
