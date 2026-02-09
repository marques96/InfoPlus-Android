package br.com.infoplus.infoplus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = InfoPrimary,
    onPrimary = InfoSurfaceLight,

    secondary = InfoSecondary,
    onSecondary = InfoSurfaceLight,

    tertiary = InfoTertiary,
    onTertiary = InfoSurfaceLight,

    background = InfoBgLight,
    onBackground = InfoOnLight,

    surface = InfoSurfaceLight,
    onSurface = InfoOnLight,

    surfaceVariant = InfoSurfaceVariantLight,
    onSurfaceVariant = InfoOnLightMuted,

    primaryContainer = InfoPrimaryContainerLight,
    onPrimaryContainer = InfoOnPrimaryContainerLight,

    // Navy container = “institucional”, ótimo p/ status offline-first sem ficar policial
    secondaryContainer = InfoSecondaryContainerLight,
    onSecondaryContainer = InfoOnSecondaryContainerLight,

    outline = InfoOutlineLight,
    error = InfoError
)

private val DarkColorScheme = darkColorScheme(
    primary = InfoPrimaryDark,
    onPrimary = InfoOnDark,

    secondary = InfoSecondary,
    onSecondary = InfoOnDark,

    tertiary = InfoTertiary,
    onTertiary = InfoOnDark,

    background = InfoBgDark,
    onBackground = InfoOnDark,

    surface = InfoSurfaceDark,
    onSurface = InfoOnDark,

    surfaceVariant = InfoSurfaceVariantDark,
    onSurfaceVariant = InfoOnDarkMuted,

    primaryContainer = InfoPrimaryContainerDark,
    onPrimaryContainer = InfoOnPrimaryContainerDark,

    secondaryContainer = InfoSecondaryContainerDark,
    onSecondaryContainer = InfoOnSecondaryContainerDark,

    outline = InfoOutlineDark,
    error = InfoError
)

@Composable
fun InfoPlusTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}
