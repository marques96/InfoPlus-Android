package br.com.infoplus.infoplus.features.report.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.report.components.ReportStepScaffold

@Composable
fun ReportDescriptionStep(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    ReportStepScaffold(
        title = "Conte o que aconteceu",
        subtitle = "Descreva de forma objetiva. Você poderá revisar tudo antes de enviar.",
        stepIndex = 5,
        totalSteps = 9,
        primaryButtonText = "Continuar",
        onPrimaryClick = onNext,
        onBackClick = onBack,
        primaryEnabled = title.trim().length >= 4 && description.trim().length >= 10
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título (mín. 4 caracteres)") },
                modifier = Modifier,
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descrição (mín. 10 caracteres)") },
                modifier = Modifier,
                minLines = 6
            )
        }
    }
}