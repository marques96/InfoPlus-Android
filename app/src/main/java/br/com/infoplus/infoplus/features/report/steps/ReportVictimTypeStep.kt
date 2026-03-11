package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold
import br.com.infoplus.infoplus.features.report.components.SelectionOptionGrid
import br.com.infoplus.infoplus.features.report.components.SelectionOptionItem
import br.com.infoplus.infoplus.features.report.model.VictimType

@Composable
fun ReportVictimTypeStep(
    selectedVictimType: VictimType?,
    onSelectVictimType: (VictimType) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val options = listOf(
        SelectionOptionItem(
            key = VictimType.SELF.name,
            label = "Comigo"
        ),
        SelectionOptionItem(
            key = VictimType.OTHER.name,
            label = "Outra pessoa"
        )
    )

    ReportStepScaffold(
        title = "Quem é a vítima?",
        subtitle = "Essa informação ajuda a conduzir corretamente o restante do registro.",
        stepIndex = 3,
        totalSteps = 9,
        primaryButtonText = "Prosseguir",
        onPrimaryClick = onNext,
        onBackClick = onBack,
        primaryEnabled = selectedVictimType != null
    ) {
        SelectionOptionGrid(
            options = options,
            selectedKey = selectedVictimType?.name,
            onSelect = { selected ->
                onSelectVictimType(VictimType.valueOf(selected.key))
            },
            minColumnsOnCompact = 2,
            minColumnsOnLarge = 2,
            itemHeight = (76.dp)
        )
    }
}