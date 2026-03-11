package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold
import br.com.infoplus.infoplus.features.report.model.OccurrenceDraft
import br.com.infoplus.infoplus.features.report.model.VictimType

@Composable
fun ReportReviewStep(
    draft: OccurrenceDraft,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    ReportStepScaffold(
        title = "Revise antes de enviar",
        subtitle = "Confira as informações abaixo. Você pode voltar e editar qualquer etapa.",
        stepIndex = 9,
        totalSteps = 9,
        primaryButtonText = if (isSubmitting) "Enviando..." else "Enviar ocorrência",
        onPrimaryClick = onSubmit,
        onBackClick = onBack,
        primaryEnabled = canSubmit && !isSubmitting
    ) {
        Column(verticalArrangement = Arrangement.spacedBy((12.dp))) {

            ReviewItem("Categoria", draft.category?.name ?: "Não informada")
            ReviewItem("Título", draft.title.ifBlank { "Não informado" })
            ReviewItem("Descrição", draft.description.ifBlank { "Não informada" })
            ReviewItem(
                "Vítima",
                when (draft.victimType) {
                    VictimType.SELF -> "Comigo"
                    VictimType.OTHER -> "Outra pessoa"
                    null -> "Não informado"
                }
            )

            if (draft.victimType == VictimType.OTHER) {
                ReviewItem(
                    "Gênero da vítima",
                    draft.victimGender.name
                        .replace("_", " ")
                        .lowercase()
                        .replaceFirstChar { it.titlecase() }
                )
            }

            ReviewItem(
                "Localização",
                if (draft.useCurrentLocation) {
                    listOf(draft.street, draft.number, draft.district, draft.city)
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                        .ifBlank { "Localização atual capturada" }
                } else {
                    draft.manualLocationText.ifBlank { "Não informado" }
                }
            )

            ReviewItem(
                "Anexos",
                "${draft.attachments.size} item(ns)"
            )

            ReviewItem(
                "Privacidade",
                if (draft.isAnonymous) "Anônima" else "Identificada"
            )

            ReviewItem(
                "Termos",
                if (draft.acceptedTerms) "Aceitos" else "Não aceitos"
            )
        }
    }
}

@Composable
private fun ReviewItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy((4.dp))) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}