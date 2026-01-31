package br.com.infoplus.infoplus.features.report.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DateTimeRow(
    dateTimeMillis: Long,
    onSetNow: () -> Unit,
    onAddMinutes: (Int) -> Unit
) {
    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
    val text = fmt.format(Date(dateTimeMillis))

    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Data/Hora", style = MaterialTheme.typography.titleMedium)
                Text(text)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onAddMinutes(-60) }) { Text("-1h") }
                OutlinedButton(onClick = onSetNow) { Text("Agora") }
                OutlinedButton(onClick = { onAddMinutes(+60) }) { Text("+1h") }
            }
        }
    }
}
