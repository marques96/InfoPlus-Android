package br.com.infoplus.infoplus.features.report.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.infoplus.infoplus.features.report.model.OccurrenceCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selected: OccurrenceCategory?,
    onSelected: (OccurrenceCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected?.toDisplayName() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Selecione a categoria") },
            placeholder = { Text("Ex: Assédio, Assalto...") },
            leadingIcon = {
                selected?.let {
                    Icon(imageVector = it.toIcon(), contentDescription = null)
                } ?: Icon(imageVector = Icons.Default.Category, contentDescription = null)
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            shape = MaterialTheme.shapes.medium
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            OccurrenceCategory.values().forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.toDisplayName()) },
                    leadingIcon = {
                        Icon(
                            imageVector = cat.toIcon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onSelected(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun OccurrenceCategory.toDisplayName(): String {
    return when (this) {
        OccurrenceCategory.ASSALTO -> "Assalto"
        OccurrenceCategory.ASSEDIO -> "Assédio"
        OccurrenceCategory.VIOLENCIA -> "Violência"
        OccurrenceCategory.DESAPARECIMENTO -> "Desaparecimento"
        OccurrenceCategory.EMERGENCIA_MEDICA -> "Emergência Médica"
        OccurrenceCategory.OUTROS -> "Outros"
    }
}

private fun OccurrenceCategory.toIcon(): ImageVector {
    return when (this) {
        OccurrenceCategory.ASSALTO -> Icons.Default.Warning
        OccurrenceCategory.ASSEDIO -> Icons.Default.RecordVoiceOver
        OccurrenceCategory.VIOLENCIA -> Icons.Default.Gavel
        OccurrenceCategory.DESAPARECIMENTO -> Icons.Default.Search
        OccurrenceCategory.EMERGENCIA_MEDICA -> Icons.Default.MedicalServices
        OccurrenceCategory.OUTROS -> Icons.Default.MoreHoriz
    }
}
