package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold
import br.com.infoplus.infoplus.features.report.components.SelectionOptionGrid
import br.com.infoplus.infoplus.features.report.components.SelectionOptionItem
import br.com.infoplus.infoplus.features.report.model.Gender

@Composable
fun ReportVictimGenderStep(
    selectedGender: Gender,
    onSelectGender: (Gender) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val options = Gender.values()
        .filter { it != Gender.NAO_INFORMADO }
        .map {
            SelectionOptionItem(
                key = it.name,
                label = it.toDisplayText()
            )
        }

    ReportStepScaffold(
        title = "Qual é o gênero da vítima?",
        subtitle = "Essa informação é obrigatória para gerar dados mais confiáveis e apoiar políticas públicas.",
        stepIndex = 4,
        totalSteps = 9,
        primaryButtonText = "Prosseguir",
        onPrimaryClick = onNext,
        onBackClick = onBack,
        primaryEnabled = selectedGender != Gender.NAO_INFORMADO
    ) {
        SelectionOptionGrid(
            options = options,
            selectedKey = selectedGender.name,
            onSelect = { selected ->
                onSelectGender(Gender.valueOf(selected.key))
            },
            itemHeight = (72.dp)
        )
    }
}

private fun Gender.toDisplayText(): String {
    return when (this) {
        Gender.FEMININO -> "Feminino"
        Gender.MASCULINO -> "Masculino"
        Gender.NAO_BINARIO -> "Não binário"
        Gender.TRAVESTI -> "Travesti"
        Gender.HOMEM_TRANS -> "Homem trans"
        Gender.MULHER_TRANS -> "Mulher trans"
        Gender.NAO_INFORMADO -> "Não informado"
    }
}