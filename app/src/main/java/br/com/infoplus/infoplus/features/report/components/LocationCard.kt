package br.com.infoplus.infoplus.features.report.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationCard(
    useCurrent: Boolean,
    isGettingLocation: Boolean,
    lat: Double?,
    lon: Double?,
    manualText: String,
    onToggleUseCurrent: (Boolean) -> Unit,
    onManualTextChange: (String) -> Unit,
    onCaptureNow: () -> Unit
) {
    Card {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Linha do switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Usar localização atual")
                Switch(
                    checked = useCurrent,
                    onCheckedChange = onToggleUseCurrent
                )
            }

            // Botão de captura (horizontal, padrão)
            if (useCurrent) {
                Button(
                    onClick = onCaptureNow,
                    enabled = !isGettingLocation
                ) {
                    Text(if (isGettingLocation) "Capturando…" else "Capturar agora")
                }
            }

            // Status
            if (lat != null && lon != null) {
                Text(
                    "Capturada: $lat, $lon",
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (useCurrent) {
                Text(
                    "Ainda não capturada",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Manual
            if (!useCurrent) {
                OutlinedTextField(
                    value = manualText,
                    onValueChange = onManualTextChange,
                    label = { Text("Endereço / ponto de referência") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }
}
