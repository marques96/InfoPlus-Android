package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold

@Composable
fun ReportLocationStep(
    useCurrentLocation: Boolean,
    manualLocationText: String,
    street: String,
    number: String,
    district: String,
    city: String,
    isGettingLocation: Boolean,
    hasValidLocation: Boolean,
    errorMessage: String?,
    onUseCurrentLocationChange: (Boolean) -> Unit,
    onManualLocationChange: (String) -> Unit,
    onCaptureLocation: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    ReportStepScaffold(
        title = "Onde aconteceu?",
        subtitle = "Você pode usar a localização atual ou informar manualmente o local.",
        stepIndex = 6,
        totalSteps = 9,
        primaryButtonText = "Continuar",
        onPrimaryClick = onNext,
        onBackClick = onBack,
        primaryEnabled = hasValidLocation
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = useCurrentLocation,
                    onClick = { onUseCurrentLocationChange(true) },
                    label = { Text("Usar localização atual") }
                )

                FilterChip(
                    selected = !useCurrentLocation,
                    onClick = { onUseCurrentLocationChange(false) },
                    label = { Text("Informar manualmente") }
                )
            }

            if (useCurrentLocation) {
                Button(
                    onClick = onCaptureLocation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isGettingLocation) "Capturando..." else "Capturar localização")
                }

                val resolvedAddress = buildString {
                    if (street.isNotBlank()) append(street)
                    if (number.isNotBlank()) append(", ").append(number)
                    if (district.isNotBlank()) append(" • ").append(district)
                    if (city.isNotBlank()) append(" • ").append(city)
                }

                if (resolvedAddress.isNotBlank()) {
                    Text(
                        text = resolvedAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                OutlinedTextField(
                    value = manualLocationText,
                    onValueChange = onManualLocationChange,
                    label = { Text("Informe o local (bairro, rua, cidade)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}