package com.jandg.niftytrader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NiftyDarkColors = darkColorScheme(
    primary          = Cyan,
    onPrimary        = BgDeep,
    secondary        = Orange,
    onSecondary      = BgDeep,
    tertiary         = Purple,
    background       = BgDeep,
    onBackground     = TextPrimary,
    surface          = BgCard,
    onSurface        = TextPrimary,
    surfaceVariant   = BgMid,
    onSurfaceVariant = TextSecondary,
    outline          = BgBorder,
    error            = Red,
    onError          = TextPrimary,
)

@Composable
fun NiftyTraderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NiftyDarkColors,
        typography  = NiftyTypography,
        content     = content,
    )
}
