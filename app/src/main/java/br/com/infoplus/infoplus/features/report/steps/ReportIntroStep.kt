package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold

@Composable
fun ReportIntroStep(
    dontShowAgainChecked: Boolean,
    onDontShowAgainChange: (Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    ReportStepScaffold(
        title = "Registrar ocorrência",
        subtitle = "O registro é guiado, pode ser anônimo e não coleta dados pessoais identificáveis. O gênero é solicitado apenas como requisito funcional para apoiar dados mais confiáveis sobre violência contra a comunidade LGBTQIAPN+.",
        stepIndex = 1,
        totalSteps = 1,
        primaryButtonText = "Começar",
        onPrimaryClick = onNext,
        onBackClick = onBack
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "• O preenchimento é guiado e objetivo.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "• Você pode informar se a ocorrência aconteceu com você ou com outra pessoa.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "• O gênero da vítima é obrigatório para apoiar a finalidade social do INFO+.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "• O envio pode ser anônimo.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "• Se não houver internet, o registro será salvo com segurança para envio posterior.",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDontShowAgainChange(!dontShowAgainChecked)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = dontShowAgainChecked,
                    onCheckedChange = onDontShowAgainChange
                )
                Text(
                    text = "Não mostrar esta introdução novamente",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}