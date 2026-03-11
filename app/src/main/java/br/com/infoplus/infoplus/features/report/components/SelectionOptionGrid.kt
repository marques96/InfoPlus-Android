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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class SelectionOptionItem(
    val key: String,
    val label: String
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
    itemHeight: androidx.compose.ui.unit.Dp = 76.dp
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
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
    }

    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        tonalElevation = if (selected) 2.dp else 0.dp,
        shadowElevation = if (selected) 4.dp else 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}