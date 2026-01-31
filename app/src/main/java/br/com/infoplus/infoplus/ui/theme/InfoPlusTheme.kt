package br.com.infoplus.infoplus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = InfoPrimary,
    onPrimary = InfoSurfaceLight,
    secondary = InfoPrimary,
    onSecondary = InfoSurfaceLight,
    background = InfoBgLight,
    onBackground = InfoOnLight,
    surface = InfoSurfaceLight,
    onSurface = InfoOnLight,
    surfaceVariant = InfoSurfaceVariantLight,
    onSurfaceVariant = InfoOnLightMuted,
    outline = InfoOutlineLight,
    error = InfoError
)

private val DarkColorScheme = darkColorScheme(
    primary = InfoPrimary,
    onPrimary = InfoOnDark,
    secondary = InfoPrimary,
    onSecondary = InfoOnDark,
    background = InfoBgDark,
    onBackground = InfoOnDark,
    surface = InfoSurfaceDark,
    onSurface = InfoOnDark,
    surfaceVariant = InfoSurfaceVariantDark,
    onSurfaceVariant = InfoOnDarkMuted,
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
