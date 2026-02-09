package br.com.infoplus.infoplus.features.report

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
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
import androidx.navigation.NavHostController
import br.com.infoplus.infoplus.core.ui.components.InfoSectionCard
import br.com.infoplus.infoplus.core.ui.components.ScreenContainer
import br.com.infoplus.infoplus.features.report.components.AttachmentsPickerRow
import br.com.infoplus.infoplus.features.report.model.Gender
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory
import br.com.infoplus.infoplus.features.report.model.VictimType
import br.com.infoplus.infoplus.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportScreen(
    navController: NavHostController,
    vm: ReportViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    // ==============================
    // Permissão de localização
    // ==============================
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

    // ==============================
    // Scaffold
    // ==============================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar ocorrência") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { padding ->

        ScreenContainer(padding = padding) {

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                // ==============================
                // O QUE ACONTECEU
                // ==============================
                item {
                    InfoSectionCard(title = "O que aconteceu") {

                        Text(
                            "Categoria",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OccurrenceCategory.values().forEach { cat ->
                                FilterChip(
                                    selected = state.draft.category == cat,
                                    onClick = { vm.setCategory(cat) },
                                    label = {
                                        Text(
                                            cat.name.replace("_", " ")
                                                .lowercase()
                                                .replaceFirstChar { it.titlecase() }
                                        )
                                    }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = state.draft.title,
                            onValueChange = vm::setTitle,
                            label = { Text("Título (mín. 4 caracteres)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.draft.description,
                            onValueChange = vm::setDescription,
                            label = { Text("Descrição (mín. 10 caracteres)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                    }
                }

                // ==============================
                // PRIVACIDADE + VÍTIMA
                // ==============================
                item {
                    InfoSectionCard(title = "Privacidade e vítima") {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Denúncia anônima", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Recomendado para reduzir riscos e barreiras de relato.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.draft.isAnonymous,
                                onCheckedChange = vm::setAnonymous
                            )
                        }

                        Divider(Modifier.padding(vertical = 10.dp))

                        Text(
                            "Quem é a vítima?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = state.draft.victimType == VictimType.SELF,
                                onClick = { vm.setVictimType(VictimType.SELF) },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                            ) { Text("Comigo") }

                            SegmentedButton(
                                selected = state.draft.victimType == VictimType.OTHER,
                                onClick = { vm.setVictimType(VictimType.OTHER) },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                            ) { Text("Outra pessoa") }
                        }

                        if (state.draft.victimType == VictimType.OTHER) {
                            Spacer(Modifier.height(10.dp))

                            Text(
                                "Gênero da vítima (obrigatório)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Gender.values()
                                    .filter { it != Gender.NAO_INFORMADO }
                                    .forEach { g ->
                                        FilterChip(
                                            selected = state.draft.victimGender == g,
                                            onClick = { vm.setVictimGender(g) },
                                            label = {
                                                Text(
                                                    g.name.replace("_", " ")
                                                        .lowercase()
                                                        .replaceFirstChar { it.titlecase() }
                                                )
                                            }
                                        )
                                    }
                            }

                            if (state.draft.victimGender == Gender.NAO_INFORMADO) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Selecione o gênero da vítima para fins estatísticos e políticas públicas.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // ==============================
                // ONDE ACONTECEU
                // ==============================
                item {
                    InfoSectionCard(title = "Onde aconteceu") {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Usar localização atual", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Recomendado para estatística territorial e mapa.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.draft.useCurrentLocation,
                                onCheckedChange = vm::setUseCurrentLocation
                            )
                        }

                        if (state.draft.useCurrentLocation) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
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
                                ) {
                                    Text(if (state.isGettingLocation) "Capturando..." else "Capturar localização")
                                }

                                OutlinedButton(onClick = vm::syncNow) { Text("Sincronizar") }
                            }

                            if (state.draft.street.isNotBlank() || state.draft.city.isNotBlank()) {
                                Divider(Modifier.padding(vertical = 6.dp))
                                Text(
                                    text = buildString {
                                        if (state.draft.street.isNotBlank()) append(state.draft.street)
                                        if (state.draft.number.isNotBlank()) append(", ").append(state.draft.number)
                                        if (state.draft.district.isNotBlank()) append(" • ").append(state.draft.district)
                                        if (state.draft.city.isNotBlank()) append(" • ").append(state.draft.city)
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = state.draft.manualLocationText,
                                onValueChange = vm::setManualLocation,
                                label = { Text("Informe o local (bairro/cidade/endereço)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // ==============================
                // EVIDÊNCIAS (foto/vídeo/áudio + gravação) — via componente existente
                // ==============================
                item {
                    InfoSectionCard(title = "Evidências (opcional)") {

                        Text(
                            text = "Anexe evidências com responsabilidade. Evite expor dados sensíveis desnecessários.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(Modifier.height(10.dp))

                        AttachmentsPickerRow(
                            attachments = state.draft.attachments,
                            onAddAttachment = { uri, type -> vm.addAttachment(uri, type) },
                            onRemoveAttachment = { uri -> vm.removeAttachment(uri) },
                            maxAttachments = 3,
                            onError = { msg -> vm.setError(msg) }
                        )
                    }
                }

                // ==============================
                // CONFIRMAÇÃO / ENVIO
                // ==============================
                item {
                    InfoSectionCard(title = "Confirmação") {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Li e aceito os termos", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Uso responsável, sem exposição indevida de dados sensíveis.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.draft.acceptedTerms,
                                onCheckedChange = vm::setTerms
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val offline = !state.isOnline
                                vm.submit {
                                    navController.navigate("${Routes.REPORT_SUCCESS}?offline=$offline") {
                                        popUpTo(Routes.REPORT) { inclusive = true }
                                    }
                                }

                            },
                            enabled = state.canSubmit,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (state.isSubmitting) "Enviando..." else "Enviar ocorrência")
                        }

                        if (!state.isOnline) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Você está offline. O registro será salvo e sincronizado automaticamente quando a internet voltar.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // ==============================
                // ERRO (se quiser exibir fora dos cards)
                // ==============================
                item {
                    state.errorMessage?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}