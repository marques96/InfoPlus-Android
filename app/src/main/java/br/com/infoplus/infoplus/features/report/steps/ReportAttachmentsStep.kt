package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.report.components.AttachmentsPickerRow
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold
import br.com.infoplus.infoplus.features.report.model.Attachment
import br.com.infoplus.infoplus.features.report.model.AttachmentType

@Composable
fun ReportAttachmentsStep(
    attachments: List<Attachment>,
    onAddAttachment: (String, AttachmentType) -> Unit,
    onRemoveAttachment: (String) -> Unit,
    onError: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    ReportStepScaffold(
        title = "Deseja adicionar evidências?",
        subtitle = "Você pode anexar foto, vídeo ou áudio. Essa etapa é opcional.",
        stepIndex = 7,
        totalSteps = 9,
        primaryButtonText = "Continuar",
        onPrimaryClick = onNext,
        onBackClick = onBack,
        primaryEnabled = true
    ) {
        Column(verticalArrangement = Arrangement.spacedBy((12.dp))) {
            Text(
                text = "Anexe evidências com responsabilidade. Evite expor dados sensíveis desnecessários.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AttachmentsPickerRow(
                attachments = attachments,
                onAddAttachment = onAddAttachment,
                onRemoveAttachment = onRemoveAttachment,
                maxAttachments = 3,
                onError = onError
            )
        }
    }
}