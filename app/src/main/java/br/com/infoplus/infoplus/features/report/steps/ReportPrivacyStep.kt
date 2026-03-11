package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold

@Composable
fun ReportPrivacyStep(
    isAnonymous: Boolean,
    acceptedTerms: Boolean,
    onAnonymousChange: (Boolean) -> Unit,
    onAcceptedTermsChange: (Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    ReportStepScaffold(
        title = "Privacidade e confirmação",
        subtitle = "Antes de enviar, defina a privacidade do registro e confirme os termos.",
        stepIndex = 8,
        totalSteps = 9,
        primaryButtonText = "Continuar",
        onPrimaryClick = onNext,
        onBackClick = onBack,
        primaryEnabled = acceptedTerms
    ) {
        Column(verticalArrangement = Arrangement.spacedBy((18.dp))) {

            Column(verticalArrangement = Arrangement.spacedBy((8.dp))) {
                Text(
                    text = "Denúncia anônima",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Recomendado para reduzir riscos e barreiras de relato.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = isAnonymous,
                    onCheckedChange = onAnonymousChange
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy((8.dp))) {
                Text(
                    text = "Aceite dos termos",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Confirme que o registro foi preenchido de forma responsável e sem exposição indevida de dados sensíveis.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = acceptedTerms,
                    onCheckedChange = onAcceptedTermsChange
                )
            }
        }
    }
}