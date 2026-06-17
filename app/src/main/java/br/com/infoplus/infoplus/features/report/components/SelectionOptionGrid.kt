package br.com.infoplus.infoplus.features.report.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

data class SelectionOptionItem(
    val key: String,
    val label: String,
    val icon: ImageVector? = null
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectionOptionGrid(
    options: List<SelectionOptionItem>,
    selectedKey: String?,
    onSelect: (SelectionOptionItem) -> Unit,
    modifier: Modifier = Modifier,
    minColumnsOnCompact: Int = 2,
    minColumnsOnLarge: Int = 3,
    compactBreakpoint: Int = 520,
    itemHeight: androidx.compose.ui.unit.Dp = 86.dp
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {

        val containerWidth = this.maxWidth

        val spacing = 12.dp

        val columns =
            if (containerWidth > compactBreakpoint.dp) minColumnsOnLarge
            else minColumnsOnCompact

        val itemWidth =
            (containerWidth - spacing * (columns - 1)) / columns

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            maxItemsInEachRow = columns
        ) {
            options.forEach { option ->
                SelectionOptionCard(
                    text = option.label,
                    icon = option.icon,
                    selected = selectedKey == option.key,
                    onClick = { onSelect(option) },
                    modifier = Modifier
                        .width(itemWidth)
                        .height(itemHeight)
                )
            }
        }
    }
}

@Composable
private fun SelectionOptionCard(
    text: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Surface(
        modifier = modifier
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        tonalElevation = if (selected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary else contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    lineHeight = 16.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
