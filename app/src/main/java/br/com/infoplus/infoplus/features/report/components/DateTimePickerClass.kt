package br.com.infoplus.infoplus.features.report.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerCard(
    dateTimeMillis: Long,
    onSetNow: () -> Unit,
    onSetDateTimeMillis: (Long) -> Unit
) {
    val fmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")) }

    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }

    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Data/Hora", style = MaterialTheme.typography.titleMedium)
            Text(fmt.format(Date(dateTimeMillis)))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showDate = true }) { Text("Selecionar data") }
                OutlinedButton(onClick = { showTime = true }) { Text("Selecionar hora") }
                Button(onClick = onSetNow) { Text("Agora") }
            }
        }
    }

    if (showDate) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = dateTimeMillis)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    val picked = dpState.selectedDateMillis
                    if (picked != null) {
                        // mantém a hora atual e troca só a data
                        val calOld = Calendar.getInstance().apply { timeInMillis = dateTimeMillis }
                        val calNew = Calendar.getInstance().apply { timeInMillis = picked }
                        calNew.set(Calendar.HOUR_OF_DAY, calOld.get(Calendar.HOUR_OF_DAY))
                        calNew.set(Calendar.MINUTE, calOld.get(Calendar.MINUTE))
                        onSetDateTimeMillis(calNew.timeInMillis)
                    }
                    showDate = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = dpState)
        }
    }

    if (showTime) {
        val cal = Calendar.getInstance().apply { timeInMillis = dateTimeMillis }
        val tpState = rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(onClick = {
                    val c = Calendar.getInstance().apply { timeInMillis = dateTimeMillis }
                    c.set(Calendar.HOUR_OF_DAY, tpState.hour)
                    c.set(Calendar.MINUTE, tpState.minute)
                    onSetDateTimeMillis(c.timeInMillis)
                    showTime = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTime = false }) { Text("Cancelar") } },
            title = { Text("Selecionar hora") },
            text = { TimePicker(state = tpState) }
        )
    }
}
