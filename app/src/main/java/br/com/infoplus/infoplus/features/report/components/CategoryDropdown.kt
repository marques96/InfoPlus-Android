package br.com.infoplus.infoplus.features.report.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.name?.replace('_', ' ') ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            OccurrenceCategory.values().forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.name.replace('_', ' ')) },
                    onClick = {
                        onSelected(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}
