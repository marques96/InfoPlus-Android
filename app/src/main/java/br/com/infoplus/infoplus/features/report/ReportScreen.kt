package br.com.infoplus.infoplus.features.report

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.features.report.components.AttachmentsPickerRow
import br.com.infoplus.infoplus.features.report.components.CategoryDropdown
import br.com.infoplus.infoplus.features.report.components.DateTimePickerCard
import br.com.infoplus.infoplus.features.report.components.LocationCard
import br.com.infoplus.infoplus.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavHostController,
    vm: ReportViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    // Paleta/estilo do Home
    val primary = Color(0xFF5B5A95)
    val bgBrush = Brush.verticalGradient(
        listOf(Color.White, Color(0xFFF6F5FF), Color(0xFFFFFBF0))
    )
    val titleColor = Color(0xFF1F1F1F)
    val subtitleColor = Color(0xFF2F2F2F)
    val sectionShape = RoundedCornerShape(18.dp)

    // Permissão de localização
    var hasLocationPermission by remember { mutableStateOf(false) }
    var pendingLocationCapture by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        val granted =
            (res[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (res[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        hasLocationPermission = granted
        if (pendingLocationCapture) {
            pendingLocationCapture = false
            vm.captureLocation(granted)
        }
    }

    val locationError = remember(state.errorMessage) {
        state.errorMessage?.takeIf { it.contains("localiza", ignoreCase = true) }
    }
    val genericError = remember(state.errorMessage) {
        state.errorMessage?.takeIf { it != null && locationError == null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar ocorrência") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.imePadding()
    ) { padding ->

        // Fundo igual ao Home
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {

                // Banner offline (discreto)
                if (!state.isOnline) {
                    item {
                        Card(
                            shape = sectionShape,
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(
                                    "Você está offline",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = titleColor
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Seu registro será enviado assim que houver conexão com a internet.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2F2F2F)
                                )
                            }
                        }
                    }
                }

                // SEÇÃO — O que aconteceu
                item {
                    SectionCard(
                        title = "O que aconteceu",
                        titleColor = titleColor,
                        shape = sectionShape
                    ) {
                        CategoryDropdown(
                            selected = state.draft.category,
                            onSelected = vm::setCategory,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.draft.title,
                            onValueChange = vm::setTitle,
                            label = { Text("Título") },
                            supportingText = { Text("Ex: Assalto na parada") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.draft.description,
                            onValueChange = vm::setDescription,
                            label = { Text("Descrição") },
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )

                        genericError?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // SEÇÃO — Onde aconteceu
                item {
                    SectionCard(
                        title = "Onde aconteceu",
                        titleColor = titleColor,
                        shape = sectionShape
                    ) {
                        LocationCard(
                            useCurrent = state.draft.useCurrentLocation,
                            isGettingLocation = state.isGettingLocation,
                            lat = state.draft.lat,
                            lon = state.draft.lon,
                            manualText = state.draft.manualLocationText,
                            onToggleUseCurrent = vm::setUseCurrentLocation,
                            onManualTextChange = vm::setManualLocation,
                            onCaptureNow = {
                                if (hasLocationPermission) {
                                    vm.captureLocation(true)
                                } else {
                                    pendingLocationCapture = true
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        )

                        locationError?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // SEÇÃO — Quando aconteceu
                item {
                    SectionCard(
                        title = "Quando aconteceu",
                        titleColor = titleColor,
                        shape = sectionShape
                    ) {
                        DateTimePickerCard(
                            dateTimeMillis = state.draft.dateTimeMillis,
                            onSetNow = { vm.setDateTimeMillis(System.currentTimeMillis()) },
                            onSetDateTimeMillis = vm::setDateTimeMillis
                        )
                    }
                }

                // SEÇÃO — Evidências
                item {
                    SectionCard(
                        title = "Evidências (opcional)",
                        titleColor = titleColor,
                        shape = sectionShape
                    ) {
                        Text(
                            "Você pode anexar foto, vídeo ou áudio para ajudar na verificação.",
                            style = MaterialTheme.typography.bodySmall,
                            color = subtitleColor
                        )

                        AttachmentsPickerRow(
                            attachments = state.draft.attachments,
                            onAddAttachment = vm::addAttachment,
                            onRemoveAttachment = vm::removeAttachment,
                            maxAttachments = 3,
                            onError = vm::setError
                        )
                    }
                }

                // SEÇÃO — Privacidade
                item {
                    SectionCard(
                        title = "Privacidade",
                        titleColor = titleColor,
                        shape = sectionShape
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Enviar como anônimo", color = titleColor)
                                Text(
                                    "Seu nome não será exibido no registro.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = subtitleColor
                                )
                            }
                            Switch(
                                checked = state.draft.isAnonymous,
                                onCheckedChange = vm::setAnonymous
                            )
                        }
                    }
                }

                // SEÇÃO — Confirmação / Envio
                item {
                    SectionCard(
                        title = "Confirmação",
                        titleColor = titleColor,
                        shape = sectionShape
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = state.draft.acceptedTerms,
                                onCheckedChange = vm::setTerms
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Aceito os termos e confirmo as informações.",
                                color = titleColor
                            )
                        }

                        if (!state.canSubmit) {
                            val missing = buildList {
                                if (state.draft.category == null) add("Selecione uma categoria")
                                if (state.draft.title.trim().length < 4) add("Informe um título (mín. 4 caracteres)")
                                if (state.draft.description.trim().length < 10) add("Descreva melhor (mín. 10 caracteres)")
                                if (!state.draft.acceptedTerms) add("Aceite os termos")

                                val hasLoc =
                                    (state.draft.useCurrentLocation && state.draft.lat != null && state.draft.lon != null) ||
                                            (!state.draft.useCurrentLocation && state.draft.manualLocationText.trim().isNotEmpty())

                                if (!hasLoc) add("Informe a localização (capturar ou manual)")
                            }

                            if (missing.isNotEmpty()) {
                                Text(
                                    text = "Para enviar: ${missing.joinToString(" • ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.canSubmit,
                            onClick = { vm.submit { navController.navigate(Routes.REPORT_SUCCESS) } }
                        ) {
                            if (state.isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Enviando…")
                            } else {
                                Text("Enviar ocorrência")
                            }
                        }

                        if (state.isOfflinePending) {
                            Text(
                                "Registro salvo como pendente. Ele será enviado automaticamente quando houver internet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = subtitleColor
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card de seção no mesmo “feeling” do Home:
 * - container branco
 * - shape 18.dp
 * - espaçamento consistente
 */
@Composable
private fun SectionCard(
    title: String,
    titleColor: Color,
    shape: RoundedCornerShape,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                Text(title, style = MaterialTheme.typography.titleMedium, color = titleColor)
                content()
            }
        )
    }
}
