package br.com.infoplus.infoplus.features.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RegionStatusCard(
    state: RegionSafetyState,
    message: String
) {
    val containerColor = when (state) {
        RegionSafetyState.SAFE -> Color(0xFFEAF7EE)
        RegionSafetyState.ATTENTION -> Color(0xFFFFF6E5)
        RegionSafetyState.ALERT -> Color(0xFFFFECEC)
    }

    val badgeColor = when (state) {
        RegionSafetyState.SAFE -> Color(0xFF2E7D32)
        RegionSafetyState.ATTENTION -> Color(0xFFF9A825)
        RegionSafetyState.ALERT -> Color(0xFFC62828)
    }

    val title = when (state) {
        RegionSafetyState.SAFE -> "Você está em uma área estável"
        RegionSafetyState.ATTENTION -> "Atenção à movimentação da região"
        RegionSafetyState.ALERT -> "Área com maior atenção no momento"
    }

    val icon = when (state) {
        RegionSafetyState.SAFE -> Icons.Default.GppGood
        RegionSafetyState.ATTENTION -> Icons.Default.Info
        RegionSafetyState.ALERT -> Icons.Default.WarningAmber
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = containerColor,
        tonalElevation = 2.dp,
        shadowElevation = 5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = badgeColor.copy(alpha = 0.14f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor
                    )
                }

                Column {
                    Text(
                        text = "Status da sua região",
                        style = MaterialTheme.typography.labelLarge,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}