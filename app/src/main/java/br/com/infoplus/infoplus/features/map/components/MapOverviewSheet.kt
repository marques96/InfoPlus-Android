package br.com.infoplus.infoplus.features.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Panorama da sua região",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Acompanhe a segurança ao seu redor em tempo real",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Ocorrências próximas",
                value = nearbyOccurrencesCount.toString(),
                description = when {
                    nearbyOccurrencesCount == 0 -> "Nenhum registro no raio atual"
                    nearbyOccurrencesCount == 1 -> "1 incidente próximo"
                    else -> "$nearbyOccurrencesCount incidentes próximos"
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null
                    )
                },
                containerTint = when {
                    nearbyOccurrencesCount == 0 -> MaterialTheme.colorScheme.surfaceVariant
                    nearbyOccurrencesCount in 1..2 -> Color(0xFFFFF3E0)
                    else -> Color(0xFFFFEBEE)
                }
            )

            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Zonas seguras",
                value = nearbySafeZonesCount.toString(),
                description = when {
                    nearbySafeZonesCount == 0 -> "Nenhuma zona segura próxima"
                    nearbySafeZonesCount == 1 -> "1 zona segura por perto"
                    else -> "$nearbySafeZonesCount zonas seguras por perto"
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.GppGood,
                        contentDescription = null
                    )
                },
                containerTint = Color(0xFFEAF7EE)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        RegionStatusCard(
            state = regionState,
            message = safetyMessage
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Como interpretar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "• Ocorrências próximas representam registros dentro do raio monitorado da sua localização.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Zonas seguras indicam áreas com melhor contexto recente e menor concentração de alertas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Dica",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Arraste esta barra para cima para acompanhar o panorama da região enquanto navega pelo mapa.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

enum class RegionSafetyState {
    SAFE,
    ATTENTION,
    ALERT
}