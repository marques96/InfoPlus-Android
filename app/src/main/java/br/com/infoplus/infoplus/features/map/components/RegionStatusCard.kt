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
    val (containerColor, badgeColor, icon) = when (state) {
        RegionSafetyState.SAFE -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), Icons.Default.GppGood)
        RegionSafetyState.ATTENTION -> Triple(Color(0xFFFFF3E0), Color(0xFFF57C00), Icons.Default.Info)
        RegionSafetyState.ALERT -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), Icons.Default.WarningAmber)
    }

    val title = when (state) {
        RegionSafetyState.SAFE -> "Área Estável"
        RegionSafetyState.ATTENTION -> "Atenção Necessária"
        RegionSafetyState.ALERT -> "Alerta de Segurança"
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = badgeColor
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
                )
            }
        }
    }
}