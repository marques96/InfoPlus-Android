package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold

@Composable
fun ReportIntroStep(
    onNext: () -> Unit
) {
    ReportStepScaffold(
        title = "Registrar ocorrência",
        subtitle = "Vamos conduzir você por etapas simples. O registro pode ser anônimo e, se estiver offline, será salvo com segurança.",
        stepIndex = 1,
        totalSteps = 9,
        primaryButtonText = "Começar",
        onPrimaryClick = onNext,
        onBackClick = null
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "• O preenchimento é guiado e objetivo.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "• Você pode anexar evidências.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "• O envio pode ser anônimo.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "• Se não houver internet, o registro será salvo localmente.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}