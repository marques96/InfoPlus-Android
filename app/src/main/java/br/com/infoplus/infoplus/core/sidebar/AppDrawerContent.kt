package br.com.infoplus.infoplus.core.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawerContent(
    selectedRoute: String?,
    onItemClick: (AppDrawerItem) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerTonalElevation = 0.dp,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        DrawerHeader()

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "PRINCIPAL",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
            )

            AppDrawerItem.mainItems.filter { it !is AppDrawerItem.Settings && it !is AppDrawerItem.HelpContent && it !is AppDrawerItem.ExitApp }.forEach { item ->
                DrawerMenuItem(
                    item = item,
                    selected = item.route == selectedRoute,
                    onClick = { onItemClick(item) }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Text(
                text = "SUPORTE E CONFIGURAÇÕES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
            )

            listOf(AppDrawerItem.Settings, AppDrawerItem.HelpContent).forEach { item ->
                DrawerMenuItem(
                    item = item,
                    selected = item.route == selectedRoute,
                    onClick = { onItemClick(item) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            DrawerMenuItem(
                item = AppDrawerItem.ExitApp,
                selected = false,
                onClick = { onItemClick(AppDrawerItem.ExitApp) }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "INFO+",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Conectada e Segura",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    item: AppDrawerItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isExitItem = item is AppDrawerItem.ExitApp

    val backgroundColor = when {
        isExitItem -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)
        selected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val iconContainerColor = when (item) {
        AppDrawerItem.PlanRoute -> Color(0xFFE3F2FD)
        AppDrawerItem.OccurrenceHistory -> Color(0xFFFFF3E0)
        AppDrawerItem.SupportResources -> Color(0xFFE8F5E9)
        AppDrawerItem.CommunitySupport -> Color(0xFFF3E5F5)
        AppDrawerItem.Opportunities -> Color(0xFFE0F2F1)
        AppDrawerItem.Inbox -> Color(0xFFE0F7FA)
        AppDrawerItem.Settings -> Color(0xFFECEFF1)
        AppDrawerItem.HelpContent -> Color(0xFFFFFDE7)
        AppDrawerItem.ExitApp -> Color(0xFFFFEBEE)
    }

    val iconColor = when (item) {
        AppDrawerItem.PlanRoute -> Color(0xFF1976D2)
        AppDrawerItem.OccurrenceHistory -> Color(0xFFF57C00)
        AppDrawerItem.SupportResources -> Color(0xFF388E3C)
        AppDrawerItem.CommunitySupport -> Color(0xFF7B1FA2)
        AppDrawerItem.Opportunities -> Color(0xFF00796B)
        AppDrawerItem.Inbox -> Color(0xFF0097A7)
        AppDrawerItem.Settings -> Color(0xFF455A64)
        AppDrawerItem.HelpContent -> Color(0xFFFBC02D)
        AppDrawerItem.ExitApp -> Color(0xFFD32F2F)
    }

    val contentColor = if (isExitItem) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconContainerColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                fontWeight = fontWeight
            )

            if (selected) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
        }
    }
}
