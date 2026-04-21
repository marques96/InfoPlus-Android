package br.com.infoplus.infoplus.features.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.infoplus.infoplus.features.map.model.MapFeedItem
import br.com.infoplus.infoplus.features.map.model.ZoneType

@Composable
fun ZoneOverviewCard(
    zone: MapFeedItem.ZoneCard,
    onClick: () -> Unit
) {
    val badgeColor = when (zone.type) {
        ZoneType.FRIENDLY -> Color(0x334CAF50)
        ZoneType.RISK -> Color(0x33EF5350)
    }

    val levelText = when {
        zone.score >= 10 -> "Nível alto"
        zone.score >= 7 -> "Nível moderado"
        else -> "Nível leve"
    }

    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = zone.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = zone.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = onClick,
                    label = { Text(levelText) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = badgeColor
                    )
                )

                AssistChip(
                    onClick = onClick,
                    label = { Text("${zone.supportCount} registros") }
                )
            }
        }
    }
}