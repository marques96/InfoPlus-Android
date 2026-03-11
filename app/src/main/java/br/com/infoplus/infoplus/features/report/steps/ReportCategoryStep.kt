package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold
import br.com.infoplus.infoplus.features.report.components.SelectionOptionGrid
import br.com.infoplus.infoplus.features.report.components.SelectionOptionItem
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory

@Composable
fun ReportCategoryStep(
    selectedCategory: OccurrenceCategory?,
    onSelectCategory: (OccurrenceCategory) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val options = OccurrenceCategory.values().map {
        SelectionOptionItem(
            key = it.name,
            label = it.toDisplayText()
        )
    }

    ReportStepScaffold(
        title = "Qual tipo de ocorrência você quer registrar?",
        subtitle = "Escolha a categoria que mais se aproxima do caso.",
        stepIndex = 2,
        totalSteps = 9,
        primaryButtonText = "Prosseguir",
        onPrimaryClick = onNext,
        onBackClick = onBack,
        primaryEnabled = selectedCategory != null
    ) {
        SelectionOptionGrid(
            options = options,
            selectedKey = selectedCategory?.name,
            onSelect = { selected ->
                onSelectCategory(OccurrenceCategory.valueOf(selected.key))
            },
            itemHeight = (72.dp)
        )
    }
}

private fun OccurrenceCategory.toDisplayText(): String {
    return when (this) {
        OccurrenceCategory.ASSALTO -> "Assalto"
        OccurrenceCategory.ASSEDIO -> "Assédio"
        OccurrenceCategory.VIOLENCIA -> "Violência"
        OccurrenceCategory.DESAPARECIMENTO -> "Desaparecimento"
        OccurrenceCategory.EMERGENCIA_MEDICA -> "Emergência médica"
        OccurrenceCategory.OUTROS -> "Outros"
    }
}