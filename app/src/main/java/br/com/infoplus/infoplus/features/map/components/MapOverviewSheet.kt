package br.com.infoplus.infoplus.features.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MapOverviewSheet(
    nearbyOccurrencesCount: Int,
    nearbySafeZonesCount: Int,
    safetyMessage: String
) {
    val regionState = when {
        nearbyOccurrencesCount == 0 -> RegionSafetyState.SAFE
        nearbyOccurrencesCount in 1..2 -> RegionSafetyState.ATTENTION
        else -> RegionSafetyState.ALERT
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Cabeçalho Estilo Waze
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Olá! Está tudo bem?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Confira o panorama da sua região",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Incidentes",
                value = nearbyOccurrencesCount.toString(),
                description = if (nearbyOccurrencesCount == 0) "Raio limpo" else "Perto de você",
                icon = {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = if (nearbyOccurrencesCount > 0) Color(0xFFD32F2F) else Color(0xFF757575),
                        modifier = Modifier.size(28.dp)
                    )
                },
                containerTint = Color.White
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Zonas Seguras",
                value = nearbySafeZonesCount.toString(),
                description = "Pontos de apoio",
                icon = {
                    Icon(
                        imageVector = Icons.Default.GppGood,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(28.dp)
                    )
                },
                containerTint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        RegionStatusCard(
            state = regionState,
            message = safetyMessage
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Precisa de ajuda? Explore o menu lateral para ver recursos de apoio e dicas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

enum class RegionSafetyState {
    SAFE,
    ATTENTION,
    ALERT
}