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

    // ✅ novos campos resolvidos
    street: String,
    number: String,
    district: String,
    city: String,

    onToggleUseCurrent: (Boolean) -> Unit,
    onManualTextChange: (String) -> Unit,
    onCaptureNow: () -> Unit
) {
    // Monta as linhas do endereço (BR)
    val line1 = listOf(street.trim(), number.trim())
        .filter { it.isNotBlank() }
        .joinToString(", ")

    val line2 = listOf(district.trim(), city.trim())
        .filter { it.isNotBlank() }
        .joinToString(" – ")

    val hasResolvedAddress = line1.isNotBlank() || line2.isNotBlank()
    val hasCoords = lat != null && lon != null

    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            // Botão de captura
            if (useCurrent) {
                Button(
                    onClick = onCaptureNow,
                    enabled = !isGettingLocation
                ) {
                    Text(if (isGettingLocation) "Capturando…" else "Capturar agora")
                }
            }

            // STATUS / RESULTADO
            if (useCurrent) {
                when {
                    // Endereço já resolvido
                    hasResolvedAddress -> {
                        Text(
                            text = line1.ifBlank { "Endereço identificado" },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (line2.isNotBlank()) {
                            Text(
                                text = line2,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Capturou coordenadas mas ainda não resolveu endereço
                    hasCoords && isGettingLocation -> {
                        Text(
                            text = "Localização capturada, resolvendo endereço…",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Capturou coordenadas, mas não veio endereço (fallback)
                    hasCoords -> {
                        Text(
                            text = "Localização capturada",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Lat: ${"%.5f".format(lat)} • Lon: ${"%.5f".format(lon)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Ainda não capturou nada
                    else -> {
                        Text(
                            "Ainda não capturada",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
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
