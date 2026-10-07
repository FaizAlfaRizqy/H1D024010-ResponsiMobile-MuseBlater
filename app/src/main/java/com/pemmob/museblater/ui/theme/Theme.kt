package com.pemmob.museblater.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MuseBlaterColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = OnPrimary,
    primaryContainer = OrangePale,
    onPrimaryContainer = OrangeDeep,
    secondary = OrangeSoft,
    onSecondary = OnPrimary,
    secondaryContainer = OrangeLight,
    onSecondaryContainer = OrangeDeep,
    tertiary = OrangeSoft,
    onTertiary = OnPrimary,
    background = BackgroundWhite,
    onBackground = OnBackground,
    surface = SurfaceWarmWhite,
    onSurface = OnSurface,
    surfaceVariant = OrangePale,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = ErrorColor,
    onError = OnPrimary
)

@Composable
fun MuseBlaterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MuseBlaterColorScheme,
        typography = MuseBlaterTypography,
        content = content
    )
}